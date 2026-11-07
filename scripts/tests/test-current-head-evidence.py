#!/usr/bin/env python3
"""Require published evidence to bind current implementation source."""
import argparse
import csv
import json
import re
import subprocess
from collections.abc import Mapping
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import current_implementation_binding
from source_binding import (
    BINDING_SCHEMA_VERSION, BINDING_SURFACES, REPO_ROOT,
    is_standalone_root, runtime_contract_digest,
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
    if payload.get("runtime_contract_dirty", False) is not False or runtime.get("runtime_dirty", False) is not False:
        return "runtime contract is dirty"
    digest = runtime_contract_digest(runtime)
    if payload["runtime_contract_binding"] != "rt-" + digest[:16]:
        return "runtime contract binding does not match report runtime"
    if "runtime_contract_digest" in payload and payload["runtime_contract_digest"] != digest:
        return "runtime contract digest does not match report runtime"
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
        error = identity_error(payload, current)
        if error:
            raise SystemExit("current-head evidence: " + error + ": " + str(path))
    print("current-head evidence identity: PASS (%d reports; HEAD=%s)" % (len(reports), head[:12]))


if __name__ == "__main__":
    main()
