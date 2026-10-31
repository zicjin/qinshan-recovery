#!/usr/bin/env python3
"""Validate the executable handoff bundle without executing external systems."""
import json
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
BUNDLE = ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/replay-bundle-20261025.json"
payload = json.loads(BUNDLE.read_text(encoding="utf-8"))
assert payload["status"] == "prepared"
assert payload["production_access"] is False
assert payload["source_dirty"] is False
assert re.fullmatch(r"[0-9a-f]{40}", payload["source_commit"])
assert subprocess.run(["git", "cat-file", "-e", payload["source_commit"] + "^{commit}"], cwd=ROOT.parent).returncode == 0
assert re.fullmatch(r"[0-9a-f]{40}", payload["nested_project_commit"])
project = ROOT / "project"
assert subprocess.run(["git", "cat-file", "-e", payload["nested_project_commit"] + "^{commit}"], cwd=project).returncode == 0
assert payload["images"]["oracle"].startswith("ghcr.io/gvenzl/oracle-free@sha256:")
assert payload["images"]["redis"].startswith("public.ecr.aws/docker/library/redis:7.4-alpine@sha256:")
for key in ("prepare", "package_clean", "start", "replay", "stop", "rollback"):
    assert isinstance(payload["commands"].get(key), str) and payload["commands"][key]
for key in ("status", "production_access", "source_commit", "source_dirty", "role", "organization", "mode", "fixture", "http_cases", "audit", "cleanup"):
    assert key in payload["expected_evidence"]
assert all("password" not in value.lower() and "token" not in value.lower()
           for value in payload["commands"].values())
print("replay bundle: PASS (source, nested project, image digests, commands and evidence contract)")
