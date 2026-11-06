#!/usr/bin/env python3
"""Scan, classify, verify and safely migrate evidence artifacts.

The evidence directory is a canonical publication area.  Runners should write
pending output below ``.scratch/sunway-supplier-exit-api-first/diagnostic-evidence``
and use the explicit ``promote`` command only after a strict report has passed
the evidence contract and has been accepted by a closure report.
"""
from __future__ import annotations

import argparse
import csv
import datetime as dt
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys
from typing import Any, Dict, Iterable, List, Mapping, Optional, Sequence, Tuple

sys.path.insert(0, str(Path(__file__).resolve().parent))
from evidence_batch import (
    EvidenceBatchError,
    materialize_batch,
    registry_metadata,
    verify_batch,
)
from evidence_output import (
    cleanup_contract_complete,
    current_implementation_binding,
    binding_from_payload,
    route_execution_model_valid,
    source_scope_unchanged,
    strict_case_contract,
)
from source_binding import (
    REPO_ROOT as DISCOVERED_REPO_ROOT,
    feature_root,
    is_standalone_root,
    resolve_workspace_path,
    workspace_root,
)

ROOT = DISCOVERED_REPO_ROOT
EVIDENCE = ROOT / "evidence"
FEATURE = feature_root(DISCOVERED_REPO_ROOT)
WORKSPACE = workspace_root(DISCOVERED_REPO_ROOT)
DIAGNOSTIC = FEATURE / "diagnostic-evidence"
ARCHIVE = FEATURE / "archive"
REGISTRY = EVIDENCE / "evidence-index.tsv"
REPO_PREFIX = "" if is_standalone_root(ROOT) else "lims-recovery/"
EVIDENCE_PREFIX = REPO_PREFIX + "evidence/"
DEFAULT_SCAN = DIAGNOSTIC / "evidence-inventory.tsv"
REGISTRY_FIELDS = (
    "path", "class", "status", "source_commit", "source_dirty",
    "strict_evidence", "ticket", "closure", "sha256", "retention",
    "reason", "referenced_by", "generated_at", "superseded_by",
    "storage", "repository_head_at_capture", "implementation_binding",
    "implementation_tree_digest", "implementation_dirty", "implementation_scope", "artifact_ref",
    "application_binding", "harness_binding", "runtime_contract_binding",
    "planning_binding", "governance_binding", "family_fingerprint",
    "stimulus_fingerprint", "response_fingerprint", "validation_mode",
    "stale_reason", "batch_id",
)
SCAN_FIELDS = REGISTRY_FIELDS + ("tracked", "untracked")
PLAN_FIELDS = (
    "source", "class", "target", "sha256", "referenced_by", "action",
    "reason", "retention", "superseded_by", "generated_at",
)
AUTHORITATIVE_REFERENCE_FILES = (
    "vertical-slice-matrix.tsv",
    "vertical-slice-evidence-status.tsv",
    "function-coverage-catalog.tsv",
    "function-coverage-catalog.md",
    "rebuild-implementation-status.md",
    "vertical-slices-08-14.md",
)
REQUIRED_CLEANUP = (
    "application_stopped", "oracle_stopped", "redis_stopped",
    "network_removed", "volumes_removed", "temporary_files_removed",
)
_CURRENT_BINDING_CACHE: Optional[Dict[str, Any]] = None


def now() -> str:
    return dt.datetime.now(dt.timezone.utc).isoformat()


def current_binding() -> Dict[str, Any]:
    global _CURRENT_BINDING_CACHE
    if _CURRENT_BINDING_CACHE is None:
        _CURRENT_BINDING_CACHE = current_implementation_binding()
    return _CURRENT_BINDING_CACHE


def head() -> str:
    return subprocess.check_output(["git", "-C", str(ROOT), "rev-parse", "HEAD"], text=True).strip()


def tracked_paths() -> set[str]:
    result = subprocess.run(
        ["git", "-C", str(ROOT), "ls-files", "--cached", "--full-name"],
        text=True, capture_output=True, check=True,
    )
    managed_prefixes = (
        EVIDENCE_PREFIX,
        ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/",
        ".scratch/sunway-supplier-exit-api-first/archive/",
    )
    return {
        line.strip() for line in result.stdout.splitlines()
        if line.strip().endswith(".json") and line.strip().startswith(managed_prefixes)
    }


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def generated_date(row: Mapping[str, str], path: Path) -> dt.date:
    """Return the evidence generation date, falling back to filesystem time.

    Reports are often copied between workspaces during a replay.  Using the
    copy mtime for retention therefore made an old failure look new (or a new
    failure look expired).  The registry timestamp is the durable source; the
    filesystem timestamp is only a compatibility fallback for legacy rows.
    """
    value = str(row.get("generated_at", "")).strip()
    if value:
        try:
            return dt.datetime.fromisoformat(value.replace("Z", "+00:00")).date()
        except ValueError:
            pass
    return dt.date.fromtimestamp(path.stat().st_mtime)


def has_reference(value: Any) -> bool:
    """Treat the TSV empty sentinel as empty, not as a real path."""
    return bool(str(value or "").strip() not in {"", "-"})


def json_payload(path: Path) -> Dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeDecodeError, json.JSONDecodeError) as error:
        return {"_parse_error": str(error)}
    return value if isinstance(value, dict) else {"_parse_error": "JSON root is not an object"}


def relative(path: Path) -> str:
    resolved = path.resolve()
    try:
        return resolved.relative_to(ROOT.resolve()).as_posix()
    except ValueError:
        return resolved.relative_to(WORKSPACE.resolve()).as_posix()


def resolve_registry_path(value: str | Path) -> Path:
    """Resolve root-relative or parent-workspace registry paths."""
    return resolve_workspace_path(value, ROOT)


def inside(path: Path, root: Path) -> bool:
    """Return true only when the resolved path is below the resolved root."""
    try:
        path.resolve().relative_to(root.resolve())
    except ValueError:
        return False
    return True


def canonical_closure_reference(closure: Optional[Path]) -> str:
    """Return the publication-root reference for a promoted report."""
    if closure is None:
        return "-"
    target = closure if closure.resolve().is_relative_to(EVIDENCE.resolve()) else EVIDENCE / closure.name
    return relative(target)


def cleanup_complete(value: Any) -> bool:
    if not isinstance(value, Mapping):
        return False
    disposable = all(value.get(key) is True for key in REQUIRED_CLEANUP)
    persistent = (
        value.get("cleanup_mode") == "persistent-oracle"
        and value.get("application_stopped") is True
        and value.get("redis_preserved") is True
        and value.get("oracle_reused") is True
        and value.get("oracle_preserved") is True
        and value.get("baseline_schema_immutable") is True
        and value.get("runtime_schema_restored") is True
        and value.get("runtime_dirty") is False
        and value.get("network_preserved") is True
        and value.get("volume_preserved") is True
        and value.get("temporary_files_removed") is True
    )
    return disposable or persistent


def route_model_valid(value: Any) -> bool:
    return isinstance(value, Mapping) and value.get("boundary_process") is False and value.get("internal_http_bridge") is False


def report_cases(value: Mapping[str, Any]) -> bool:
    return isinstance(value.get("cases"), list) and bool(value.get("cases"))


def current_source_clean(payload: Mapping[str, Any], current: str) -> bool:
    """Validate the implementation binding, with legacy HEAD fallback."""
    current_binding_value = current_binding()
    surfaces = ("application", "harness", "runtime_contract", "planning")
    if all(surface + "_binding" in payload for surface in surfaces):
        return (
            all(
                payload.get(surface + "_binding") == current_binding_value.get(surface + "_binding")
                for surface in surfaces
            )
            and payload.get("implementation_dirty") is False
            and all(current_binding_value.get(surface + "_dirty") is False for surface in surfaces)
        )
    digest = payload.get("implementation_tree_digest")
    if digest:
        declared_scope = set(str(item) for item in payload.get("implementation_scope", ()))
        return (
            digest == current_binding_value["implementation_tree_digest"]
            and binding_from_payload(payload) == current_binding_value["binding_id"]
            and payload.get("implementation_dirty") is False
            and current_binding_value["implementation_dirty"] is False
            and set(current_binding_value["implementation_scope"]).issubset(declared_scope)
        )
    source_commit = str(payload.get("source_commit", ""))
    return source_commit == current and payload.get("source_dirty") is False and source_scope_unchanged(current, payload.get("source_scope", ()))


def closure_reports_strict(value: Mapping[str, Any]) -> bool:
    """Return true only when a closure contains no diagnostic report inputs."""
    reports = value.get("reports")
    if not isinstance(reports, list) or not reports:
        return False
    return all(
        isinstance(item, Mapping)
        and item.get("status") == "executed-pass"
        and item.get("strict_evidence") is True
        and item.get("strict_contract") == "strict report contract valid"
        for item in reports
    )


