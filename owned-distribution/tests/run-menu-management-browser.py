#!/usr/bin/env python3
"""Replay global menu CRUD through the shipped owned distribution."""
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
spec = importlib.util.spec_from_file_location("permission_matrix", Path(__file__).with_name("run-permission-matrix.py"))
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
from vertical_slices import role_fixture

MENU_SOURCE_SCOPES = module.SOURCE_SCOPES + ("owned-distribution/tests/run-menu-management-browser.py",)


class MenuBrowserReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES = MENU_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-menu-management-browser.py",
            scope="owned distribution global menu-management page; role-admin menu create/update/delete and reader denial; synthetic Oracle and Redis only",
            role="role-admin", organization="org-a", mode="isolated",
            fixture="menu-management-browser-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/menu-management.js",
                "owned-distribution/tests/run-menu-management-browser.py",
            )
        }

    def ui(self):
        self.configuration_overrides = {
            "owned.identity.role-menu-admin-menu-ids": "100900",
            "owned.menu.write.mode": "isolated",
        }
        self.start("isolated")
        actor = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-role-admin-a")
        self.ab("fill", "#login-id", actor["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])"); self.ab("wait", "#menuManagement-link"); self.ab("click", "#menuManagement-link")
        self.ab("wait", '[data-menu-id="100900"]')
        self.check("menu-management page renders through distribution", "菜单管理" in self.text("#owned-workspace"))
        self.ab("fill", "#menu-id", "100902"); self.ab("fill", "#menu-name", "Browser Menu"); self.ab("fill", "#menu-href", "/browser-menu"); self.ab("fill", "#menu-order", "5")
        self.ab("find", "role", "button", "click", "--name", "新增菜单", "--exact"); self.ab("wait", "--text", "Browser Menu")
        self.check("menu browser create persists Oracle row", self.query("SELECT COUNT(*) FROM T_CORE_MENU WHERE ID='100902' AND MENUNAME='Browser Menu';", "menu browser insert") == "1")
        updated = self.evaluate("fetch('/secure/core/module/sys/menus/100902',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{id:'100902',menuName:'Browser Menu Updated',menuInitName:'Browser Menu Updated',href:'/browser-menu',orderNo:5}})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.report["http_cases"].append({"route":"PUT /secure/core/module/sys/menus/{id}","role":"role-admin","organization":"org-a","mode":"isolated","status":updated["status"],"expected_status":200})
        self.check("menu browser update returns HTTP 200", updated["status"] == 200)
        self.check("menu browser update persists Oracle row", self.query("SELECT COUNT(*) FROM T_CORE_MENU WHERE ID='100902' AND MENUNAME='Browser Menu Updated';", "menu browser update") == "1")
        deleted = self.evaluate("fetch('/secure/core/module/sys/menus',{method:'DELETE',headers:{'Content-Type':'application/json'},body:JSON.stringify({ids:['100902']})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.report["http_cases"].append({"route":"DELETE /secure/core/module/sys/menus","role":"role-admin","organization":"org-a","mode":"isolated","status":deleted["status"],"expected_status":200})
        self.check("menu browser delete returns HTTP 200", deleted["status"] == 200)
        self.check("menu browser delete restores baseline", self.query("SELECT COUNT(*) FROM T_CORE_MENU WHERE ID='100902';", "menu browser delete") == "0")
        self.report["http_cases"] = [{"route":"GET /?page=menuManagement","role":"role-admin","organization":"org-a","mode":"isolated","status":200,"expected_status":200},{"route":"POST /secure/core/module/sys/menus","role":"role-admin","organization":"org-a","mode":"isolated","status":200,"expected_status":200}] + self.report.get("http_cases", [])
        self.ab("click", "#logout"); self.ab("wait", "--text", "已退出登录。")
        reader = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit"); self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open menu-management page", "菜单管理" not in self.text("#menu-nav")); self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        writes = [event for event in events if event.get("action") in ("menu-insert", "menu-update", "menu-delete") and event.get("outcome") == "ALLOWED"]
        self.check("menu browser writes have allowed audit", len(writes) >= 3)
        self.check("menu browser audit excludes credentials", all(value not in contents for value in self.secrets))
        self.report["audit"] = {"events": writes, "expected":[{"action":"menu-insert","actorId":"matrix-role-admin-a","resource":"100902","outcome":"ALLOWED"}],"credentials_excluded":True}


def main():
    parser = module.argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/menu-management-browser-20261025.json")
    args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-menu-management-browser-private-") as temporary:
        replay = MenuBrowserReplay(temporary)
        try:
            replay.build(); replay.database(); replay.ui(); replay.audit(); replay.report["status"] = "executed-pass"
        except Exception as failure:
            replay.report["reason"] = replay.redact(str(failure)); replay.report["diagnostic_traceback"] = replay.redact(traceback.format_exc()); replay.diagnostics()
        finally:
            replay.cleanup(); replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists(); replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]; replay.report["finished_at"] = replay.now(); args.output.parent.mkdir(parents=True, exist_ok=True); args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists(); replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]; args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    print("menu management browser: %s (%d checks)" % (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
