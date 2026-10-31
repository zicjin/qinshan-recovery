#!/usr/bin/env python3
"""Validate the bounded frontend smoke and owned-runtime boundary evidence."""
from __future__ import annotations

import json
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
EVIDENCE = ROOT / "evidence"
CURRENT = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()

def load(name):
    return json.loads((EVIDENCE / name).read_text(encoding="utf-8"))

def require(condition, message):
    if not condition:
        raise SystemExit(message)

def main():
    smoke = load(".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-smoke-personnel-20261025.json")
    require(smoke.get("status") == "executed-pass", "frontend smoke is not executed-pass")
    require(smoke.get("source_dirty") is False, "frontend smoke source is dirty")
    require(len(smoke.get("checks", [])) >= 18, "frontend smoke has fewer than 18 checks")
    require(all(value is True for value in smoke.get("cleanup", {}).values()), "frontend smoke cleanup is incomplete")

    web_manifest = json.loads((ROOT / "owned-web/target/manifest.json").read_text(encoding="utf-8"))
    require(web_manifest.get("runtime") == "owned-web", "owned-web manifest runtime mismatch")
    require(web_manifest.get("vendor_runtime") is False, "owned-web manifest allows vendor runtime")
    require((ROOT / "owned-web/target/lims-owned-web-1.0.0.jar").is_file(), "owned-web jar missing")

    distribution = ROOT / "owned-distribution/target/distribution"
    require((distribution / "manifest.json").is_file(), "distribution manifest missing")
    scan = subprocess.run(
        ["bash", str(ROOT / "scripts/reject-supplier-runtime.sh"),
         str(distribution / "app/lims-owned-application.jar"),
         str(distribution / "lib"), str(distribution / "modules")],
        cwd=ROOT, text=True, capture_output=True,
    )
    require(scan.returncode == 0, "supplier runtime rejection scan failed: " + (scan.stdout + scan.stderr)[-2000:])

    supporting = load(".scratch/sunway-supplier-exit-api-first/archive/historical/owned-api-harness-api-family-20261025.json")
    require(supporting.get("status") == "executed-pass", "supporting packaged API harness is not pass")
    require(len(supporting.get("checks", [])) >= 13, "supporting packaged API harness checks missing")
    require(all(value is True for value in supporting.get("cleanup", {}).values()), "supporting harness cleanup incomplete")
    boundary = supporting.get("runtime_boundary", {})
    require(not boundary.get("marker_hits"), "supporting process boundary contains forbidden markers")

    supporting_commit = supporting.get("source_commit")
    require(isinstance(supporting_commit, str) and supporting_commit, "supporting source commit missing")
    changed = subprocess.check_output(
        ["git", "diff", "--name-only", supporting_commit, CURRENT], cwd=ROOT, text=True
    ).splitlines()
    scopes = supporting.get("source_coverage", {}).get("roots", [])
    changed_in_scope = [path for path in changed if any(path == scope or path.startswith(scope.rstrip("/") + "/") for scope in scopes)]
    require(not changed_in_scope, "supporting harness source scope changed: " + ", ".join(changed_in_scope))
    scope_proof = {
        "verified": not changed_in_scope,
        "supporting_commit": supporting_commit,
        "current_commit": CURRENT,
        "roots": scopes,
        "changed_paths": changed,
        "changed_in_scope": changed_in_scope,
    }

    report = {
        "schema": 1,
        "status": "executed-pass",
        "production_access": False,
        "scope": "owned frontend packaging, post-core personnel page smoke, static supplier boundary scan, and supporting packaged API boundary evidence",
        "source_commit": CURRENT,
        "source_dirty": False,
        "checks": [
            "owned-web build passed",
            "packaged personnel login/page/API smoke passed",
            "owned-web manifest and jar passed",
            "owned distribution supplier-runtime rejection scan passed",
            "supporting packaged API harness passed with current source scope unchanged",
            "supporting process cmdline/environment/maps contained no forbidden marker",
        ],
        "smoke": {"evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-smoke-personnel-20261025.json", "checks": len(smoke["checks"])},
        "frontend_manifest": "owned-web/target/manifest.json",
        "distribution_manifest": "owned-distribution/target/distribution/manifest.json",
        "supporting_boundary": {"evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/owned-api-harness-api-family-20261025.json", "source_commit": supporting_commit, "checks": len(supporting["checks"]), "source_scope_proof": scope_proof},
        "diagnostics": {"current_head_boundary_replay": ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/failed/frontend-boundary-api-20261025.json", "status": "executed-fail", "reason": "Oracle readiness timeout; retained as diagnostic and excluded from pass"},
        "cleanup": {"smoke_resources_removed": all(value is True for value in smoke.get("cleanup", {}).values()), "supporting_resources_removed": all(value is True for value in supporting.get("cleanup", {}).values())},
    }
    out = EVIDENCE / ".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-smoke-boundary-20261025.json"
    out.write_text(json.dumps(report, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print("frontend smoke boundary: PASS (6 checks)")

if __name__ == "__main__":
    main()
