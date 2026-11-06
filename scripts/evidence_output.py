"""Output-path policy shared by API runners.

Canonical evidence is a publication area.  A runner may write temporary or
diagnostic JSON freely, but a root evidence path requires an explicit
finalization context (normally ``manage-evidence.py promote``).
"""
from __future__ import annotations

from pathlib import Path
import subprocess
import sys
from typing import Any, Iterable, Mapping, Tuple

try:
    from source_binding import (
        IMPLEMENTATION_SCOPE,
        capture_binding,
        binding_from_payload,
        feature_root,
        REPO_ROOT,
    )
except ModuleNotFoundError:
    sys.path.insert(0, str(Path(__file__).resolve().parent))
    from source_binding import (
    IMPLEMENTATION_SCOPE,
    capture_binding,
    binding_from_payload,
    feature_root,
    REPO_ROOT,
    )

ROOT = Path(__file__).resolve().parents[1]
GIT_ROOT = REPO_ROOT
CANONICAL_ROOT = ROOT / "evidence"
DIAGNOSTIC_ROOT = feature_root(REPO_ROOT) / "diagnostic-evidence"
WRITE_METHODS = {"POST", "PUT", "PATCH", "DELETE"}
REQUIRED_CLEANUP = (
    "application_stopped", "oracle_stopped", "redis_stopped",
    "network_removed", "volumes_removed", "temporary_files_removed",
)
_SOURCE_SCOPE_CACHE: dict[tuple[str, tuple[str, ...]], bool] = {}
_SOURCE_DIFF_CACHE: dict[str, set[str]] = {}
_WORKTREE_PATHS: set[str] | None = None


def current_implementation_binding(repo_root: Path | None = None, *, extra_scope: Iterable[str] = ()) -> dict[str, Any]:
    """Return the one binding shared by every API/evidence tool.

    ``repo_root`` is injectable for isolated verifier fixtures.  Production
    runners omit it and therefore use the workspace root.  ``extra_scope`` is
    retained as a keyword-only compatibility parameter but cannot redefine
    the canonical implementation scope.
    """
    extras = tuple(str(item) for item in extra_scope if str(item))
    if extras:
        raise ValueError("implementation binding scope is centralized; extra_scope is not allowed")
    return capture_binding(repo_root or REPO_ROOT)


def implementation_binding_valid(payload: Mapping[str, Any], *, require_clean: bool = True) -> bool:
    current = current_implementation_binding()
    surfaces = ("application", "harness", "runtime_contract", "planning")
    if all(surface + "_binding" in payload for surface in surfaces):
        if any(payload.get(surface + "_binding") != current.get(surface + "_binding")
               for surface in surfaces):
            return False
        if require_clean and any(
            payload.get(surface + "_dirty") is True or current.get(surface + "_dirty") is True
            for surface in surfaces
        ):
            return False
        return True

    payload_binding = binding_from_payload(payload)
    if payload_binding != current["binding_id"]:
        return False
    if payload.get("implementation_tree_digest") != current["implementation_tree_digest"]:
        return False
    if require_clean and (payload.get("implementation_dirty") is not False or current["implementation_dirty"]):
        return False
    return True


def _changed_paths(source_commit: str) -> set[str]:
    cached = _SOURCE_DIFF_CACHE.get(source_commit)
    if cached is not None:
        return cached
    result = subprocess.run(
        ["git", "-C", str(GIT_ROOT), "diff", "--name-only", source_commit, "HEAD", "--"],
        stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True,
    )
    paths = {line.strip() for line in result.stdout.splitlines() if line.strip()}
    _SOURCE_DIFF_CACHE[source_commit] = paths
    return paths


def _worktree_paths() -> set[str]:
    global _WORKTREE_PATHS
    if _WORKTREE_PATHS is not None:
        return _WORKTREE_PATHS
    result = subprocess.run(
        ["git", "-C", str(GIT_ROOT), "status", "--porcelain", "--untracked-files=all"],
        stdout=subprocess.PIPE, stderr=subprocess.DEVNULL, text=True,
    )
    paths: set[str] = set()
    for line in result.stdout.splitlines():
        if len(line) < 4:
            continue
        value = line[3:]
        if " -> " in value:
            value = value.split(" -> ", 1)[1]
        paths.add(value.strip())
    _WORKTREE_PATHS = paths
    return paths


def _touches_scope(path: str, scope: tuple[str, ...]) -> bool:
    return any(path == item or path.startswith(item.rstrip("/") + "/") for item in scope)


