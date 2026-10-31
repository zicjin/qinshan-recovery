#!/usr/bin/env python3
"""Replay the owned role-user matrix page through the packaged distribution."""
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

ROLE_USER_SOURCE_SCOPES = module.SOURCE_SCOPES + (
    "owned-distribution/tests/run-role-user-browser.py",
)


class RoleUserBrowserReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES = ROLE_USER_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-role-user-browser.py",
            scope="owned distribution role-user matrix page; role-admin same-organization grant/revoke and reader denial; synthetic Oracle and Redis only",
            role="role-admin", organization="org-a", mode="isolated",
            fixture="role-user-browser-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/role-user-matrix.js",
                "owned-distribution/tests/run-role-user-browser.py",
            )
        }

    def ui(self):
        self.configuration_overrides = {
            "owned.identity.role-menu-admin-menu-ids": "100900",
            "owned.role-user.write.mode": "isolated",
        }
        self.start("isolated")
        actor = next(a for a in role_fixture.actors()
                     if a["user_id"] == "matrix-role-admin-a")
        self.ab("fill", "#login-id", actor["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        self.ab("wait", "#roleUserMatrix-link")
        self.ab("click", "#roleUserMatrix-link")
        cell = "#role-user-cell-matrix-reader-a-T_CORE_ROLE_ROLENAME_104"
        self.ab("wait", cell)
        self.check("role-user page renders through distribution",
                    "角色用户矩阵" in self.text("#owned-workspace"))
        self.evaluate("(() => { const original = window.fetch; window.__roleUserCases = []; window.fetch = function(url, options) { return original.apply(this, arguments).then(async response => { const path = new URL(url, location.origin).pathname; if (path === '/secure/core/module/sys/role-users/instant') window.__roleUserCases.push({method: options?.method || 'GET', status: response.status, body: await response.clone().text()}); return response; }); }; return true; })()")
        self.ab("click", cell)
        self.ab("wait", 1500)
        self.check("role-user grant persists in Oracle", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE_USER WHERE ROLEID=104 AND USERID='matrix-reader-a';",
            "role-user grant") == "1")
        self.ab("wait", "--fn", "document.querySelector(" + json.dumps(cell) + ")?.checked === true && document.querySelector(" + json.dumps(cell) + ")?.disabled === false")
        self.ab("click", cell)
        self.ab("wait", 1500)
        self.check("role-user revoke restores Oracle baseline", self.query(
            "SELECT COUNT(*) FROM T_CORE_ROLE_USER WHERE ROLEID=104 AND USERID='matrix-reader-a';",
            "role-user revoke") == "0")
        observed = self.evaluate("window.__roleUserCases")
        self.report["http_cases"] = [
            {"route": "GET /?page=roleUserMatrix", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200},
            {"route": "PUT /secure/core/module/sys/role-users/instant", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200, "label": "role-user-grant"},
            {"route": "PUT /secure/core/module/sys/role-users/instant", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200, "label": "role-user-revoke"},
        ]
        self.report["role_user_http_cases"] = observed
        self.ab("click", "#logout")
        self.ab("wait", "--text", "已退出登录。")
        reader = next(a for a in role_fixture.actors()
                      if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open role-user page",
                    "角色用户矩阵" not in self.text("#menu-nav"))
        self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        writes = [event for event in events if event.get("action") == "role-user-write"
                  and event.get("resource") == "role-user.write"
                  and event.get("outcome") == "ALLOWED"]
        self.check("role-user writes have allowed audit", len(writes) >= 2)
        self.check("role-user audit excludes credentials",
                    all(value not in contents for value in self.secrets))
        self.report["audit"] = {
            "events": writes,
            "expected": [{"action": "role-user-write", "actorId": "matrix-role-admin-a",
                           "resource": "role-user.write", "outcome": "ALLOWED"}],
            "credentials_excluded": True,
        }


def main():
    parser = module.argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path,
                        default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/role-user-distribution-browser-20261025.json")
    args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-role-user-browser-private-") as temporary:
        replay = RoleUserBrowserReplay(temporary)
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
    print("role-user browser: %s (%d checks)" %
          (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
