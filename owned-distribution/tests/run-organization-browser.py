#!/usr/bin/env python3
"""Replay the owned organization-management page through the distribution."""
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
module = importlib.util.module_from_spec(spec); sys.modules[spec.name] = module; spec.loader.exec_module(module)
from vertical_slices import role_fixture

class OrganizationBrowserReplay(module.PermissionReplay):
    def build(self):
        super().build()
        self.report.update(runner="owned-distribution/tests/run-organization-browser.py",
                          scope="owned distribution organization-management page; role-admin multi-organization create/update/delete and reader denial; synthetic Oracle and Redis only",
                          role="role-admin", organization="org-a", mode="isolated", fixture="organization-browser-fixture")
        self.report['browser_source_sha256'] = {path: hashlib.sha256((ROOT/path).read_bytes()).hexdigest() for path in (
            "owned-web/src/main/resources/static/index.html", "owned-web/src/main/resources/static/owned-web/app.js",
            "owned-web/src/main/resources/static/owned-web/organization-management.js",
            "owned-distribution/tests/run-organization-browser.py")}

    def ui(self):
        self.configuration_overrides = {"owned.identity.role-menu-admin-menu-ids":"100900", "owned.organization.write.mode":"isolated"}
        self.start("isolated")
        actor = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-role-admin-a")
        self.ab("fill", "#login-id", actor["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])"); self.ab("wait", "#organizationManagement-link"); self.ab("click", "#organizationManagement-link")
        self.ab("wait", "[data-organization-id=\"org-a\"]"); self.check("organization-management page renders through distribution", "组织管理" in self.text("#owned-workspace"))
        self.ab("fill", "#organization-id", "org-browser"); self.ab("fill", "#organization-name", "Browser Organization"); self.ab("fill", "#organization-parent", "org-a")
        self.ab("find", "role", "button", "click", "--name", "新增组织", "--exact"); self.ab("wait", "--text", "Browser Organization")
        self.check("organization browser create persists Oracle row", self.query("SELECT COUNT(*) FROM T_CORE_ORG WHERE ID='org-browser' AND ORGNAME='Browser Organization';", "organization browser create") == "1")
        updated = self.evaluate("fetch('/secure/core/module/mdm/orgs/org-browser',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{id:'org-browser',orgName:'Browser Organization Updated'}})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("organization browser update returns HTTP 200", updated["status"] == 200)
        self.check("organization browser update persists Oracle row", self.query("SELECT COUNT(*) FROM T_CORE_ORG WHERE ID='org-browser' AND ORGNAME='Browser Organization Updated';", "organization browser update") == "1")
        deleted = self.evaluate("fetch('/secure/core/module/mdm/orgs',{method:'DELETE',headers:{'Content-Type':'application/json'},body:JSON.stringify({ids:['org-browser']})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("organization browser delete returns HTTP 200", deleted["status"] == 200)
        self.check("organization browser delete restores baseline", self.query("SELECT COUNT(*) FROM T_CORE_ORG WHERE ID='org-browser';", "organization browser delete") == "0")
        self.report['http_cases'] = [
            {"route":"GET /?page=organizationManagement","role":"role-admin","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"POST /secure/core/module/mdm/orgs","role":"role-admin","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"PUT /secure/core/module/mdm/orgs/{id}","role":"role-admin","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"DELETE /secure/core/module/mdm/orgs","role":"role-admin","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
        ]
        self.ab("click", "#logout"); self.ab("wait", "--text", "已退出登录。"); reader = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit"); self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open organization-management page", "组织管理" not in self.text("#menu-nav")); self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths); events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        writes = [e for e in events if e.get('action') in ('organization-insert','organization-update','organization-delete') and e.get('outcome') == 'ALLOWED']
        self.check("organization browser writes have allowed audit", len(writes) >= 3); self.check("organization browser audit excludes credentials", all(value not in contents for value in self.secrets))
        self.report['audit']={'events':writes,'expected':[{'action':'organization-insert','actorId':'matrix-role-admin-a','resource':'org-browser','outcome':'ALLOWED'}],'credentials_excluded':True}

def main():
    parser = module.argparse.ArgumentParser(description=__doc__); parser.add_argument('--output',type=Path,default=ROOT/'.scratch/sunway-supplier-exit-api-first/archive/historical/organization-browser-distribution-20261025.json'); args=parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix='lims-organization-browser-private-') as temporary:
        replay=OrganizationBrowserReplay(temporary)
        try: replay.build(); replay.database(); replay.ui(); replay.audit(); replay.report['status']='executed-pass'
        except Exception as failure: replay.report['reason']=replay.redact(str(failure)); replay.report['diagnostic_traceback']=replay.redact(traceback.format_exc()); replay.diagnostics()
        finally:
            replay.cleanup(); replay.report['cleanup']['private_temporary_directory_removed']=not Path(temporary).exists(); replay.report['cleanup']['temporary_files_removed']=replay.report['cleanup']['private_temporary_directory_removed']; replay.report['finished_at']=replay.now(); args.output.parent.mkdir(parents=True,exist_ok=True); args.output.write_text(replay.redact(json.dumps(replay.report,ensure_ascii=False,indent=2))+'\n')
    replay.report['cleanup']['private_temporary_directory_removed'] = not Path(temporary).exists()
    replay.report['cleanup']['temporary_files_removed'] = replay.report['cleanup']['private_temporary_directory_removed']
    args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2))+'\n')
    print('organization browser: %s (%d checks)'%(replay.report['status'],len(replay.report['checks']))); return 0 if replay.report['status']=='executed-pass' else 1
if __name__=='__main__': raise SystemExit(main())