def source_scope_unchanged(source_commit: str, scope: Iterable[str] = IMPLEMENTATION_SCOPE) -> bool:
    """Classify historical scope for diagnostics; never grant current promotion."""
    if not source_commit:
        return False
    if subprocess.run(["git", "-C", str(GIT_ROOT), "cat-file", "-e", source_commit + "^{commit}"],
                      stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode:
        return False
    scope = tuple(scope) or IMPLEMENTATION_SCOPE
    cache_key = (source_commit, scope)
    if cache_key in _SOURCE_SCOPE_CACHE:
        return _SOURCE_SCOPE_CACHE[cache_key]
    if any(_touches_scope(path, scope) for path in _changed_paths(source_commit)):
        _SOURCE_SCOPE_CACHE[cache_key] = False
        return False
    # A report bound to HEAD is still stale when the implementation files are
    # dirty in the worktree.  Comparing HEAD to HEAD alone cannot see this
    # state, which used to let uncommitted runner changes masquerade as clean
    # strict evidence.
    value = not any(_touches_scope(path, scope) for path in _worktree_paths())
    _SOURCE_SCOPE_CACHE[cache_key] = value
    return value


def route_execution_model_valid(value: Any) -> bool:
    return (
        isinstance(value, Mapping)
        and value.get("boundary_process") is False
        and value.get("internal_http_bridge") is False
        and value.get("jni_replacement_process") is False
    )


def cleanup_contract_complete(value: Any) -> bool:
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


def strict_case_contract(report: Mapping[str, Any]) -> Tuple[bool, str]:
    """Validate the per-case evidence required before strict promotion.

    Read-only cases may declare fixture/audit as ``not-required``.  A business
    write must use HTTP, restore through the API (or an explicitly named
    case-HTTP cleanup), record rollback proof, and carry an audit observation.
    """
    cases = report.get("cases")
    if not isinstance(cases, list) or not cases:
        return False, "strict report has no cases"
    fingerprinted_schema = int(report.get("schema", 1)) >= 2
    for index, case in enumerate(cases):
        if not isinstance(case, Mapping):
            return False, "case %d is not an object" % index
        status = case.get("status")
        if not isinstance(status, int) or not 100 <= status <= 599:
            return False, "case %d has no HTTP status" % index
        route = case.get("route")
        if not isinstance(route, Mapping) or not route.get("method") or not route.get("path"):
            return False, "case %d has no route binding" % index
        fixture = case.get("fixture")
        if not isinstance(fixture, Mapping):
            return False, "case %d has no fixture observation" % index
        audit = case.get("audit")
        if audit is None:
            return False, "case %d has no audit observation" % index
        if fingerprinted_schema:
            for field in (
                "request_fingerprint", "stimulus_contract_version",
                "response_fingerprint", "normalizer_version",
                "validation_mode", "batch_id",
            ):
                if not case.get(field):
                    return False, "case %d lacks %s" % (index, field)
            for field in (
                "application_binding", "harness_binding",
                "runtime_contract_binding", "family_fingerprint",
            ):
                if not case.get(field):
                    return False, "case %d lacks %s" % (index, field)
            if not str(case["request_fingerprint"]).startswith("req-"):
                return False, "case %d has invalid request fingerprint" % index
            if not str(case["response_fingerprint"]).startswith("res-"):
                return False, "case %d has invalid response fingerprint" % index
            if case["validation_mode"] not in {"executed", "revalidated-by-fingerprint"}:
                return False, "case %d has invalid validation mode" % index
        method = str(route.get("method", case.get("method", "GET"))).upper()
        operation = str(route.get("operation", case.get("operation", "read"))).lower()
        # POST is also the legacy query verb for many inherited/resource
        # routes. Treat it as a business write only when the contract marks
        # the operation as write/create/update/delete; PUT/PATCH/DELETE are
        # writes by method alone.
        is_business_write = method in {"PUT", "PATCH", "DELETE"} or operation in {"write", "create", "update", "delete"}
        if is_business_write:
            if fixture.get("business_write_via", "http") != "http":
                return False, "case %d business write did not use HTTP" % index
            if fixture.get("restore") not in {"restored", "case-http-operation"}:
                return False, "case %d business write has no API restore" % index
            rollback = case.get("rollback")
            if not isinstance(rollback, Mapping) or rollback.get("status") not in {
                "verified", "verified-via-fixture-restore", "not-required",
            }:
                return False, "case %d business write has no rollback proof" % index
            if audit == "not-required" or not isinstance(audit, Mapping):
                return False, "case %d business write has no audit observation" % index
            if audit.get("required", True) and audit.get("status") not in {"observed", "verified"}:
                return False, "case %d audit was not observed" % index
        if case.get("readback_required"):
            readback = case.get("readback_http")
            if not isinstance(readback, list) or not readback:
                return False, "case %d requires API readback but has no readback observation" % index
            if any(not isinstance(item, Mapping) or not isinstance(item.get("status"), int)
                   or not 100 <= int(item["status"]) <= 599 for item in readback):
                return False, "case %d readback observation has no HTTP status" % index
    return True, "strict per-case contract valid"


def is_canonical_path(path: Path) -> bool:
    try:
        return path.resolve().is_relative_to(CANONICAL_ROOT.resolve())
    except ValueError:
        return False


def guard_output_path(path: Path) -> None:
    # Runners never publish directly.  Publication is an explicit registry
    # operation (`manage-evidence.py promote`) so an interrupted or failed
    # runner cannot leave an apparently canonical JSON behind.  Do not keep
    # an environment-variable escape hatch here: it would turn a normal
    # runner invocation into an undocumented publication path.
    if is_canonical_path(path):
        raise ValueError(
            "runner output cannot write canonical evidence directly; "
            "write diagnostic output and use manage-evidence.py promote"
        )


def pending_path(final_path: Path, run_id: str) -> Path:
    """Return a deterministic diagnostic path for a would-be canonical file."""
    return DIAGNOSTIC_ROOT / "pending" / run_id / final_path.name
