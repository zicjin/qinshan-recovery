#!/usr/bin/env python3
"""Verify that role-management writes fail closed in the default profile."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import traceback

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location(
    "permission_matrix", Path(__file__).with_name("run-permission-matrix.py"))
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
from vertical_slices import role_fixture

DEFAULT_SOURCE_SCOPES = module.SOURCE_SCOPES + (
    "owned-distribution/tests/run-role-management-default-browser.py",
)


class RoleManagementDefaultReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES = DEFAULT_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-role-management-default-browser.py",
            scope="owned distribution role-management default-profile deny; role-admin write gate and denied audit; synthetic Oracle and Redis only",
            role="role-admin", organization="org-a", mode="default",
            fixture="role-management-default-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/role-management.js",
                "owned-distribution/tests/run-role-management-default-browser.py",
            )
        }

    def ui(self):
        self.configuration_overrides = {
            "owned.identity.role-menu-admin-menu-ids": "100900",
        }
        self.start("default")
        actor = next(a for a in role_fixture.actors()
                     if a["user_id"] == "matrix-role-admin-a")
        self.ab("fill", "#login-id", actor["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        self.ab("wait", "#roleManagement-link")
        self.ab("click", "#roleManagement-link")
        self.ab("wait", '[data-role-id="104"]')
        self.check("role-management page opens in default profile",
                    "角色管理" in self.text("#owned-workspace"))
        denied = self.evaluate(
            "fetch('/secure/core/module/sys/roles',{method:'POST',"
            "headers:{'Content-Type':'application/json'},"
            "body:JSON.stringify({p:{roleName:'Default Denied Role'}})})"
            ".then(async r=>({status:r.status,body:await r.text()}))")
        self.check("default profile rejects role write", denied["status"] == 403)
        self.check("default profile leaves Oracle baseline", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE WHERE ROLENAME='Default Denied Role';",
            "default role baseline") == "0")
        self.report["http_cases"] = [
            {"route": "GET /?page=roleManagement", "role": "role-admin",
             "organization": "org-a", "mode": "default", "status": 200,
             "expected_status": 200},
            {"route": "POST /secure/core/module/sys/roles", "role": "role-admin",
             "organization": "org-a", "mode": "default", "status": 403,
             "expected_status": 403},
        ]
        self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        self.report["audit_events_debug"] = events
        denied = [event for event in events if event.get("action") == "authorize"
                  and event.get("resource") == "role.write"
                  and event.get("outcome") == "DENIED"]
        self.check("default role write has denied audit", len(denied) >= 1)
        self.check("default role audit excludes credentials",
                    all(value not in contents for value in self.secrets))
        self.report["audit"] = {
            "events": denied,
            "expected": [{"action": "authorize", "actorId": "matrix-role-admin-a",
                           "resource": "role.write", "outcome": "DENIED"}],
            "credentials_excluded": True,
        }


def main():
    parser = module.argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path,
                        default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/role-management-default-browser-20261025.json")
    args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-role-management-default-private-") as temporary:
        replay = RoleManagementDefaultReplay(temporary)
        try:
            replay.build(); replay.database(); replay.ui(); replay.audit()
            replay.report["status"] = "executed-pass"
        except Exception as failure:
            replay.report["reason"] = replay.redact(str(failure))
            replay.report["diagnostic_traceback"] = replay.redact(traceback.format_exc())
            replay.diagnostics()
        finally:
            replay.cleanup()
            replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists()
            replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]
            replay.report["finished_at"] = replay.now()
            args.output.parent.mkdir(parents=True, exist_ok=True)
            args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists()
    replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]
    args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    print("role management default browser: %s (%d checks)" %
          (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