def closure_reports_current(value: Mapping[str, Any], current: str) -> bool:
    """Require every strict report listed by a closure to share its binding."""
    reports = value.get("reports")
    surfaces = ("application", "harness", "runtime_contract", "planning")
    digest = value.get("implementation_tree_digest")
    binding = value.get("implementation_binding")
    if not isinstance(reports, list) or not reports:
        return False
    for item in reports:
        if not isinstance(item, Mapping):
            return False
        if all(surface + "_binding" in value for surface in surfaces):
            if any(item.get(surface + "_binding") != value.get(surface + "_binding")
                   for surface in surfaces):
                return False
        elif digest:
            if (item.get("implementation_tree_digest") != digest
                    or item.get("implementation_binding") != binding):
                return False
        elif item.get("source_commit") != current:
            return False
        if item.get("implementation_dirty", item.get("source_dirty", False)) is not False:
            return False
    return True


def closure_report_paths_exist(value: Mapping[str, Any], root: Path = ROOT) -> Tuple[bool, str]:
    reports = value.get("reports")
    if not isinstance(reports, list) or not reports:
        return False, "closure has no report index"
    for item in reports:
        if not isinstance(item, Mapping) or not isinstance(item.get("path"), str):
            return False, "closure report index has no path"
        path = Path(str(item["path"]))
        if not path.is_absolute():
            path = resolve_registry_path(path)
        if not path.is_file():
            return False, "closure report path does not exist: " + str(item["path"])
    return True, "closure report paths exist"


def closure_report_registry_valid(value: Mapping[str, Any], registry: Mapping[str, Mapping[str, str]],
                                  current: str) -> Tuple[bool, str]:
    """Validate that a published closure points only at canonical reports."""
    reports = value.get("reports")
    if not isinstance(reports, list) or not reports:
        return False, "closure has no report index"
    for item in reports:
        if not isinstance(item, Mapping) or not isinstance(item.get("path"), str):
            return False, "closure report index has no path"
        path = Path(str(item["path"]))
        if path.is_absolute():
            try:
                rel = path.resolve().relative_to(ROOT.resolve()).as_posix()
            except ValueError:
                return False, "closure report path is outside repository"
        else:
            rel = resolve_registry_path(path)
            try:
                rel = rel.resolve().relative_to(ROOT.resolve()).as_posix()
            except ValueError:
                return False, "closure report path is outside repository"
        row = registry.get(rel)
        if row is None or row.get("class") != "canonical-strict":
            return False, "closure report is not canonical-strict: " + rel
        if row.get("storage") != "git":
            return False, "closure report storage must be storage=git: " + rel
        if row.get("implementation_dirty", row.get("source_dirty")) != "false":
            return False, "closure report is not current clean evidence: " + rel
        if item.get("strict_evidence") is not True or (value.get("implementation_tree_digest") and item.get("implementation_tree_digest") != value.get("implementation_tree_digest")):
            return False, "closure report index has stale or non-strict metadata: " + rel
        closure_digest = value.get("implementation_tree_digest")
        closure_binding = value.get("implementation_binding")
        surfaces = ("application", "harness", "runtime_contract", "planning")
        if all(surface + "_binding" in value for surface in surfaces):
            if any(row.get(surface + "_binding") != value.get(surface + "_binding")
                   for surface in surfaces):
                return False, "registry report dependency-surface binding differs from closure: " + rel
        elif closure_digest:
            if row.get("implementation_tree_digest") != closure_digest:
                return False, "registry report implementation digest differs from closure: " + rel
            if closure_binding and row.get("implementation_binding") != closure_binding:
                return False, "registry report implementation binding differs from closure: " + rel
    return True, "closure report registry is valid"


def strict_closure_contract(value: Mapping[str, Any], current: str,
                            registry: Optional[Mapping[str, Mapping[str, str]]] = None) -> Tuple[bool, str]:
    """Validate a closure before it can authorize report publication.

    A pending closure may still reference pending strict reports, so registry
    membership is optional during per-report promotion.  Final closure
    promotion enables the registry check and therefore closes the publication
    cycle without accepting a diagnostic or stale closure by basename alone.
    """
    if not current_source_clean(value, current):
        return False, "closure source is stale or implementation source is dirty"
    if value.get("implementation_tree_digest"):
        if value.get("implementation_dirty") is not False:
            return False, "closure implementation_dirty is not false"
    elif value.get("source_dirty") is not False:
        return False, "legacy closure source_dirty is not false"
    if not isinstance(value.get("failures"), list) or value.get("failures"):
        return False, "closure contains failures"
    try:
        strict_count = int(value.get("strict_pass_count", 0))
    except (TypeError, ValueError):
        strict_count = 0
    if strict_count <= 0:
        return False, "closure has no strict route passes"
    if not route_model_valid(value.get("route_execution_model")) or not route_execution_model_valid(value.get("route_execution_model")):
        return False, "closure route execution model is missing or declares a bridge/process"
    if not closure_reports_strict(value):
        return False, "closure contains diagnostic or failed report inputs"
    if not closure_reports_current(value, current):
        return False, "closure reports must share the current implementation binding"
    paths_ok, paths_reason = closure_report_paths_exist(value)
    if not paths_ok:
        return False, paths_reason
    if registry is not None:
        registry_ok, registry_reason = closure_report_registry_valid(value, registry, current)
        if not registry_ok:
            return False, registry_reason
    return True, "strict closure contract valid"


def authoritative_reference_violations(registry: Mapping[str, Mapping[str, str]]) -> List[str]:
    """Find stale/diagnostic report references in closure/catalog/handoff files."""
    violations: List[str] = []
    rows = [
        (path, row) for path, row in registry.items()
        if row.get("class") not in {"canonical-strict", "canonical-diagnostic"}
        and ("api-contract-" in path or "api-route-closure-" in path or
             "strict-api-runtime-" in path or "api-fast-" in path)
    ]
    for name in AUTHORITATIVE_REFERENCE_FILES:
        file = EVIDENCE / name
        if not file.is_file():
            continue
        try:
            content = file.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue
        for rel, row in rows:
            if rel in content or Path(rel).name in content:
                try:
                    file_label = relative(file)
                except ValueError:
                    file_label = file.as_posix()
                violations.append(f"{file_label} references {rel} ({row.get('class')})")
    return sorted(set(violations))


def registry_contract_violations(registry: Mapping[str, Mapping[str, str]], current: str) -> List[str]:
    """Validate class semantics before any closure or release gate consumes rows."""
    violations: List[str] = []
    for path, row in registry.items():
        cls = row.get("class", "")
        if cls == "canonical-diagnostic" and row.get("strict_evidence") == "true":
            violations.append(path + ": canonical-diagnostic cannot declare strict_evidence=true")
        if cls == "canonical-strict" and row.get("strict_evidence") != "true" and "api-route-closure" not in path:
            violations.append(path + ": canonical-strict report must declare strict_evidence=true")
        closure = str(row.get("closure", "")).strip()
        if cls == "canonical-strict" and closure and closure != "-":
            closure_row = registry.get(closure)
            if closure_row is None or closure_row.get("class") != "canonical-strict":
                violations.append(path + ": closure is not canonical-strict: " + closure)
            elif row.get("implementation_tree_digest") and closure_row.get("implementation_tree_digest") != row.get("implementation_tree_digest"):
                violations.append(path + ": closure implementation binding differs: " + closure)
            elif closure_row.get("implementation_dirty", closure_row.get("source_dirty")) != "false":
                violations.append(path + ": closure is not current clean evidence: " + closure)
    return violations


