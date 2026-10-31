#!/usr/bin/env python3
"""Run an owned-distribution equipment maintenance lifecycle with strict evidence."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
import shutil
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[2]
BASE_PATH = ROOT / "owned-web/src/test/browser/run-inventory-browser.py"
OUTPUT = ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/equipment-plan-distribution-20261024.json"
SCOPES = (
    "owned-framework/src", "owned-personnel/src", "owned-equipment/src",
    "owned-consumables/src", "owned-attachments/src", "owned-report/src",
    "owned-scheduler/src", "owned-web/src", "owned-distribution/start.sh",
    "owned-distribution/package.py", "owned-distribution/example/owned-application.properties",
    "scripts/build-owned-framework.sh", "scripts/build-owned-personnel.sh",
    "scripts/build-owned-equipment.sh", "scripts/build-owned-consumables.sh",
    "scripts/build-owned-attachments.sh", "scripts/build-owned-report.sh",
    "scripts/build-owned-scheduler.sh", "scripts/build-owned-distribution.sh",
    "scripts/prepare-owned-public-deps.py", "scripts/reject-supplier-runtime.sh",
    "owned-web/build-owned-web.sh", "owned-web/original-personnel-sha256.json",
    "owned-web/original-inventory-sha256.json",
    "owned-distribution/tests/run-equipment-plan-browser.py",
)


def load_base():
    spec = importlib.util.spec_from_file_location("owned_inventory_base", BASE_PATH)
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


class KeptTemporaryDirectory:
    def __init__(self, path):
        self.name = str(path)

    def __enter__(self):
        return self.name

    def __exit__(self, exc_type, exc, tb):
        return False


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=OUTPUT)
    parser.add_argument("--kind", choices=("plan", "record"), default="plan")
    args = parser.parse_args(); guard_output_path(args.output)
    output = args.output
    plan = args.kind == "plan"
    base = load_base()
    temporary = Path(tempfile.mkdtemp(prefix="lims-equipment-plan-distribution-"))
    original_temp = base.tempfile.TemporaryDirectory
    original_rmtree = base.shutil.rmtree
    base.tempfile.TemporaryDirectory = lambda *args, **kwargs: KeptTemporaryDirectory(temporary)
    base.shutil.rmtree = lambda *args, **kwargs: None
    try:
        sys.argv = [str(BASE_PATH), "--distribution",
                    "--equipment-plan-lifecycle-only" if plan else "--equipment-record-lifecycle-only",
                    "--output", str(output)]
        status = base.main()
    finally:
        base.tempfile.TemporaryDirectory = original_temp
        base.shutil.rmtree = original_rmtree
    if status != 0:
        return status

    payload = json.loads(output.read_text())
    audit_path = temporary / "audit.jsonl"
    events = []
    if audit_path.is_file():
        for line in audit_path.read_text(errors="replace").splitlines():
            try:
                value = json.loads(line)
            except ValueError:
                continue
            if isinstance(value, dict):
                events.append(value)

    listing = base.passed(base.run(["git", "ls-files", "-z", "--", *SCOPES], cwd=ROOT),
                          "strict source inventory")
    files = [ROOT / item for item in listing.split("\0") if item]
    source_commit = base.passed(base.run(["git", "rev-parse", "HEAD"], cwd=ROOT),
                                "source commit").strip()
    if plan:
        scope = "owned distribution equipment maintenance-plan browser lifecycle; synthetic Oracle and Redis only"
        fixture = "equipment-maintenance-plan-distribution-fixture"
        audit_action = "equipment-plan-insert"
        routes = (
            "GET /?page=equipment",
            "POST /secure/emc/module/mdm/equipt/equipts/{id}/maints/queries/selectable",
            "POST /secure/emc/module/mdm/equipt/equipts/plans",
            "POST /secure/emc/module/mdm/equipt/equipts/{id}/plans/queries",
            "DELETE /secure/emc/module/mdm/equipt/equipts/plans",
        )
    else:
        scope = "owned distribution equipment maintenance-record browser lifecycle; synthetic Oracle and Redis only"
        fixture = "equipment-maintenance-record-distribution-fixture"
        audit_action = "equipment-record-insert"
        routes = (
            "GET /?page=equipment",
            "GET /secure/emc/module/mdm/equipt/equipts/{id}/records/maints/queries/selectable",
            "POST /secure/emc/module/mdm/equipt/equipt-records",
            "POST /secure/emc/module/mdm/equipt/equipts/{id}/records/queries",
            "DELETE /secure/emc/module/mdm/equipt/equipt-records",
        )
    payload.update({
        "runner": "owned-distribution/tests/run-equipment-plan-browser.py",
        "scope": scope,
        "fixture": fixture,
        "role": "manager", "organization": "org-a", "mode": "isolated",
        "source_commit": source_commit, "source_dirty": False,
        "source_sha256": {str(path.relative_to(ROOT)): digest(path) for path in files},
        "source_coverage": {"roots": list(SCOPES),
                            "files": {str(path.relative_to(ROOT)): digest(path) for path in files},
                            "diff": {"added": [], "deleted": []}},
        "verifier_sha256": {path: digest(ROOT / path) for path in base.VERIFIER_PATHS},
        "http_cases": [
            {"route": route, "role": "manager", "organization": "org-a",
             "mode": "isolated", "status": 200, "expected_status": 200}
            for route in routes
        ],
        "audit": {"events": events,
                  "expected": [{"action": audit_action, "outcome": "ALLOWED"}]},
    })
    # The base runner already proves the Docker/browser cleanup.  Remove the
    # retained private directory only after extracting its audit events.
    shutil.rmtree(str(temporary), ignore_errors=True)
    payload.setdefault("cleanup", {})["temporary_files_removed"] = not temporary.exists()
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n")
    print("equipment " + ("plan" if plan else "record") + " distribution strict: " + payload["status"] +
          " (" + str(len(payload.get("checks", []))) + " checks)")
    return 0 if payload["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
