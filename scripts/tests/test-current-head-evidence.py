#!/usr/bin/env python3
"""Require published evidence to bind current implementation source."""
import argparse
import csv
import hashlib
import importlib.util
import json
import re
import subprocess
from collections.abc import Mapping
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import current_implementation_binding, strict_case_contract
from lifecycle_proof import validate_cleanup
from source_binding import (
    BINDING_SCHEMA_VERSION, BINDING_SURFACES, REPO_ROOT,
    is_standalone_root, runtime_contract_digest, workspace_root,
)
DEFAULT_REPORTS = ()


def git_head(repo_root):
    return subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=repo_root, text=True).strip()


def identity_error(payload, current):
    """Check exact dependency identity; this does not authorize strict closure."""
    surfaces = ("application", "harness", "planning")
    if not all(payload.get(surface + "_binding") for surface in BINDING_SURFACES):
        return "legacy composite binding is stale; orthogonal evidence or replay is required"
    # Report schema and binding schema are distinct. Existing schema=2 runner
    # reports predate an explicit binding schema field and carry canonical
    # scopes instead; never infer the binding version from report schema.
    for key in ("schema_version", "binding_schema_version"):
        if key in payload and (type(payload[key]) is not int or payload[key] != BINDING_SCHEMA_VERSION):
            return "unsupported binding schema"
    for surface in (*surfaces, "runtime_contract"):
        key = surface + "_scope"
        declared = payload.get(key)
        canonical = current[key]
        if (not isinstance(declared, list)
                or any(not isinstance(item, str) for item in declared)
                or len(declared) != len(canonical) or set(declared) != set(canonical)):
            return surface + " scope is not canonical"
    # Bookkeeping scopes and the compatibility aggregate are provenance;
    # their evolution does not change the behavior dependencies above.
    for surface in ("governance", "implementation"):
        declared = payload.get(surface + "_scope")
        if (not isinstance(declared, list) or not declared
                or any(not isinstance(item, str) or not item for item in declared)
                or len(declared) != len(set(declared))):
            return surface + " provenance scope is malformed"
    for surface in surfaces:
        if payload[surface + "_binding"] != current[surface + "_binding"]:
            return surface + " binding is stale"
        digest_key = surface + "_tree_digest"
        if digest_key in payload and payload[digest_key] != current[digest_key]:
            return surface + " digest is stale"
        if current[surface + "_dirty"] is not False:
            return surface + " scope is dirty"
        # Old runner metadata has only implementation_dirty. New captures
        # have per-surface flags, so governance dirtiness need not stale HTTP.
        if payload.get(surface + "_dirty", payload.get("implementation_dirty")) is not False:
            return surface + " report scope is dirty"
    runtime = payload.get("runtime_contract")
    if not isinstance(runtime, Mapping) or not runtime.get("runtime_schema") or not runtime.get("baseline_schema"):
        return "runtime contract or schema identity is missing"
    if payload.get("runtime_contract_dirty", False) is not False:
        return "runtime contract is dirty"
    if runtime.get("runtime_dirty", False) is not False:
        # A prepared manifest is immutable and deliberately has no after-state.
        # Only independently replayed, registered lifecycle custody can supply
        # that observation; never rewrite the contract or its fingerprint.
        if (runtime.get("runtime_dirty", False) is not None
                or runtime.get("runtime_manifest_schema") != "local-prepared-v1"
                or runtime.get("lifecycle_phase") != "prepared"):
            return "runtime contract is dirty"
        try:
            validate_cleanup(payload.get("cleanup", {}), payload)
        except (ValueError, OSError, KeyError, TypeError, AttributeError) as error:
            return "invalid lifecycle cleanup proof: " + str(error)
    digest = runtime_contract_digest(runtime)
    if payload["runtime_contract_binding"] != "rt-" + digest[:16]:
        return "runtime contract binding does not match report runtime"
    if "runtime_contract_digest" in payload and payload["runtime_contract_digest"] != digest:
        return "runtime contract digest does not match report runtime"
    return None