def reference_index(paths: Sequence[Path]) -> Dict[str, List[str]]:
    """Find textual references outside JSON evidence payloads."""
    result: Dict[str, List[str]] = {relative(path): [] for path in paths}
    needles: Dict[str, List[str]] = {}
    for path in paths:
        needles.setdefault(path.name, []).append(relative(path))
    # Search only files that mention an evidence-shaped token first.  The
    # previous all-files x all-names scan made a 600-file inventory needlessly
    # quadratic and looked like a hung runner on this repository.
    pattern = "|".join(("api-contract-", "api-route-closure-", "strict-api-runtime-", "api-fast-"))
    # Limit the reference walk to the recovery project and this feature's
    # tracker.  The repository also contains large tool/plugin caches with
    # unrelated JSON; scanning those made every lifecycle pass needlessly
    # expensive while never producing a valid evidence reference.
    search = subprocess.run(
        ["rg", "-l", "--hidden", "--glob", "!.git/**",
         "--glob", "!" + EVIDENCE_PREFIX + "**",
         "--glob", "!.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/**",
         "--glob", "!.scratch/sunway-supplier-exit-api-first/archive/**",
         "--glob", "!diagnostic-evidence/**",
         "--glob", "!archive/**",
        pattern, str(ROOT), str(FEATURE)],
        text=True, capture_output=True, check=False,
    )
    candidates = [Path(line) for line in search.stdout.splitlines() if line.strip()]
    # Extract path-like evidence tokens in one linear pass.  Building a
    # giant alternation from every historical filename becomes quadratic in
    # memory and CPU once the parent workspace contains thousands of reports.
    token_pattern = re.compile(
        r"(?<![A-Za-z0-9_.-])(?:[A-Za-z0-9_.-]+/)*[A-Za-z0-9_.-]+\.(?:json|tsv|md)(?![A-Za-z0-9_.-])"
    )
    for source in candidates:
        try:
            text = source.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue
        if not needles:
            continue
        for token in token_pattern.findall(text):
            targets = needles.get(Path(token).name)
            if not targets:
                continue
            if len(targets) == 1:
                result[targets[0]].append(relative(source))
                continue
            # A basename shared by several wave snapshots is not enough to
            # establish a reference.  Require the full repository-relative
            # path (or the path relative to lims-recovery) so a generic
            # command example cannot pin every historical copy in place.
            for target in targets:
                short = target.removeprefix(REPO_PREFIX)
                if target in token or short in token:
                    result[target].append(relative(source))
    # Closure JSON is itself an authoritative reference graph and is excluded
    # from the text search above to avoid counting every embedded observation.
    # Walk its strings explicitly so reports accepted by closure can become
    # canonical-strict.
    names = {path.name: relative(path) for path in paths}
    exact_targets = {relative(path): relative(path) for path in paths}
    unique_names = {name: targets[0] for name, targets in needles.items() if len(targets) == 1}
    for source in paths:
        # Only canonical-root closures are authoritative references.  Walking
        # every historical/pending closure recursively re-parses hundreds of
        # multi-megabyte cumulative route snapshots and is both unnecessary
        # and prohibitively expensive.
        if "closure" not in source.name or not inside(source, EVIDENCE):
            continue
        payload = json_payload(source)
        closure_rel = relative(source)
        stack: List[Any] = [payload]
        while stack:
            value = stack.pop()
            if isinstance(value, Mapping):
                stack.extend(value.values())
            elif isinstance(value, list):
                stack.extend(value)
            elif isinstance(value, str):
                target = exact_targets.get(value) or names.get(value)
                if target is None and "/" in value:
                    target = unique_names.get(Path(value).name)
                if target is not None:
                    result[target].append(closure_rel)
    # A closure can mention a report once per case.  The registry describes
    # references, rather than observation multiplicity, so keep one entry per
    # referring file.  This makes the index stable and keeps archive/prune
    # decisions readable even for large route closures.
    return {path: sorted(set(references)) for path, references in result.items()}


def ticket_for(path: Path, payload: Mapping[str, Any]) -> str:
    for key in ("ticket", "issue", "ticket_id"):
        if payload.get(key):
            return str(payload[key])
    name = path.name.lower()
    if "fast" in name:
        return "09/13"
    if "full-read" in name:
        return "09/13"
    if "strict-api-runtime" in name:
        return "08"
    if any(token in name for token in ("personnel", "equipment", "consumable", "material")):
        return "11"
    if any(token in name for token in (
        "secondary", "report", "attachment", "scheduler", "erp",
        "core", "result", "order", "project", "task", "integration", "page-other",
    )):
        return "12"
    if any(token in name for token in ("foundation", "identity", "workspace", "boundary")):
        return "10"
    if "closure" in name:
        return "13"
    return "unknown"


def duplicate_primary(paths: Sequence[str]) -> str:
    """Choose one stable primary for identical artifacts.

    Canonical publication wins when present.  Otherwise an archived historical
    copy is the durable audit record and pending diagnostic copies are marked
    as superseded.  The deterministic tie-breaker keeps repeated scans from
    creating circular ``superseded_by`` links.
    """
    def rank(value: str) -> Tuple[int, int, int, str]:
        return (
            0 if value.startswith(EVIDENCE_PREFIX) else 1,
            0 if "/archive/" in value else 1,
            1 if "/pending/" in value else 0,
            value,
        )
    return min(paths, key=rank)


def storage_for(path: Path) -> str:
    try:
        if path.resolve().is_relative_to(EVIDENCE.resolve()):
            return "git"
        if path.resolve().is_relative_to(ARCHIVE.resolve()):
            return "git" if relative(path) in tracked_paths() else "artifact"
        if path.resolve().is_relative_to(DIAGNOSTIC.resolve()):
            return "local"
    except ValueError:
        pass
    return "local"


