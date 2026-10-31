#!/usr/bin/env python3
"""Replay the owned role-management page through the shipped distribution."""
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

ROLE_SOURCE_SCOPES = module.SOURCE_SCOPES + (
    "owned-distribution/tests/run-role-management-browser.py",
)


class RoleBrowserReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES = ROLE_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-role-management-browser.py",
            scope="owned distribution role-management page; role-admin role lifecycle and reader denial; synthetic Oracle and Redis only",
            role="role-admin", organization="org-a", mode="isolated",
            fixture="role-management-browser-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/role-management.js",
                "owned-distribution/tests/run-role-management-browser.py",
            )
        }

    def ui(self):
        self.configuration_overrides = {
            "owned.identity.role-menu-admin-menu-ids": "100900",
            "owned.role.write.mode": "isolated",
        }
        self.start("isolated")
        actor = next(a for a in role_fixture.actors()
                     if a["user_id"] == "matrix-role-admin-a")
        self.ab("fill", "#login-id", actor["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        self.ab("wait", "#roleManagement-link")
        self.ab("click", "#roleManagement-link")
        self.ab("wait", '[data-role-id="104"]')
        self.check("role-management page renders through distribution",
                    "角色管理" in self.text("#owned-workspace"))

        self.ab("fill", "#role-name", "Browser Role")
        self.ab("fill", "#role-organization", "org-b")
        self.ab("find", "role", "button", "click", "--name", "新增角色", "--exact")
        self.ab("wait", "--text", "Browser Role")
        role_id = int(self.query(
            "SELECT ID FROM T_CORE_ROLE WHERE ROLENAME='Browser Role';",
            "role browser insert"))
        self.check("role browser create persists Oracle row", role_id > 104)
        self.check("role browser create persists organization scope", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE WHERE ID=%d AND USEDBYORGID='org-b';" % role_id,
            "role browser organization") == "1")

        updated = self.evaluate(
            "fetch('/secure/core/module/sys/roles/" + str(role_id) + "',"
            "{method:'PUT',headers:{'Content-Type':'application/json'},"
            "body:JSON.stringify({p:{id:" + str(role_id) + ","
            "roleName:'Browser Role Updated',usedByOrgId:'org-b',lastSuspendedFlag:'0'}})})"
            ".then(async r=>({status:r.status,body:await r.text()}))")
        self.report["http_cases"].append({
            "route": "PUT /secure/core/module/sys/roles/{id}",
            "role": "role-admin", "organization": "org-a", "mode": "isolated",
            "status": updated["status"], "expected_status": 200,
        })
        self.check("role browser update returns HTTP 200", updated["status"] == 200)
        self.check("role browser update persists Oracle row", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE WHERE ID=%d AND ROLENAME='Browser Role Updated';" % role_id,
            "role browser update") == "1")

        suspended = self.evaluate(
            "fetch('/secure/core/module/sys/roles/" + str(role_id) + "',"
            "{method:'PUT',headers:{'Content-Type':'application/json'},"
            "body:JSON.stringify({p:{id:" + str(role_id) + ","
            "roleName:'Browser Role Updated',usedByOrgId:'org-b',lastSuspendedFlag:'1'}})})"
            ".then(async r=>({status:r.status,body:await r.text()}))")
        self.report["http_cases"].append({
            "route": "PUT /secure/core/module/sys/roles/{id}",
            "role": "role-admin", "organization": "org-a", "mode": "isolated",
            "status": suspended["status"], "expected_status": 200,
        })
        self.check("role browser suspend returns HTTP 200", suspended["status"] == 200)
        self.check("role browser suspend persists Oracle row", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE WHERE ID=%d AND LASTSUSPENDEDFLAG='1';" % role_id,
            "role browser suspend") == "1")

        deleted = self.evaluate(
            "fetch('/secure/core/module/sys/roles',{method:'DELETE',"
            "headers:{'Content-Type':'application/json'},"
            "body:JSON.stringify({ids:[" + str(role_id) + "]})})"
            ".then(async r=>({status:r.status,body:await r.text()}))")
        self.report["http_cases"].append({
            "route": "DELETE /secure/core/module/sys/roles",
            "role": "role-admin", "organization": "org-a", "mode": "isolated",
            "status": deleted["status"], "expected_status": 200,
        })
        self.check("role browser delete returns HTTP 200", deleted["status"] == 200)
        self.check("role browser delete restores baseline", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE WHERE ID=%d;" % role_id,
            "role browser delete") == "0")

        self.report["http_cases"] = [
            {"route": "GET /?page=roleManagement", "role": "role-admin",
             "organization": "org-a", "mode": "isolated", "status": 200,
             "expected_status": 200},
            {"route": "POST /secure/core/module/sys/roles", "role": "role-admin",
             "organization": "org-a", "mode": "isolated", "status": 200,
             "expected_status": 200},
        ] + self.report["http_cases"]

        self.ab("click", "#logout")
        self.ab("wait", "--text", "已退出登录。")
        reader = next(a for a in role_fixture.actors()
                      if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open role-management page",
                    "角色管理" not in self.text("#menu-nav"))
        self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        writes = [event for event in events if event.get("action") in
                  ("role-insert", "role-update", "role-delete")
                  and event.get("outcome") == "ALLOWED"]
        self.check("role browser writes have allowed audit", len(writes) >= 4)
        self.check("role browser audit excludes credentials",
                    all(value not in contents for value in self.secrets))
        self.report["audit"] = {
            "events": writes,
            "expected": [{"action": "role-insert", "actorId": "matrix-role-admin-a",
                           "resource": writes[0].get("resource") if writes else "",
                           "outcome": "ALLOWED"}],
            "credentials_excluded": True,
        }


def main():
    parser = module.argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path,
                        default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/role-management-browser-20261025.json")
    args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-role-management-browser-private-") as temporary:
        replay = RoleBrowserReplay(temporary)
        try:
            replay.build()
            replay.database()
            replay.ui()
            replay.audit()
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
    print("role management browser: %s (%d checks)" %
          (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
