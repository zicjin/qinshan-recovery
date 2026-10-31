#!/usr/bin/env python3
"""Replay the bounded sample-test-run result query through the distribution."""
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
module = importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
from vertical_slices import role_fixture

RESULT_SOURCE_SCOPES = module.SOURCE_SCOPES + ("owned-distribution/tests/run-result-browser.py",)


class ResultBrowserReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES = RESULT_SOURCE_SCOPES
        super().build()
        self.report.update(
            runner="owned-distribution/tests/run-result-browser.py",
            scope="owned distribution bounded sample-test-run result read page; manager/org-a organization scope and reader denial; synthetic Oracle and Redis only",
            role="manager", organization="org-a", mode="isolated", fixture="result-run-browser-fixture")
        self.report["browser_source_sha256"] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest()
            for path in (
                "owned-web/src/main/resources/static/index.html",
                "owned-web/src/main/resources/static/owned-web/app.js",
                "owned-web/src/main/resources/static/owned-web/result-runs.js",
                "owned-distribution/tests/run-result-browser.py",
            )
        }

    def database(self):
        super().database()
        self.query(
            "CREATE TABLE T_EMC_RUN (ID NUMBER PRIMARY KEY,RUNNO VARCHAR2(64),STATUS VARCHAR2(36),ORGID VARCHAR2(64),EQUIPTID NUMBER,TESTID NUMBER);\n"
            "INSERT INTO T_CORE_MENU (ID,MENUNAME,ORDERNO) VALUES ('300100','Sample test runs',10);\n"
            "INSERT INTO T_CORE_ROLE_PERMISSION VALUES (102,'T_CORE_MENU','300100');\n"
            "INSERT INTO T_CORE_ROLE_PERMISSION VALUES (103,'T_CORE_MENU','300100');\n"
            "INSERT INTO T_EMC_RUN VALUES (1,'RUN-A-001','done','org-a',11,101);\n"
            "INSERT INTO T_EMC_RUN VALUES (2,'RUN-A-002','audit','org-a',12,102);\n"
            "INSERT INTO T_EMC_RUN VALUES (3,'RUN-B-001','done','org-b',13,103);\nCOMMIT;",
            "result run fixture")

    def ui(self):
        self.configuration_overrides = {"owned.identity.result-menu-ids": "300100"}
        self.start("isolated")
        actor = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-manager-a")
        self.ab("fill", "#login-id", actor["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit")
        self.ab("wait", "#workspace-view:not([hidden])"); self.ab("wait", "#resultRuns-link"); self.ab("click", "#resultRuns-link")
        self.ab("wait", "#result-runs-table"); self.ab("wait", "[data-result-run-id=\"1\"]")
        self.check("result page renders through distribution", "样品测试批结果" in self.text("#owned-workspace"))
        self.check("result query hides foreign organization row", int(self.evaluate("document.querySelectorAll('#result-runs-table tbody tr').length")) == 2)
        self.ab("fill", "#result-run-status", "audit"); self.ab("find", "role", "button", "click", "--name", "查询", "--exact"); self.ab("wait", "--fn", "document.querySelectorAll('#result-runs-table tbody tr').length === 1 && document.querySelector('#result-runs-table tbody tr')?.getAttribute('data-result-run-id') === '2'")
        self.check("result status filter narrows visible rows", int(self.evaluate("document.querySelectorAll('#result-runs-table tbody tr').length")) == 1)
        detail = self.evaluate("fetch('/secure/core/module/bp/result/runs/1',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("result detail returns same-organization run", detail["status"] == 200 and json.loads(detail["body"]).get("id") == 1 and json.loads(detail["body"]).get("orgId") == "org-a")
        foreign_detail = self.evaluate("fetch('/secure/core/module/bp/result/runs/3',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check("result detail suppresses foreign organization run", foreign_detail["status"] == 200 and json.loads(foreign_detail["body"]) == {})
        self.report["http_cases"] = [
            {"route":"GET /?page=resultRuns","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"POST /secure/core/module/bp/result/runs/queries/raw","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"POST /secure/core/module/bp/result/runs/1","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
            {"route":"POST /secure/core/module/bp/result/runs/3","role":"manager","organization":"org-a","mode":"isolated","status":200,"expected_status":200},
        ]
        self.ab("click", "#logout"); self.ab("wait", "--text", "已退出登录。")
        reader = next(a for a in role_fixture.actors() if a["user_id"] == "matrix-reader-a")
        self.ab("fill", "#login-id", reader["user_id"]); self.ab("fill", "#login-password", self.credential); self.ab("click", "#login-submit"); self.ab("wait", "#workspace-view:not([hidden])")
        self.check("reader cannot open result page", "样品测试批结果" not in self.text("#menu-nav")); self.close()

    def audit(self):
        contents = "\n".join(path.read_text() for path in self.audit_paths)
        events = [json.loads(line) for line in contents.splitlines() if line.strip()]
        allowed = [event for event in events if event.get("action") == "authorize" and event.get("resource") == "result.read" and event.get("outcome") == "ALLOWED"]
        self.check("result read has allowed audit", len(allowed) >= 1)
        self.check("result audit excludes credentials", all(value not in contents for value in self.secrets))
        self.report["audit"] = {"events": allowed, "expected":[{"action":"authorize","actorId":"matrix-manager-a","resource":"result.read","outcome":"ALLOWED"}], "credentials_excluded":True}


def main():
    parser = module.argparse.ArgumentParser(description=__doc__); parser.add_argument("--output", type=Path, default=ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/result-run-distribution-browser-20261025.json"); args = parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix="lims-result-browser-private-") as temporary:
        replay = ResultBrowserReplay(temporary)
        try: replay.build(); replay.database(); replay.ui(); replay.audit(); replay.report["status"] = "executed-pass"
        except Exception as failure: replay.report["reason"] = replay.redact(str(failure)); replay.report["diagnostic_traceback"] = replay.redact(traceback.format_exc()); replay.diagnostics()
        finally:
            replay.cleanup(); replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists(); replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]; replay.report["finished_at"] = replay.now(); args.output.parent.mkdir(parents=True, exist_ok=True); args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    replay.report["cleanup"]["private_temporary_directory_removed"] = not Path(temporary).exists(); replay.report["cleanup"]["temporary_files_removed"] = replay.report["cleanup"]["private_temporary_directory_removed"]; args.output.write_text(replay.redact(json.dumps(replay.report, ensure_ascii=False, indent=2)) + "\n")
    print("result browser: %s (%d checks)" % (replay.report["status"], len(replay.report["checks"])))
    return 0 if replay.report["status"] == "executed-pass" else 1


if __name__ == "__main__": raise SystemExit(main())