def classify(path: Path, payload: Mapping[str, Any], refs: Sequence[str],
             duplicates: Mapping[str, Sequence[str]], current_head: str,
             registered_class: str = "") -> Dict[str, str]:
    rel = relative(path)
    source_commit = str(payload.get("source_commit", "unknown"))
    source_dirty = payload.get("implementation_dirty", payload.get("source_dirty"))
    binding = binding_from_payload(payload)
    digest = payload.get("implementation_tree_digest", "")
    current_binding_value = current_binding()
    binding_current = bool(digest) and digest == current_binding_value["implementation_tree_digest"] and binding == current_binding_value["binding_id"]
    strict = payload.get("strict_evidence") is True
    status = str(payload.get("status", "unknown"))
    execution = str(payload.get("execution", ""))
    cleanup = payload.get("cleanup")
    closure_refs = [ref for ref in refs if "api-route-closure" in ref or "closure" in ref]
    # A report may be included in several cumulative checkpoints.  Keep the
    # canonical root closure with the greatest filename (the latest named
    # cumulative checkpoint) as its registry binding; older diagnostic
    # closures remain available in the archive but must not win by accident.
    canonical_closures = [ref for ref in closure_refs if ref.startswith(EVIDENCE_PREFIX)]
    closure = max(canonical_closures, key=lambda ref: (len(ref), ref)) if canonical_closures else (
        max(closure_refs, key=lambda ref: (len(ref), ref)) if closure_refs else ""
    )
    runtime = path.name.startswith("strict-api-runtime-") or execution == "clone-ready-unverified"
    is_closure = "api-route-closure" in path.name
    is_root = path.resolve().is_relative_to(EVIDENCE.resolve())
    source_current = binding_current or (not digest and source_commit == current_head)
    canonical_authorized = registered_class in {"canonical-strict", "canonical-diagnostic"}
    # A previously registered root report may be rebound after a source
    # binding migration.  Do not force a manual registry edit just because
    # its old class was historical/failed; the current binding and strict
    # contract below are the authority.  An entirely unregistered root JSON
    # is still rejected, so ordinary runners cannot publish by accident.
    reclassifiable_root = (
        is_root
        and registered_class in {"historical-superseded", "diagnostic-failed"}
        and binding_current
    )
    if is_root and not (canonical_authorized or reclassifiable_root):
        cls = "diagnostic-failed"
        reason = "canonical root JSON is not explicitly promoted in the evidence registry"
        retention = "180d"
    elif is_root and not source_current:
        cls = "historical-superseded"
        reason = ("implementation binding does not match current implementation; "
                  "canonical evidence must be rebound and replayed") if digest else (
                      "legacy source_commit does not match current HEAD; canonical evidence "
                      "must be migrated or replayed")
        retention = "permanent-archive"
    elif is_root and source_dirty is not False:
        cls = "diagnostic-failed"
        reason = "canonical root JSON has source_dirty=true or an unknown dirty state"
        retention = "180d"
    elif is_closure and not source_current:
        cls = "historical-superseded"
        reason = ("closure implementation binding is stale; retained only for historical audit"
                  if digest else "legacy closure source_commit is stale; retained only for historical audit")
        retention = "permanent-archive"
    elif is_closure and not path.resolve().is_relative_to(EVIDENCE.resolve()):
        cls = "diagnostic-failed"
        reason = "strict closure is pending explicit canonical promotion"
        retention = "180d"
    elif is_closure and source_current and source_dirty is False:
        failures = payload.get("failures")
        if payload.get("strict_pass_count", 0) > 0 and isinstance(failures, list) and not failures:
            cls = "canonical-strict"
            reason = "current clean closure with strict route passes and no failures"
            retention = "permanent"
        elif payload.get("diagnostic_observation_count", 0) > 0:
            cls = "canonical-diagnostic"
            reason = "current clean diagnostic closure; no strict promotion"
            retention = "90d"
        else:
            cls = "diagnostic-failed"
            reason = "closure has no accepted strict or diagnostic observations"
            retention = "180d"
    elif runtime:
        cls = "runtime-lifecycle"
        reason = "clone-ready or runtime lifecycle manifest; cleanup is finalized by the dependent contract report"
        retention = "30d"
    elif "_parse_error" in payload:
        cls = "diagnostic-failed"
        reason = "invalid or unreadable JSON"
        retention = "180d"
    elif status in {"executed-fail", "failed", "error", "aborted"}:
        cls = "diagnostic-failed"
        reason = "execution did not pass"
        retention = "180d"
    elif "evidence-lifecycle-migration" in path.name:
        cls = "diagnostic-failed"
        reason = "governance migration audit; not behavior evidence"
        retention = "180d"
    elif strict:
        case_ok, _ = strict_case_contract(payload)
        if not source_current:
            cls = "historical-superseded"
            reason = ("strict report implementation binding is stale"
                      if digest else "legacy strict report source_commit does not match current HEAD")
            retention = "permanent-archive"
        elif status == "executed-pass" and source_dirty is False and payload.get("production_access") is False and cleanup_complete(cleanup) and route_model_valid(payload.get("route_execution_model")) and route_execution_model_valid(payload.get("route_execution_model")) and report_cases(payload) and case_ok:
            if not path.resolve().is_relative_to(EVIDENCE.resolve()):
                cls = "diagnostic-failed"
                reason = "strict report is pending explicit canonical promotion"
                retention = "180d"
            elif closure:
                cls = "canonical-strict"
                reason = "strict HTTP report has clean source, complete cleanup and closure reference"
                retention = "permanent"
            else:
                cls = "diagnostic-failed"
                reason = "strict report is not referenced by a closure report"
                retention = "180d"
        else:
            cls = "diagnostic-failed"
            reason = "strict contract incomplete: source, HTTP, route model, status or cleanup check failed"
            retention = "180d"
    elif execution == "fast-differential" or status.startswith("diagnostic"):
        if source_dirty is False and source_current:
            cls = "canonical-diagnostic"
            reason = "current clean diagnostic screening; never strict evidence"
            retention = "90d"
        else:
            cls = "historical-superseded"
            reason = "diagnostic report is bound to an older source or dirty checkout"
            retention = "permanent-archive"
    elif source_dirty is False and source_current and any(token in path.name for token in ("inventory", "ledger", "execution-plan", "skeleton", "registry")):
        if source_scope_unchanged(current_head, payload.get("source_scope", ())):
            cls = "canonical-diagnostic"
            reason = "current clean planning/catalog evidence; not a strict behavior pass"
            retention = "90d"
        else:
            cls = "historical-superseded"
            reason = "planning/catalog payload claimed a clean source, but the current implementation scope is dirty"
            retention = "permanent-archive"
    elif source_commit == current_head and source_dirty is False and not source_scope_unchanged(current_head, payload.get("source_scope", ())):
        cls = "historical-superseded"
        reason = "report claimed source_dirty=false, but the current implementation scope is dirty"
        retention = "permanent-archive"
    elif source_commit != current_head or source_dirty is not False:
        cls = "historical-superseded"
        reason = "source binding is stale or dirty"
        retention = "permanent-archive"
    else:
        cls = "canonical-diagnostic" if refs else "duplicate-or-unreferenced"
        reason = "non-strict evidence retained for an explicit reference" if refs else "no valid reference"
        retention = "90d" if refs else "180d"

    superseded_by = ""
    same_hash = list(duplicates.get(sha256(path), ()))
    primary = duplicate_primary(same_hash) if same_hash else ""
    if len(same_hash) > 1 and cls not in {"canonical-strict", "canonical-diagnostic", "historical-superseded"} and not (
        is_root and registered_class == "canonical-strict"
    ):
        cls = "duplicate-or-unreferenced"
        reason = "duplicate SHA-256; canonical or first registered copy is retained"
        superseded_by = primary if rel != primary else ""
        retention = "180d"
    elif len(same_hash) > 1 and cls == "historical-superseded":
        superseded_by = primary if rel != primary else ""
    elif len(same_hash) > 1 and cls == "duplicate-or-unreferenced":
        superseded_by = primary if rel != primary else ""
    return {
        **registry_metadata(payload),
        "path": rel,
        "class": cls,
        "status": status,
        "source_commit": source_commit,
        "source_dirty": "true" if source_dirty is True else "false" if source_dirty is False else "unknown",
        "repository_head_at_capture": str(payload.get("repository_head_at_capture", source_commit)),
        "implementation_binding": binding or "",
        "implementation_tree_digest": str(payload.get("implementation_tree_digest", "")),
        "implementation_dirty": "true" if payload.get("implementation_dirty") is True else "false" if payload.get("implementation_dirty") is False else "unknown",
        "implementation_scope": ";".join(str(item) for item in payload.get("implementation_scope", ())),
        "strict_evidence": "true" if strict else "false",
        "ticket": ticket_for(path, payload),
        "closure": closure,
        "sha256": sha256(path),
        "retention": retention,
        "reason": reason,
        "referenced_by": ";".join(sorted(refs)),
        "generated_at": str(payload.get("generated_at") or payload.get("finished_at") or payload.get("created_at") or payload.get("observed_at") or now()),
        "superseded_by": superseded_by,
        "storage": storage_for(path),
        "artifact_ref": str(payload.get("artifact_ref", "")),
    }


def scan_rows() -> List[Dict[str, str]]:
    paths = sorted(set(EVIDENCE.glob("*.json"))
                   | set(DIAGNOSTIC.rglob("*.json"))
                   | set(ARCHIVE.rglob("*.json")))
    tracked = tracked_paths()
    hashes: Dict[str, List[str]] = {}
    for path in paths:
        hashes.setdefault(sha256(path), []).append(relative(path))
    refs = reference_index(paths)
    current = head()
    registered_rows = {row.get("path", ""): row for row in load_registry()}
    registered = {path: row.get("class", "") for path, row in registered_rows.items()}
    rows = []
    for path in paths:
        # Read one payload at a time.  The diagnostic tree contains several
        # multi-megabyte cumulative route reports; retaining every decoded JSON
        # object here made a full lifecycle scan grow without a bound and could
        # be killed before it produced a registry.  Hashes and references are
        # already indexed above, so per-file classification is independent.
        payload = json_payload(path)
        row = classify(path, payload, refs.get(relative(path), []), hashes, current,
                       registered.get(relative(path), ""))
        row["tracked"] = "true" if relative(path) in tracked else "false"
        row["untracked"] = "false" if row["tracked"] == "true" else "true"
        row["referenced_by"] = ";".join(sorted(set(refs.get(relative(path), []))))
        # Prefer the report's own timestamp.  For legacy payloads that have no
        # timestamp, preserve the prior registry value so repeated scans are
        # idempotent and do not create bookkeeping-only diffs.
        if not any(payload.get(key) for key in ("generated_at", "finished_at", "created_at", "observed_at")):
            prior = registered_rows.get(relative(path), {})
            if prior.get("generated_at"):
                row["generated_at"] = prior["generated_at"]
        rows.append(row)
    return rows


def write_tsv(path: Path, rows: Sequence[Mapping[str, str]], fields: Sequence[str]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fields, delimiter="\t", extrasaction="ignore", lineterminator="\n")
        writer.writeheader()
        for row in rows:
            normalized = dict(row)
            if "superseded_by" in fields and not normalized.get("superseded_by"):
                normalized["superseded_by"] = "-"
            # Keep TSV rows free of trailing tabs so ``git diff --check`` is a
            # useful gate even when newly-added lifecycle columns are empty for
            # historical rows.
            for field in fields:
                if not normalized.get(field):
                    normalized[field] = "-"
            writer.writerow(normalized)


def load_registry(path: Optional[Path] = None) -> List[Dict[str, str]]:
    path = path or REGISTRY
    if not path.is_file():
        return []
    with path.open(encoding="utf-8", newline="") as handle:
        return list(csv.DictReader(handle, delimiter="\t"))


def canonical_registry_rows(rows: Sequence[Mapping[str, str]]) -> List[Dict[str, str]]:
    """Return only publication-root rows suitable for the standalone registry.

    Diagnostic and historical files are deliberately outside the deploy tree
    in the parent workspace.  They may be included in an inventory or a
    migration plan, but must never be copied into the standalone registry:
    their ``.scratch/...`` paths do not exist in a clean standalone clone.
    """
    result: List[Dict[str, str]] = []
    for row in rows:
        raw = str(row.get("path", ""))
        if not raw.endswith(".json"):
            continue
        path = resolve_registry_path(raw)
        if not inside(path, EVIDENCE):
            continue
        # Evidence-owned support directories have their own manifests.  The
        # publication registry is intentionally limited to root reports.
        if path.parent.resolve() != EVIDENCE.resolve():
            continue
        if row.get("class") not in {"canonical-strict", "canonical-diagnostic"}:
            continue
        result.append(dict(row))
    return result