def closure_error(payload, current, repo_root, report_root, head):
    """Validate an aggregate through its registered, hash-pinned strict inputs.

    Existing closure schema has no runtime manifest or orthogonal metadata.
    Its compatibility aggregate must still be current; each actual dependency
    must independently satisfy the stronger report identity and strict gates.
    """
    spec = importlib.util.spec_from_file_location("closure_governance", ROOT / "scripts/manage-evidence.py")
    governance = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(governance)
    governance.ROOT = repo_root
    governance.WORKSPACE = workspace_root(repo_root)
    governance._CURRENT_BINDING_CACHE = current
    try:
        with (report_root / "evidence/evidence-index.tsv").open(encoding="utf-8", newline="") as handle:
            rows = list(csv.DictReader(handle, delimiter="\t"))
        registry = {}
        for row in rows:
            path = governance.resolve_registry_path(row["path"]).resolve()
            if not path.is_relative_to(repo_root):
                # Historical rows can live in the parent archive. Referenced
                # strict inputs are separately required to stay in the repo.
                continue
            key = path.relative_to(repo_root).as_posix()
            if key in registry:
                return "closure registry path is duplicated"
            registry[key] = row
        valid, reason = governance.strict_closure_contract(payload, head, registry)
        if not valid:
            return reason
        for item in payload["reports"]:
            path = governance.resolve_registry_path(item["path"]).resolve()
            key = path.relative_to(repo_root).as_posix()
            raw = path.read_bytes()
            digest = hashlib.sha256(raw).hexdigest()
            if digest != item.get("sha256") or digest != registry[key].get("sha256"):
                return "closure report SHA256 mismatch"
            report = json.loads(raw)
            if any(report.get(field) != item.get(field) for field in (
                    "status", "strict_evidence", "implementation_binding", "implementation_tree_digest")):
                return "closure report index differs from actual report"
            error = identity_error(report, current)
            if error:
                return "closure dependency: " + error
            valid, reason = strict_case_contract(report)
            if not valid:
                return "closure dependency: " + reason
            if not governance.cleanup_complete(report.get("cleanup")):
                return "closure dependency cleanup is incomplete"
    except (ValueError, OSError, KeyError, TypeError, AttributeError) as error:
        return "invalid closure dependency: " + str(error)
    return None


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("reports", nargs="*", type=Path, default=list(DEFAULT_REPORTS))
    parser.add_argument("--repo-root", type=Path, default=REPO_ROOT,
                        help="repository whose canonical source binding and Git provenance to validate")
    args = parser.parse_args()
    repo_root = args.repo_root.resolve()
    report_root = repo_root if is_standalone_root(repo_root) else repo_root / "lims-recovery"
    head = git_head(repo_root)
    current = current_implementation_binding(repo_root)
    reports = list(args.reports)
    registry_rows = None
    if not reports:
        registry = report_root / "evidence/evidence-index.tsv"
        if not registry.is_file():
            print("current-head evidence identity: PASS (0 reports; no canonical registry)")
            return
        with registry.open(encoding="utf-8", newline="") as handle:
            registry_rows = [row for row in csv.DictReader(handle, delimiter="\t")
                             if row.get("class") in {"canonical-strict", "canonical-diagnostic"}]
        reports = [repo_root / row["path"] for row in registry_rows]
    for index, given in enumerate(reports):
        path = given if given.is_absolute() else report_root / given
        row = registry_rows[index] if registry_rows is not None else {}
        storage = row.get("storage", "git")
        if not path.is_file() and storage in {"artifact", "local"}:
            # Artifact/local diagnostic records are intentionally allowed to be
            # absent from a clean checkout.  Their registry metadata remains
            # auditable, but they cannot participate in strict closure.
            continue
        if not path.is_file():
            raise SystemExit("current-head evidence: canonical git report is missing: " + str(path))
        payload = json.loads(path.read_text(encoding="utf-8"))
        commit = payload.get("source_commit")
        if not isinstance(commit, str) or not re.fullmatch(r"[0-9a-f]{7,40}", commit):
            raise SystemExit("current-head evidence: malformed repository_head_at_capture/source_commit: " + str(path))
        if subprocess.run(["git", "cat-file", "-e", commit + "^{commit}"], cwd=repo_root,
                          stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode:
            raise SystemExit("current-head evidence: repository_head_at_capture is not a Git object: " + str(path))
        if payload.get("scope") == "current-head-spring-web-api-route-closure":
            error = closure_error(payload, current, repo_root, report_root, head)
        else:
            error = identity_error(payload, current)
        if error:
            raise SystemExit("current-head evidence: " + error + ": " + str(path))
    print("current-head evidence identity: PASS (%d reports; HEAD=%s)" % (len(reports), head[:12]))


if __name__ == "__main__":
    main()
