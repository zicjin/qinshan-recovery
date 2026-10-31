#!/usr/bin/env python3
"""Replay bounded result-run status writes through the shipped distribution."""
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import traceback
import sys

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT))
from scripts.evidence_output import guard_output_path
spec = importlib.util.spec_from_file_location("result_read_browser", Path(__file__).with_name("run-result-browser.py"))
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
WRITE_SOURCE_SCOPES = module.RESULT_SOURCE_SCOPES + ("owned-distribution/tests/run-result-write-browser.py",)


class ResultWriteBrowserReplay(module.ResultBrowserReplay):
    def build(self):
        module.RESULT_SOURCE_SCOPES = WRITE_SOURCE_SCOPES
        module.SOURCE_SCOPES = WRITE_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-result-write-browser.py",
            scope="owned distribution bounded result-run status write; manager/org-a isolated update, cross-organization and reader/default denial; synthetic Oracle and Redis only",
            role="manager", organization="org-a", mode="isolated", fixture="result-run-write-browser-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/result-runs.js",
                "owned-distribution/tests/run-result-write-browser.py",
            )
        }

    def database(self):
        super().database()
        self.query(
            "INSERT INTO T_CORE_MENU (ID,MENUNAME,ORDERNO) VALUES ('300101','Sample test run status write',11);\n"
            "INSERT INTO T_CORE_ROLE_PERMISSION VALUES (102,'T_CORE_MENU','300101');\n"
            "UPDATE T_EMC_RUN SET STATUS='audit' WHERE ID=1;\nCOMMIT;",
            "result write permission fixture")
        self.query(
            "CREATE TABLE T_EMC_ORDER_TASK_RESULT (ID NUMBER PRIMARY KEY,RUNID VARCHAR2(64),ORGID VARCHAR2(64),ITEMNAME VARCHAR2(100),ORIGINALRESULT VARCHAR2(200),REPORTRESULT VARCHAR2(200),RESULTSTATUS VARCHAR2(36),UNITNAME VARCHAR2(36),LOWLIMIT VARCHAR2(36),HIGHLIMIT VARCHAR2(36));\n"
            "INSERT INTO T_EMC_ORDER_TASK_RESULT VALUES (101,'1','org-a','pH','',NULL,NULL,'pH','0','14');\n"
            "INSERT INTO T_EMC_ORDER_TASK_RESULT VALUES (102,'3','org-b','VOC','1.0','1.0','entered','ppm','0','10');\n"
            "COMMIT;",
            "result item fixture")

    def ui(self):
        self.configuration_overrides = {
            "owned.identity.result-menu-ids": "300100",
            "owned.identity.result-write-menu-ids": "300101",
            "owned.result.write.mode": "isolated",
        }
        self.start("isolated")
        actor = next(a for a in module.role_fixture.actors() if a["user_id"] == "matrix-manager-a")
        self.ab("fill", "#login-id", actor["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])"); self.ab("wait", "#resultRuns-link"); self.ab("click", "#resultRuns-link")
        self.ab("wait", "#result-runs-table"); self.ab("wait", "#result-run-update-1"); self.ab("click", "#result-run-view-1"); self.ab("wait", "#result-item-value-101")
        self.check("result write controls render through distribution", "结果状态已更新" not in self.text("#owned-workspace"))
        self.ab("fill", "#result-item-value-101", "12.50"); self.ab("click", "#result-item-save-101"); self.ab("wait", "--fn", "document.querySelector('#result-item-status-101')?.textContent === 'entered'")
        self.ab("click", "#result-item-calculate-101"); self.ab("wait", "--fn", "document.querySelector('#result-item-status-101')?.textContent === 'calculated' && document.querySelector('#result-item-report-101')?.textContent === '12.5'")
        self.ab("click", "#result-item-review-101"); self.ab("wait", "--fn", "document.querySelector('#result-item-status-101')?.textContent === 'reviewed'")
        self.ab("click", "#result-item-approve-101"); self.ab("wait", "--fn", "document.querySelector('#result-item-status-101')?.textContent === 'approved'")
        result_after_workflow = self.evaluate("fetch('/secure/core/module/bp/result/runs/1/results/queries',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("result value entry, calculation, review and approval persist", result_after_workflow["status"] == 200 and json.loads(result_after_workflow["body"])["rows"][0]["resultStatus"] == "approved" and json.loads(result_after_workflow["body"])["rows"][0]["reportResult"] == "12.5")
        self.ab("select", "#result-run-status-editor-1", "done"); self.ab("click", "#result-run-update-1"); self.ab("wait", 1500)
        self.report["post_update_status_text"] = self.text("#page-status")
        self.check("result status update reports a response", "结果状态已更新" in self.report["post_update_status_text"] or "结果更新失败" in self.report["post_update_status_text"])
        detail_after_update = self.evaluate("fetch('/secure/core/module/bp/result/runs/1',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        result_status = json.loads(detail_after_update["body"]).get("status") if detail_after_update["status"] == 200 else None
        self.report["result_status_after_update"] = result_status
        self.check("same-organization result status update persists", result_status == "done")
        foreign = self.evaluate("fetch('/secure/core/module/bp/result/runs/3',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{status:'approved'}})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("cross-organization result status update is denied", foreign["status"] == 403)
        invalid = self.evaluate("fetch('/secure/core/module/bp/result/runs/1',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{status:'bad status'}})}).then(async r=>({status:r.status,body:await r.text()}))")
        rollback_detail = self.evaluate("fetch('/secure/core/module/bp/result/runs/1',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        rollback_status = json.loads(rollback_detail["body"]).get("status") if rollback_detail["status"] == 200 else None
        self.check("invalid result status is rejected", invalid["status"] == 400 and rollback_status == "done")
        foreign_value = self.evaluate("fetch('/secure/core/module/bp/result/runs/3/results/102',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{originalResult:'2.0'}})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("cross-organization result value update is denied", foreign_value["status"] == 403)
        self.report["http_cases"] = [
            {"route":"GET /?page=resultRuns","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"PUT /secure/core/module/bp/result/runs/1","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200,"label":"same-organization-update"},
            {"route":"PUT /secure/core/module/bp/result/runs/3","role":"manager","organization":"org-a","mode":"isolated","status":403,"expected_status":403,"label":"cross-organization-denial"},
            {"route":"PUT /secure/core/module/bp/result/runs/1","role":"manager","organization":"org-a","mode":"isolated","status":400,"expected_status":400,"label":"invalid-status-rollback"},
            {"route":"POST /secure/core/module/bp/result/runs/1/results/queries","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200,"label":"result-item-query"},
            {"route":"PUT /secure/core/module/bp/result/runs/1/results/101","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200,"label":"result-value-entry"},
            {"route":"POST /secure/core/module/bp/result/runs/1/results/101/calculate","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200,"label":"result-calculation"},
            {"route":"PUT /secure/core/module/bp/result/runs/1/results/101/review","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200,"label":"result-review"},
            {"route":"PUT /secure/core/module/bp/result/runs/1/results/101/approve","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200,"label":"result-approval"},
            {"route":"PUT /secure/core/module/bp/result/runs/3/results/102","role":"manager","organization":"org-a","mode":"isolated","status":403,"expected_status":403,"label":"cross-organization-result-denial"},
        ]
        self.ab("click", "#logout"); self.ab("wait", "--text", "已退出登录。")
        reader = next(a for a in module.role_fixture.actors() if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit"); self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open result write page", "样品测试批结果" not in self.text("#menu-nav"))
        self.close()
        self.configuration_overrides = {
            "owned.identity.result-menu-ids": "300100",
            "owned.identity.result-write-menu-ids": "300101",
        }
        self.start("default")
        self.ab("fill", "#login-id", actor["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit"); self.ab("wait", "#workspace-view:not([hidden])"); self.ab("wait", "#resultRuns-link"); self.ab("click", "#resultRuns-link"); self.ab("wait", "#result-run-update-1")
        default_result = self.evaluate("fetch('/secure/core/module/bp/result/runs/1',{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{status:'audit'}})}).then(async r=>({status:r.status,body:await r.text()}))")
        default_detail = self.evaluate("fetch('/secure/core/module/bp/result/runs/1',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        default_status = json.loads(default_detail["body"]).get("status") if default_detail["status"] == 200 else None
        self.check("default result write remains denied", default_result["status"] == 403 and default_status == "done")
        self.report["http_cases"].append({"route":"PUT /secure/core/module/bp/result/runs/1","role":"manager","organization":"org-a","mode":"default","status":403,"expected_status":403,"label":"default-write-denial"})
        self.ab("click", "#logout"); self.ab("wait", "--text", "已退出登录。"); self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        allowed = [event for event in events if event.get("action") == "result-run-update" and event.get("outcome") == "ALLOWED"]
        item_actions = [event for event in events if event.get("action") in ("result-value-update", "result-value-calculate", "result-review", "result-approve") and event.get("outcome") == "ALLOWED"]
        denied = [event for event in events if event.get("resource") == "result.write" and event.get("outcome") == "DENIED"]
        self.check("result status update has allowed audit", len(allowed) >= 1)
        self.check("result item workflow has entry calculation review and approval audits", {event.get("action") for event in item_actions} >= {"result-value-update", "result-value-calculate", "result-review", "result-approve"})
        self.check("result write denial has denied audit", len(denied) >= 2)
        self.check("result audit excludes credentials", all(value not in contents for value in self.secrets))
        self.report["audit"] = {"events": allowed + item_actions + denied, "expected":[
            {"action":"result-run-update","actorId":"matrix-manager-a","resource":"1","outcome":"ALLOWED"},
            {"action":"result-value-update","actorId":"matrix-manager-a","resource":"101","outcome":"ALLOWED"},
            {"action":"result-value-calculate","actorId":"matrix-manager-a","resource":"101","outcome":"ALLOWED"},
            {"action":"result-review","actorId":"matrix-manager-a","resource":"101","outcome":"ALLOWED"},
            {"action":"result-approve","actorId":"matrix-manager-a","resource":"101","outcome":"ALLOWED"},
            {"action":"authorize","actorId":"matrix-manager-a","resource":"result.write","outcome":"DENIED"}], "credentials_excluded":True}


def main():
    parser = module.argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--output", type=Path,
        default=ROOT.parent / ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/legacy/result-run-write-distribution-browser-20261025.json",
    )
    args = parser.parse_args()
    guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-result-write-browser-private-") as temporary:
        replay = ResultWriteBrowserReplay(temporary)
        try:
            replay.build(); replay.database(); replay.ui(); replay.audit(); replay.report["status"] = "executed-pass"
        except Exception as failure:
            replay.report["reason"] = replay.redact(str(failure)); replay.report["diagnostic_traceback"] = replay.redact(traceback.format_exc()); replay.diagnostics()
        finally:
            replay.cleanup(); replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists(); replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]; replay.report["finished_at"] = replay.now(); args.output.parent.mkdir(parents=True, exist_ok=True); args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists(); replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]; args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    print("result write browser: %s (%d checks)" % (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__": raise SystemExit(main())