def planned_destination(row: Mapping[str, str], occupied: set[str], demote_canonical: bool = False) -> Tuple[str, str]:
    """Return the reviewed destination and action for one registry row.

    The plan only proposes moves for JSON that escaped into the publication
    root.  Diagnostic and archive trees are already lifecycle-managed and are
    retained in place.  A hash suffix is added for every destination collision
    so a reviewed plan can never overwrite an existing artifact.
    """
    source = str(row.get("path", ""))
    source_path = resolve_registry_path(source)
    if not source_path.is_file() or not source.endswith(".json"):
        return "", "retain-missing-or-non-json"
    if not inside(source_path, EVIDENCE):
        return source, "retain-managed-tree"
    cls = str(row.get("class", ""))
    if cls in {"canonical-strict", "canonical-diagnostic"} and not demote_canonical:
        return source, "keep-canonical"
    if cls in {"canonical-strict", "canonical-diagnostic"} and demote_canonical:
        cls = "historical-superseded"
    name = source_path.name
    if cls == "runtime-lifecycle":
        base = DIAGNOSTIC / "runtime" / name
        reason = "runtime lifecycle report must stay outside canonical evidence"
    elif cls == "diagnostic-failed":
        base = DIAGNOSTIC / "failed" / name
        reason = "failed or incomplete report must stay diagnostic"
    elif cls == "duplicate-or-unreferenced":
        base = ARCHIVE / "duplicates" / name
        reason = "duplicate or unreferenced report is retained outside canonical evidence"
    else:
        base = ARCHIVE / "historical" / name
        reason = "stale or superseded report is retained as historical evidence"
    candidate = base
    digest = str(row.get("sha256", ""))[:12] or sha256(source_path)[:12]
    index = 1
    while relative(candidate) in occupied or candidate.exists():
        suffix = "--" + digest + ("-" + str(index) if index > 1 else "")
        candidate = base.with_name(base.stem + suffix + base.suffix)
        index += 1
    occupied.add(relative(candidate))
    action = "move-to-diagnostic" if base.is_relative_to(DIAGNOSTIC) else "move-to-archive"
    return relative(candidate), action + ":" + reason


def cmd_plan(args: argparse.Namespace) -> int:
    """Write a deterministic, reviewable migration plan without mutating files."""
    rows = scan_rows()
    output = Path(args.output) if args.output else DIAGNOSTIC / "evidence-migration-plan-current.tsv"
    if not output.is_absolute():
        output = resolve_registry_path(output)
    occupied = {relative(path) for path in EVIDENCE.rglob("*.json")}
    occupied.update(relative(path) for path in DIAGNOSTIC.rglob("*.json"))
    occupied.update(relative(path) for path in ARCHIVE.rglob("*.json"))
    plan: List[Dict[str, str]] = []
    for row in rows:
        target, action_reason = planned_destination(row, occupied, getattr(args, "demote_canonical", False))
        if not target:
            action, reason = "retain-missing-or-non-json", "file is already absent or is not JSON"
        elif ":" in action_reason:
            action, reason = action_reason.split(":", 1)
        else:
            action, reason = action_reason, "file is already in a managed lifecycle tree"
        plan.append({
            "source": row["path"],
            "class": row["class"],
            "target": target,
            "sha256": row["sha256"],
            "referenced_by": row.get("referenced_by", ""),
            "action": action,
            "reason": reason,
            "retention": row.get("retention", ""),
            "superseded_by": row.get("superseded_by", ""),
            "generated_at": row.get("generated_at", ""),
        })
    write_tsv(output, plan, PLAN_FIELDS)
    counts: Dict[str, int] = {}
    for item in plan:
        counts[item["action"]] = counts.get(item["action"], 0) + 1
    print(json.dumps({
        "status": "planned",
        "source_commit": head(),
        "demote_canonical": bool(getattr(args, "demote_canonical", False)),
        "rows": len(plan),
        "output": relative(output) if output.is_relative_to(ROOT) else str(output),
        "actions": counts,
        "mutated": False,
    }, ensure_ascii=False, sort_keys=True))
    return 0


def strict_contract(row: Mapping[str, str], payload: Mapping[str, Any], current: str) -> Tuple[bool, str]:
    # A migrated strict report is bound to the implementation digest.  The
    # complete repository HEAD is provenance only.  Legacy reports without a
    # digest retain the old exact-HEAD contract until explicitly migrated.
    if payload.get("implementation_tree_digest"):
        if not current_source_clean(payload, current):
            return False, "implementation binding is stale or dirty"
    else:
        if row.get("source_commit") != current:
            return False, "legacy source_commit is not the current HEAD; migration or replay is required"
        if row.get("source_dirty") != "false":
            return False, "legacy source_dirty is not false"
    if not current_source_clean(payload, current):
        return False, "current implementation source is dirty or not bound to the implementation"
    if row.get("strict_evidence") != "true" or payload.get("status") != "executed-pass":
        return False, "report is not executed strict evidence"
    if payload.get("production_access") is not False:
        return False, "production_access is not false"
    if not cleanup_complete(payload.get("cleanup")) or not cleanup_contract_complete(payload.get("cleanup")):
        return False, "cleanup contract is incomplete"
    if not route_model_valid(payload.get("route_execution_model")) or not route_execution_model_valid(payload.get("route_execution_model")):
        return False, "route execution model is missing or declares a bridge/process"
    if not report_cases(payload):
        return False, "report has no HTTP cases"
    cases_ok, cases_reason = strict_case_contract(payload)
    if not cases_ok:
        return False, cases_reason
    return True, "strict contract valid"


def cmd_scan(args: argparse.Namespace) -> int:
    rows = scan_rows()
    output = Path(args.output) if args.output else DEFAULT_SCAN
    if not output.is_absolute():
        output = resolve_registry_path(output)
    write_tsv(output, rows, SCAN_FIELDS)
    counts: Dict[str, int] = {}
    for row in rows:
        counts[row["class"]] = counts.get(row["class"], 0) + 1
    print(json.dumps({
        "rows": len(rows),
        "output": relative(output) if output.is_relative_to(ROOT) else str(output),
        "tracked": sum(row.get("tracked") == "true" for row in rows),
        "untracked": sum(row.get("untracked") == "true" for row in rows),
        "classes": counts,
    }, ensure_ascii=False, sort_keys=True))
    return 0


def cmd_classify(args: argparse.Namespace) -> int:
    rows = scan_rows()
    # The standalone registry is a publication index, not the parent
    # workspace's historical/diagnostic ledger.  Keep existing canonical rows,
    # refresh only canonical root JSON, and do not create an empty registry in
    # a clean checkout that has never promoted evidence.
    existing = {
        row.get("path", ""): row
        for row in canonical_registry_rows(load_registry())
    }
    for row in canonical_registry_rows(rows):
        existing[row["path"]] = {key: row.get(key, "") for key in REGISTRY_FIELDS}
    ordered = [existing[key] for key in sorted(existing)]
    if ordered or REGISTRY.is_file():
        write_tsv(REGISTRY, ordered, REGISTRY_FIELDS)
        registry_display = relative(REGISTRY)
    else:
        registry_display = None
    print(json.dumps({
        "registry": registry_display,
        "rows": len(ordered),
        "scanned_root": len(canonical_registry_rows(rows)),
        "ignored_external_rows": len(rows) - len(canonical_registry_rows(rows)),
    }, ensure_ascii=False))
    return 0


