#!/usr/bin/env python3
"""Replay the role-menu browser page through the packaged distribution."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import hashlib
import importlib.util
import json
from pathlib import Path
import sys
import tempfile
import traceback

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location("permission_matrix", Path(__file__).with_name("run-permission-matrix.py"))
module = importlib.util.module_from_spec(spec)
sys.modules[spec.name] = module
spec.loader.exec_module(module)
from vertical_slices import role_fixture

ROLE_MENU_SOURCE_SCOPES = module.SOURCE_SCOPES + (
    "owned-distribution/tests/run-role-menu-browser.py",
)


class RoleMenuBrowserReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES = ROLE_MENU_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-role-menu-browser.py",
            scope="owned distribution role-menu browser page; role-admin grant/revoke and reader denial; synthetic Oracle and Redis only",
            role="role-admin", organization="org-a", mode="isolated",
            fixture="role-menu-browser-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/role-menu-matrix.js",
                "owned-web/src/main/resources/static/owned-web/role-user-matrix.js",
                "owned-web/src/test/browser/run-menu-browser.py",
                "owned-distribution/tests/run-role-menu-browser.py")}

    def database(self):
        super().database()
        self.query("INSERT INTO T_CORE_MENU (ID,MENUNAME,ORDERNO,PARENTID,HREF) VALUES ('100901','Matrix Child',4,'100900','/menu-roles/child');\nCOMMIT;", "role-menu child fixture")

    def ui(self):
        self.configuration_overrides = {"owned.identity.role-menu-admin-menu-ids": "100900", "owned.role-menu.write.mode": "isolated", "owned.role-user.write.mode": "isolated"}
        self.start("isolated")
        forbidden_markers = (
            "sunway-app.so", "license.lic", "license-machine", "MAC_ADDRESS",
            "SYSTEM_MAC_ADDRESS_LIST", "sunway.app.license")
        proc_root = Path("/proc") / str(self.process.pid)
        proc_sources = {
            "cmdline": (proc_root / "cmdline").read_bytes(),
            "environ": (proc_root / "environ").read_bytes(),
            "maps": (proc_root / "maps").read_bytes(),
        }
        packaged_names = {path.name for path in self.distribution.rglob("*")}
        dynamic_hits = {
            source: [marker for marker in forbidden_markers
                     if marker.encode("utf-8") in data]
            for source, data in proc_sources.items()
        }
        file_hits = [marker for marker in forbidden_markers
                     if any(marker.lower() in name.lower() for name in packaged_names)]
        self.report["runtime_boundary"] = {
            "process_pid_observed": self.process.poll() is None,
            "forbidden_markers": list(forbidden_markers),
            "process_marker_hits": dynamic_hits,
            "packaged_file_marker_hits": file_hits,
            "native_license_inputs_loaded": not any(dynamic_hits.values()),
            "packaged_forbidden_inputs_absent": not file_hits,
        }
        self.check("owned process has no native/license/MAC marker", not any(dynamic_hits.values()))
        self.check("owned distribution has no forbidden runtime input filename", not file_hits)
        for route in ("/core/module/sys/licenses", "/core/module/sys/licenses/number-of-online-users", "/open/security/licenses"):
            result = self.evaluate("fetch(" + json.dumps(route) + ",{cache:'no-store'}).then(async r=>({status:r.status,body:await r.text()}))")
            self.check("removed license route returns 404: " + route, result["status"] == 404)
            self.report.setdefault("http_cases", []).append({"route": "GET " + route, "role": "anonymous", "organization": "none", "mode": "isolated", "status": result["status"], "expected_status": 404})
        actor = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-role-admin-a")
        self.ab("fill", "#login-id", actor["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        menus = self.evaluate("fetch('/secure/core/module/sys/menus',{cache:'no-store'}).then(async r=>({status:r.status,body:await r.text()}))")
        self.report['menu_probe'] = menus
        self.check("role-admin menu probe includes role-menu administration", menus['status'] == 200 and '100900' in menus['body'])
        self.ab("wait", "#roleMenuMatrix-link")
        self.ab("click", "#roleMenuMatrix-link")
        self.ab("wait", "#role-menu-cell-100901-T_CORE_ROLE_ROLENAME_104")
        self.check("role-admin role-menu page renders through distribution", "角色菜单矩阵" in self.text("#owned-workspace"))
        self.ab("click", "#role-menu-cell-100901-T_CORE_ROLE_ROLENAME_104")
        self.ab("wait", 3000)
        self.report['grant_status_text'] = self.text('#page-status')
        self.check("role-menu browser grant persists in Oracle", self.query("SELECT COUNT(*) FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=104 AND PERMISSIONTYPEID='T_CORE_MENU' AND TARGETID='100901';", "role-menu browser grant") == "1")
        self.ab("click", "#role-menu-cell-100901-T_CORE_ROLE_ROLENAME_104")
        self.ab("wait", 1000)
        self.check("role-menu browser revoke restores Oracle baseline", self.query("SELECT COUNT(*) FROM T_CORE_ROLE_PERMISSION WHERE ROLEID=104 AND PERMISSIONTYPEID='T_CORE_MENU' AND TARGETID='100901';", "role-menu browser revoke") == "0")
        self.ab("click", "#roleUserMatrix-link")
        self.ab("wait", "#role-user-cell-matrix-reader-a-T_CORE_ROLE_ROLENAME_104")
        self.check("role-admin role-user page renders through distribution", "角色用户矩阵" in self.text("#owned-workspace"))
        self.evaluate("(() => { const original = window.fetch; window.__roleUserCases = []; window.fetch = function(url, options) { return original.apply(this, arguments).then(async response => { const path = new URL(url, location.origin).pathname; if (path === '/secure/core/module/sys/role-users/instant') window.__roleUserCases.push({method: options?.method || 'GET', status: response.status, body: await response.clone().text()}); return response; }); }; return true; })()")
        self.ab("click", "#role-user-cell-matrix-reader-a-T_CORE_ROLE_ROLENAME_104")
        self.ab("wait", 1500)
        self.check("role-user browser grant persists in Oracle", self.query("SELECT COUNT(*) FROM T_CORE_ROLE_USER WHERE ROLEID=104 AND USERID='matrix-reader-a';", "role-user browser grant") == "1")
        self.ab("wait", "--fn", "document.querySelector('#role-user-cell-matrix-reader-a-T_CORE_ROLE_ROLENAME_104')?.checked === true && document.querySelector('#role-user-cell-matrix-reader-a-T_CORE_ROLE_ROLENAME_104')?.disabled === false")
        self.ab("click", "#role-user-cell-matrix-reader-a-T_CORE_ROLE_ROLENAME_104")
        self.ab("wait", 2000)
        self.report['role_user_http_cases'] = self.evaluate("window.__roleUserCases")
        self.check("role-user browser revoke restores Oracle baseline", self.query("SELECT COUNT(*) FROM T_CORE_ROLE_USER WHERE ROLEID=104 AND USERID='matrix-reader-a';", "role-user browser revoke") == "0")
        self.report["http_cases"] = [
            {"route": "GET /?page=roleMenuMatrix", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200},
            {"route": "PUT /secure/core/module/sys/menu-roles/instant", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200, "label": "grant"},
            {"route": "PUT /secure/core/module/sys/menu-roles/instant", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200, "label": "revoke"},
            {"route": "GET /?page=roleUserMatrix", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200},
            {"route": "PUT /secure/core/module/sys/role-users/instant", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200, "label": "role-user-grant"},
            {"route": "PUT /secure/core/module/sys/role-users/instant", "role": "role-admin", "organization": "org-a", "mode": "isolated", "status": 200, "expected_status": 200, "label": "role-user-revoke"},
        ]
        self.ab("click", "#logout")
        self.ab("wait", "--text", "已退出登录。")
        reader = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"])
        self.ab("fill", "#login-password", self.credential)
        self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open role-menu browser page", "角色菜单矩阵" not in self.text("#menu-nav"))
        self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        writes = [event for event in events if event.get("action") == "role-menu-write"
                  and event.get("resource") == "role-menu.write"
                  and event.get("outcome") == "ALLOWED"]
        self.check("role-menu browser writes have allowed audit", len(writes) >= 1)
        role_user_writes = [event for event in events if event.get("action") == "role-user-write"
                            and event.get("resource") == "role-user.write"
                            and event.get("outcome") == "ALLOWED"]
        self.check("role-user browser writes have allowed audit", len(role_user_writes) >= 1)
        self.check("role-menu browser audit excludes credentials", all(value not in contents for value in self.secrets))
        self.report["audit"] = {"events": writes + role_user_writes, "expected": [{"action": "role-menu-write", "actorId": "matrix-role-admin-a", "resource": "role-menu.write", "outcome": "ALLOWED"}, {"action": "role-user-write", "actorId": "matrix-role-admin-a", "resource": "role-user.write", "outcome": "ALLOWED"}], "credentials_excluded": True}


def main():
    parser = module.argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/role-menu-distribution-browser-20261024.json")
    args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-role-menu-browser-private-") as temporary:
        replay = RoleMenuBrowserReplay(temporary)
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
    print("role-menu distribution browser: %s (%d checks)" % (replay.report["status"], len(replay.report["checks"])) )
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
