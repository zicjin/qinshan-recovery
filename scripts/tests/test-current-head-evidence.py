#!/usr/bin/env python3
"""Require published evidence to bind current implementation source."""
import argparse
import csv
import json
import re
import subprocess
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import current_implementation_binding
from source_binding import REPO_ROOT
DEFAULT_REPORTS = ()


def git_head():
    return subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=REPO_ROOT, text=True).strip()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("reports", nargs="*", type=Path, default=list(DEFAULT_REPORTS))
    args = parser.parse_args()
    head = git_head()
    current = current_implementation_binding()
    reports = list(args.reports)
    registry_rows = None
    if not reports:
        registry = ROOT / "evidence/evidence-index.tsv"
        if not registry.is_file():
            raise SystemExit("current-head evidence: canonical registry is missing: " + str(registry))
        with registry.open(encoding="utf-8", newline="") as handle:
            registry_rows = [row for row in csv.DictReader(handle, delimiter="\t")
                             if row.get("class") in {"canonical-strict", "canonical-diagnostic"}]
        reports = [REPO_ROOT / row["path"] for row in registry_rows]
    for index, given in enumerate(reports):
        path = given if given.is_absolute() else ROOT / given
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
        if subprocess.run(["git", "cat-file", "-e", commit + "^{commit}"], cwd=REPO_ROOT,
                          stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode:
            raise SystemExit("current-head evidence: repository_head_at_capture is not a Git object: " + str(path))
        if payload.get("implementation_tree_digest"):
            if payload.get("implementation_tree_digest") != current["implementation_tree_digest"]:
                raise SystemExit("current-head evidence: implementation digest is stale: " + str(path))
            binding = payload.get("implementation_binding")
            if isinstance(binding, dict):
                binding = binding.get("binding_id")
            if binding != current["binding_id"]:
                raise SystemExit("current-head evidence: implementation binding is stale: " + str(path))
            if payload.get("implementation_dirty") is not False or current["implementation_dirty"]:
                raise SystemExit("current-head evidence: implementation scope is dirty: " + str(path))
            declared = set(str(item) for item in payload.get("implementation_scope", ()))
            if not set(current["implementation_scope"]).issubset(declared):
                raise SystemExit("current-head evidence: implementation scope is incomplete: " + str(path))
        else:
            if payload.get("source_dirty") is not False:
                raise SystemExit("current-head evidence: source_dirty is not false: " + str(path))
            if commit != head:
                raise SystemExit("current-head evidence: legacy source_commit is not current HEAD: " + str(path))
    print("current-head evidence identity: PASS (%d reports; HEAD=%s)" % (len(reports), head[:12]))


if __name__ == "__main__":
    main()