def cmd_verify(args: argparse.Namespace) -> int:
    rows = load_registry()
    if not rows:
        print(json.dumps({"status": "pass", "registered": 0, "canonical_strict": 0, "canonical_diagnostic": 0}, ensure_ascii=False))
        return 0
    by_path = {row.get("path", ""): row for row in rows}
    # The lifecycle registry governs the publication root.  Evidence-owned
    # subtrees such as framework-contract/ and license-removal/ have their own
    # manifests and are intentionally outside this report registry.
    # Pending diagnostic and archive outputs are intentionally generated in
    # bulk and are not registry publication events. Only canonical-root JSON
    # must be registered before it can be consumed as evidence.
    actual = {relative(path) for path in EVIDENCE.glob("*.json")}
    # Local diagnostic artifacts may be absent in a clean checkout; their
    # registry row remains useful for retention/provenance but they cannot be
    # consumed by strict closure. Git canonical files must exist, while
    # artifact references are verified by digest/reference metadata offline.
    missing = sorted(path for path, row in by_path.items()
                     if not resolve_registry_path(path).is_file()
                     and row.get("storage", "git") not in {"artifact", "local"})
    unregistered = sorted(actual - set(by_path))
    invalid = []
    current = head()
    for path, row in by_path.items():
        file = resolve_registry_path(path)
        if not file.is_file():
            continue
        if not inside(file, ROOT) and not inside(file, WORKSPACE):
            invalid.append(f"{path}: registry path escapes repository root")
            continue
        is_root_json = path.startswith(EVIDENCE_PREFIX) and path.endswith(".json")
        if is_root_json and row.get("class") not in {"canonical-strict", "canonical-diagnostic"}:
            invalid.append(f"{path}: canonical root JSON is not canonical class")
        payload = json_payload(file)
        if row.get("class") in {"canonical-strict", "canonical-diagnostic"}:
            if row.get("storage") != "git":
                invalid.append(f"{path}: canonical class must use storage=git")
            if row.get("implementation_dirty") != "false":
                invalid.append(f"{path}: canonical class implementation_dirty is not false")
            for surface in ("application", "harness", "runtime_contract", "planning"):
                field = surface + "_binding"
                if payload.get(field) and row.get(field) != payload.get(field):
                    invalid.append(f"{path}: canonical class {field} differs from payload")
            if not current_source_clean(payload, current):
                invalid.append(f"{path}: canonical class implementation source is not clean/current")
        if row.get("class") == "canonical-diagnostic" and payload.get("strict_evidence") is True:
            invalid.append(f"{path}: canonical-diagnostic payload cannot claim strict_evidence=true")
        if is_root_json and row.get("source_dirty") != "false":
            invalid.append(f"{path}: source_dirty is not false")
        if is_root_json and not current_source_clean(payload, current):
            invalid.append(f"{path}: implementation source is dirty or not bound to current dependency surfaces")
        if has_reference(row.get("referenced_by")):
            for reference in str(row.get("referenced_by")).split(";"):
                if reference and reference != "-":
                    reference_path = resolve_registry_path(reference)
                    if (not inside(reference_path, ROOT)
                            and not inside(reference_path, WORKSPACE)) or not reference_path.is_file():
                        invalid.append(f"{path}: referenced_by path does not exist: {reference}")
        superseded = str(row.get("superseded_by", ""))
        if superseded and superseded != "-":
            superseded_path = resolve_registry_path(superseded)
            if (not inside(superseded_path, ROOT)
                    and not inside(superseded_path, WORKSPACE)) or not superseded_path.is_file():
                invalid.append(f"{path}: superseded_by path does not exist: {superseded}")
        if row.get("class") == "canonical-strict":
            if "api-route-closure" in file.name:
                ok = (current_source_clean(payload, current)
                      and isinstance(payload.get("failures"), list) and not payload.get("failures")
                      and int(payload.get("strict_pass_count", 0)) > 0
                      and closure_reports_strict(payload)
                      and closure_reports_current(payload, current)
                      and route_model_valid(payload.get("route_execution_model")))
                if ok:
                    ok, _ = closure_report_paths_exist(payload)
                if ok:
                    ok, _ = closure_report_registry_valid(payload, by_path, current)
                reason = "closure contract valid" if ok else "closure source/pass/failure contract invalid"
            else:
                ok, reason = strict_contract(row, payload, current)
            if not ok:
                invalid.append(f"{path}: {reason}")
        if row.get("sha256") != sha256(file):
            invalid.append(f"{path}: sha256 mismatch")
    # The canonical root may contain markdown/TSV contracts, but every JSON
    # artifact in it must be in the registry.  This catches a runner that
    # bypasses the output-path guard or leaves a new unregistered report in a
    # nested canonical directory.
    root_json = {relative(path) for path in EVIDENCE.glob("*.json")}
    root_unregistered = sorted(root_json - set(by_path))
    if root_unregistered:
        unregistered.extend(root_unregistered)
    invalid.extend(authoritative_reference_violations(by_path))
    invalid.extend(registry_contract_violations(by_path, current))
    batch_root = FEATURE / "evidence-batches"
    if batch_root.is_dir():
        for batch_manifest in sorted(batch_root.rglob("batch.json")):
            try:
                verify_batch(batch_manifest)
            except (EvidenceBatchError, OSError, json.JSONDecodeError) as error:
                invalid.append(f"{batch_manifest}: immutable batch verification failed: {error}")
    if missing or unregistered or invalid:
        print(json.dumps({"missing": missing, "unregistered": unregistered, "invalid": invalid}, ensure_ascii=False, indent=2))
        return 1
    print(json.dumps({"status": "pass", "registered": len(rows), "canonical_strict": sum(row.get("class") == "canonical-strict" for row in rows), "canonical_diagnostic": sum(row.get("class") == "canonical-diagnostic" for row in rows)}, ensure_ascii=False))
    return 0


def cmd_promote(args: argparse.Namespace) -> int:
    source = Path(args.report)
    if not source.is_absolute():
        source = resolve_registry_path(source)
    target = Path(args.output) if args.output else EVIDENCE / source.name
    if not target.is_absolute():
        target = resolve_registry_path(target)
    if not source.is_file():
        raise SystemExit(f"report not found: {source}")
    if source.resolve().is_relative_to(EVIDENCE.resolve()):
        raise SystemExit("promotion source must be pending/diagnostic; canonical root is publication-only")
    if not inside(target, EVIDENCE):
        raise SystemExit("promotion target must be inside the canonical evidence root")
    is_closure = "api-route-closure" in source.name
    if not is_closure and target.name != source.name:
        raise SystemExit("promotion rejected: report target basename must match the source basename so closure references remain exact")
    payload = json_payload(source)
    current = head()
    closure = None
    registry_rows = {row.get("path", ""): row for row in load_registry()}
    if is_closure:
        closure_ok, closure_reason = strict_closure_contract(payload, current)
        if not closure_ok:
            raise SystemExit("promotion rejected: " + closure_reason)
        for item in payload.get("reports", []):
            raw = str(item.get("path", ""))
            candidate = raw if raw.startswith(EVIDENCE_PREFIX) else (
                EVIDENCE_PREFIX + Path(raw).name
            )
            report_row = registry_rows.get(candidate)
            report_file = resolve_registry_path(candidate)
            if (report_row is None or report_row.get("class") != "canonical-strict"
                    or not report_file.is_file()):
                raise SystemExit(
                    "promotion rejected: closure report must be promoted first: " + raw
                )
        closure_payload = payload
        referenced = True
        row = {"source_commit": str(payload.get("source_commit", "")), "source_dirty": "false", "strict_evidence": "true"}
    else:
        row = {"source_commit": str(payload.get("source_commit", "")), "source_dirty": "false" if payload.get("source_dirty") is False else "true", "strict_evidence": "true" if payload.get("strict_evidence") is True else "false"}
    ok, reason = ((True, "closure contract valid") if is_closure
                  else strict_contract(row, payload, current))
    if not ok:
        raise SystemExit("promotion rejected: " + reason)
    if not is_closure:
        closure = Path(args.closure) if args.closure else None
        if closure and not closure.is_absolute():
            closure = resolve_registry_path(closure)
    if not is_closure and (not closure or not closure.is_file()):
        raise SystemExit("promotion requires --closure pointing to a finalized closure report")
    # Reports are promoted before their closure so the closure can validate
    # the canonical report paths.  Keep the registry reference canonical even
    # when the caller supplied the pending/diagnostic closure path; otherwise
    # a successful promotion leaves a registry row pointing outside the
    # publication root and the next verify pass rejects it.
    closure_reference = canonical_closure_reference(closure) if not is_closure else "-"
    if not is_closure:
        closure_payload = json_payload(closure)
        closure_ok, closure_reason = strict_closure_contract(closure_payload, current)
        if not closure_ok:
            raise SystemExit("promotion rejected: " + closure_reason)
        try:
            source_reference = relative(source)
        except ValueError:
            # Diagnostic/pending evidence intentionally lives outside the
            # standalone repository root.  Closure generators may retain an
            # absolute provenance path for that input; basename matching keeps
            # promotion deterministic without pretending the pending path is
            # already canonical.
            source_reference = ""
        referenced = any(
            str(item.get("path", "")) == source_reference
            or Path(str(item.get("path", ""))).name == source.name
            for item in closure_payload.get("reports", [])
            if isinstance(item, Mapping)
        )
    if not referenced:
        raise SystemExit("promotion rejected: report is not referenced by closure")
    batch_info = None
    if not is_closure and int(payload.get("schema", 1)) >= 2:
        milestone_id = str(getattr(args, "milestone", "") or payload.get("milestone_id", ""))
        if not milestone_id:
            raise SystemExit("schema-2 promotion requires --milestone or report milestone_id")
        try:
            batch_info = materialize_batch(
                payload,
                source,
                FEATURE / "evidence-batches",
                milestone_id=milestone_id,
            )
        except EvidenceBatchError as error:
            raise SystemExit("promotion rejected: " + str(error)) from error
    if is_closure:
        def rewrite(value: Any) -> Any:
            if isinstance(value, Mapping):
                return {key: rewrite(item) for key, item in value.items()}
            if isinstance(value, list):
                return [rewrite(item) for item in value]
            if isinstance(value, str) and "/diagnostic-evidence/pending/" in value:
                return EVIDENCE_PREFIX + Path(value).name
            return value
        payload = rewrite(payload)
    target.parent.mkdir(parents=True, exist_ok=True)
    if is_closure:
        target.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    else:
        shutil.copy2(source, target)
    registry_rows[relative(target)] = {
        **registry_metadata(payload),
        "path": relative(target),
        "class": "canonical-strict",
        "status": str(payload.get("status", "executed-pass")),
        "source_commit": str(payload.get("source_commit", "")),
        "source_dirty": "false",
        "strict_evidence": "true",
        "ticket": ticket_for(target, payload),
        "closure": closure_reference,
        "sha256": sha256(target),
        "retention": "permanent",
        "reason": "explicitly promoted after strict contract and closure acceptance",
        "referenced_by": closure_reference,
        "generated_at": str(payload.get("generated_at") or payload.get("finished_at") or now()),
        "superseded_by": "",
        "storage": "git",
        "repository_head_at_capture": str(payload.get("repository_head_at_capture", payload.get("source_commit", ""))),
        "implementation_binding": str(payload.get("implementation_binding", "")),
        "implementation_tree_digest": str(payload.get("implementation_tree_digest", "")),
        "implementation_dirty": "false" if payload.get("implementation_dirty") is False else "unknown",
        "implementation_scope": ";".join(str(item) for item in payload.get("implementation_scope", ())),
        "artifact_ref": str(payload.get("artifact_ref", "")),
    }
    if batch_info:
        registry_rows[relative(target)]["reason"] += (
            "; immutable batch " + str(payload.get("batch_id", ""))
        )
    write_tsv(REGISTRY, [registry_rows[key] for key in sorted(registry_rows)], REGISTRY_FIELDS)
    try:
        closure_display = relative(closure) if closure else "-"
    except ValueError:
        closure_display = str(closure) if closure else "-"
    print(json.dumps({"status": "promoted", "source": str(source), "target": relative(target), "closure": closure_display}, ensure_ascii=False))
    return 0


def cmd_archive(args: argparse.Namespace) -> int:
    rows = load_registry()
    selected = {row["path"]: row for row in rows}
    requested = args.path or []
    if not requested:
        requested = [row["path"] for row in rows if row.get("class") in {"diagnostic-failed", "historical-superseded", "duplicate-or-unreferenced", "runtime-lifecycle"}]
    audit = ARCHIVE / "evidence-move-audit.tsv"
    existing = []
    if audit.is_file():
        with audit.open(encoding="utf-8", newline="") as handle:
            existing = list(csv.DictReader(handle, delimiter="\t"))
    # Archive is intentionally a dry-run by default.  A caller must opt in to
    # moving files with --apply after reviewing the printed SHA-256 plan.
    dry_run = (not getattr(args, "apply", False)) or getattr(args, "dry_run", False)
    moved = []
    for value in requested:
        rel = Path(value).as_posix()
        row = selected.get(rel)
        if not row:
            continue
        if row.get("class") in {"canonical-strict", "canonical-diagnostic"} and not getattr(args, "demote_canonical", False):
            continue
        source = resolve_registry_path(rel)
        if (not source.is_file()
                or (not inside(source, ROOT) and not inside(source, WORKSPACE))
                or (not inside(source, EVIDENCE) and not inside(source, DIAGNOSTIC))
                or inside(source, ARCHIVE)):
            continue
        if has_reference(row.get("referenced_by")) and not getattr(args, "references_updated", False):
            if dry_run:
                moved.append({
                    "source": rel,
                    "destination": "",
                    "sha256": row.get("sha256", ""),
                    "original_sha256": row.get("sha256", ""),
                    "action": "blocked-referenced",
                    "reason": "rewrite references before applying archive move",
                })
                continue
            raise SystemExit(
                f"archive refused for referenced evidence {rel}; run rewrite first or pass --references-updated"
            )
        if row.get("class") in {"canonical-strict", "canonical-diagnostic"}:
            destination = ARCHIVE / "historical" / Path(rel).name
        elif row.get("class") == "runtime-lifecycle":
            destination = DIAGNOSTIC / "runtime" / Path(rel).name
        elif row.get("class") == "diagnostic-failed":
            destination = DIAGNOSTIC / "failed" / Path(rel).name
        elif row.get("class") == "duplicate-or-unreferenced":
            destination = ARCHIVE / "duplicates" / Path(rel).name
        else:
            destination = ARCHIVE / "historical" / Path(rel).name
        if source.resolve() == destination.resolve():
            continue
        # Never overwrite an existing audit artifact.  Any basename collision
        # gets a deterministic hash suffix so both payloads remain recoverable
        # and registry paths stay unique, including same-content duplicates.
        if destination.exists():
            source_hash = sha256(source)
            candidate = destination.with_name(
                destination.stem + "--" + source_hash[:12] + destination.suffix
            )
            counter = 2
            while candidate.exists():
                candidate = destination.with_name(
                    destination.stem + "--" + source_hash[:12] + "-" + str(counter) + destination.suffix
                )
                counter += 1
            destination = candidate
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination_sha = sha256(source)
        original_sha = row.get("sha256") or destination_sha
        if dry_run:
            moved.append({"source": rel, "destination": relative(destination), "sha256": destination_sha,
                          "original_sha256": original_sha, "action": "would-move"})
            continue
        shutil.move(str(source), str(destination))
        moved.append({"source": rel, "destination": relative(destination), "sha256": destination_sha,
                      "original_sha256": original_sha, "action": "moved"})
    if not dry_run:
        ARCHIVE.mkdir(parents=True, exist_ok=True)
        write_tsv(audit, existing + moved, ("source", "destination", "sha256", "original_sha256", "action"))
        moved_map = {item["source"]: item["destination"] for item in moved}

        def replace_paths(value: str) -> str:
            result = value
            for source, destination in sorted(moved_map.items(), key=lambda item: len(item[0]), reverse=True):
                result = result.replace(source, destination)
                if source.startswith(REPO_PREFIX):
                    result = result.replace(source.removeprefix(REPO_PREFIX), destination)
            return result

        updated = []
        for row in rows:
            replacement = moved_map.get(row.get("path", ""))
            row = dict(row)
            if replacement:
                row["path"] = replacement
                if row.get("class") in {"canonical-strict", "canonical-diagnostic"} and getattr(args, "demote_canonical", False):
                    row["class"] = "historical-superseded"
                    row["retention"] = "permanent-archive"
                    row["reason"] = "explicitly demoted from canonical root after source/evidence revalidation"
            for field in ("closure", "referenced_by", "superseded_by"):
                if row.get(field):
                    row[field] = replace_paths(str(row[field]))
            updated.append(row)
        write_tsv(REGISTRY, updated, REGISTRY_FIELDS)
    print(json.dumps({"dry_run": dry_run, "count": len(moved), "moves": moved, "audit": relative(audit)}, ensure_ascii=False, indent=2))
    return 0


def cmd_prune(args: argparse.Namespace) -> int:
    rows = load_registry()
    today = dt.datetime.now(dt.timezone.utc).date()
    actions: List[Dict[str, str]] = []
    for row in rows:
        row_path = resolve_registry_path(row["path"])
        if row.get("class") in {"canonical-strict", "canonical-diagnostic"}:
            continue
        if has_reference(row.get("referenced_by")):
            continue
        retention = str(row.get("retention", ""))
        if not retention.endswith("d"):
            continue
        try:
            days = int(retention[:-1])
        except ValueError:
            continue
        path = row_path
        # A missing row is left untouched so verify can report the broken
        # registry reference.  Canonical files are protected above; expired
        # retained material may be deleted only after it has left that area.
        if not path.is_file() or not inside(path, ROOT) or inside(path, EVIDENCE):
            continue
        age = today - generated_date(row, path)
        if age.days < days:
            continue
        actions.append({
            "path": row["path"],
            "action": "would-delete" if (not args.apply or getattr(args, "dry_run", False)) else "deleted",
            "sha256": sha256(path),
            "reason": "retention expired; unreferenced non-canonical evidence",
            "retention": retention,
        })

    dry_run = (not args.apply) or getattr(args, "dry_run", False)
    audit_path = ARCHIVE / "evidence-prune-audit.tsv"
    # Persist the reviewed deletion list before any apply mutation.  This
    # makes `prune --apply` safe even when a caller did not run a separate
    # dry-run command first: the preview is written before the first unlink.
    preview_path = ARCHIVE / "evidence-prune-dry-run.tsv"
    if actions:
        preview_path.parent.mkdir(parents=True, exist_ok=True)
        preview_rows = [dict(item, generated_at=now()) for item in actions]
        write_tsv(preview_path, preview_rows,
                  ("path", "action", "sha256", "reason", "retention", "generated_at"))
    if not dry_run and actions:
        audit_path.parent.mkdir(parents=True, exist_ok=True)
        existing: List[Dict[str, str]] = []
        if audit_path.is_file():
            with audit_path.open(encoding="utf-8", newline="") as handle:
                existing = list(csv.DictReader(handle, delimiter="\t"))
        timestamp = now()
        audit_rows = existing + [dict(item, generated_at=timestamp) for item in actions]
        write_tsv(audit_path, audit_rows,
                  ("path", "action", "sha256", "reason", "retention", "generated_at"))
        deleted = {item["path"] for item in actions}
        write_tsv(REGISTRY, [row for row in rows if row.get("path") not in deleted], REGISTRY_FIELDS)
        for item in actions:
            resolve_registry_path(item["path"]).unlink()
    print(json.dumps({
        "dry_run": dry_run,
        "count": len(actions),
        "actions": actions,
        "preview": relative(preview_path),
        "audit": relative(audit_path),
    }, ensure_ascii=False, indent=2))
    return 0


def load_migration(path: Path) -> List[Dict[str, str]]:
    with path.open(encoding="utf-8", newline="") as handle:
        return list(csv.DictReader(handle, delimiter="\t"))


def cmd_rewrite(args: argparse.Namespace) -> int:
    plan = Path(args.plan)
    if not plan.is_absolute():
        plan = resolve_registry_path(plan)
    mapping = {
        row["source"]: row["target"]
        for row in load_migration(plan)
        if row.get("target") and row.get("target") != row.get("source")
    }
    variants: Dict[str, str] = {}
    for source, target in mapping.items():
        variants[source] = target
        variants[source.removeprefix(REPO_PREFIX)] = target
    # Search for exact planned paths instead of every evidence-shaped token.
    # This keeps `--include-json` bounded even when the diagnostic tree contains
    # large route ledgers and cumulative closures.
    search_pattern = "|".join(re.escape(key) for key in sorted(variants, key=len, reverse=True))
    if not search_pattern:
        print(json.dumps({"dry_run": True, "files": 0, "replacements": 0}, ensure_ascii=False))
        return 0
    command = ["rg", "-l", "--hidden", "--glob", "!.git/**",
               "--glob", "!" + EVIDENCE_PREFIX + "evidence-index.tsv",
               "--glob", "!.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/evidence-*",
               "--glob", "!.scratch/sunway-supplier-exit-api-first/archive/evidence-move-audit.tsv",
               "--glob", "!.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/**/*.tsv",
               "--glob", "!.scratch/sunway-supplier-exit-api-first/archive/**/*.json"]
    if not getattr(args, "include_json", False):
        command.extend([
            "--glob", "!.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/**/*.json",
            "--glob", "!.scratch/sunway-supplier-exit-api-first/archive/**/*.json",
        ])
    command.extend([search_pattern, str(ROOT)])
    search = subprocess.run(command, text=True, capture_output=True, check=False)
    candidates = [Path(line) for line in search.stdout.splitlines() if line.strip()]
    generated = {
        (DIAGNOSTIC / name).resolve()
        for name in ("evidence-inventory.tsv", "evidence-migration-plan.tsv",
                     "evidence-reference-rewrite-audit.tsv",
                     "evidence-reference-rewrite-dry-run.json",
                     "evidence-archive-dry-run.json")
    }
    candidates = [path for path in candidates
                  if path.resolve() != REGISTRY.resolve()
                  and path.resolve() not in generated
                  and path.resolve() != (ARCHIVE / "evidence-move-audit.tsv").resolve()]
    # Never replace a bare basename.  A basename can occur below an already
    # classified directory (for example ``evidence/failed/foo.json``); a bare
    # replacement would create malformed paths such as
    # ``evidence/failed/.scratch/.../foo.json``.  Full repository-relative
    # forms are sufficient for docs, manifests and closure JSON.
    audit: List[Dict[str, str]] = []
    for path in candidates:
        try:
            text = path.read_text(encoding="utf-8")
        except (OSError, UnicodeDecodeError):
            continue
        updated = text
        replacements = 0
        # Do bounded literal replacements instead of constructing one giant
        # alternation regex.  A full historical move audit can contain
        # hundreds of paths; the regex becomes expensive on inventory TSVs
        # and makes a dry-run look hung.  Longest keys first preserve the same
        # path-before-basename semantics without catastrophic backtracking.
        for key in sorted(variants, key=len, reverse=True):
            value = variants[key]
            count = updated.count(key)
            if count:
                updated = updated.replace(key, value)
                replacements += count
        if replacements:
            item = {"path": relative(path), "replacements": str(replacements), "before_sha256": hashlib.sha256(text.encode()).hexdigest(), "after_sha256": hashlib.sha256(updated.encode()).hexdigest()}
            audit.append(item)
            if getattr(args, "apply", False) and not getattr(args, "dry_run", False):
                path.write_text(updated, encoding="utf-8")
    audit_path = DIAGNOSTIC / "evidence-reference-rewrite-audit.tsv"
    if getattr(args, "apply", False) and not getattr(args, "dry_run", False):
        prior: List[Dict[str, str]] = []
        if audit_path.is_file():
            with audit_path.open(encoding="utf-8", newline="") as handle:
                prior = list(csv.DictReader(handle, delimiter="\t"))
        seen = {
            (row.get("path", ""), row.get("before_sha256", ""), row.get("after_sha256", ""))
            for row in prior
        }
        combined = prior + [
            row for row in audit
            if (row["path"], row["before_sha256"], row["after_sha256"]) not in seen
        ]
        write_tsv(audit_path, combined, ("path", "replacements", "before_sha256", "after_sha256"))
    dry_run = not getattr(args, "apply", False) or getattr(args, "dry_run", False)
    print(json.dumps({"dry_run": dry_run, "files": len(audit), "replacements": sum(int(row["replacements"]) for row in audit), "audit": relative(audit_path)}, ensure_ascii=False))
    return 0


def main(argv: Optional[Sequence[str]] = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest="command", required=True)
    scan = sub.add_parser("scan"); scan.add_argument("--output", type=Path); scan.set_defaults(func=cmd_scan)
    plan = sub.add_parser("plan", help="write a non-mutating evidence migration plan")
    plan.add_argument("--output", type=Path)
    plan.add_argument("--demote-canonical", action="store_true", help="plan archival of current canonical JSON before a source-binding commit")
    plan.set_defaults(func=cmd_plan)
    classify_parser = sub.add_parser("classify"); classify_parser.set_defaults(func=cmd_classify)
    verify = sub.add_parser("verify"); verify.set_defaults(func=cmd_verify)
    promote = sub.add_parser("promote")
    promote.add_argument("--report", required=True)
    promote.add_argument("--closure")
    promote.add_argument("--output")
    promote.add_argument("--milestone")
    promote.set_defaults(func=cmd_promote)
    archive = sub.add_parser("archive"); archive.add_argument("--path", action="append"); archive.add_argument("--dry-run", action="store_true"); archive.add_argument("--demote-canonical", action="store_true"); archive.add_argument("--references-updated", action="store_true"); archive.set_defaults(func=cmd_archive)
    rewrite = sub.add_parser("rewrite"); rewrite.add_argument("--plan", type=Path, required=True); rewrite.add_argument("--dry-run", action="store_true"); rewrite.add_argument("--include-json", action="store_true", help="also rewrite diagnostic/archive JSON references"); rewrite.set_defaults(func=cmd_rewrite)
    archive.add_argument("--apply", action="store_true", help="apply the reviewed move plan (default is dry-run)")
    rewrite.add_argument("--apply", action="store_true", help="apply replacements (default is dry-run)")
    prune = sub.add_parser("prune"); prune.add_argument("--apply", action="store_true"); prune.add_argument("--dry-run", action="store_true"); prune.set_defaults(func=cmd_prune)
    args = parser.parse_args(argv)
    return int(args.func(args))


if __name__ == "__main__":
    raise SystemExit(main())
