#!/usr/bin/env python3
"""Replay bounded order-retain reads through the shipped distribution."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse, hashlib, importlib.util, json, tempfile, traceback
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
spec=importlib.util.spec_from_file_location('permission_matrix',Path(__file__).with_name('run-permission-matrix.py')); module=importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
from vertical_slices import role_fixture
SOURCE_SCOPES=module.SOURCE_SCOPES+('owned-distribution/tests/run-order-retain-browser.py',)

class OrderRetainReplay(module.PermissionReplay):
    def build(self):
        module.SOURCE_SCOPES=SOURCE_SCOPES; super().build(); self.report.update(runner='owned-distribution/tests/run-order-retain-browser.py',scope='owned distribution bounded order-retain list/filter/detail and isolated disposal handling; synthetic Oracle and Redis only',role='manager',organization='org-a',mode='isolated',fixture='order-retain-handle-browser-fixture')
        self.report['browser_source_sha256']={p:hashlib.sha256((ROOT/p).read_bytes()).hexdigest() for p in ('owned-web/src/main/resources/static/index.html','owned-web/src/main/resources/static/owned-web/app.js','owned-web/src/main/resources/static/owned-web/order-retains.js','owned-distribution/tests/run-order-retain-browser.py')}
    def database(self):
        super().database(); self.query("""CREATE TABLE T_EMC_ORDER (ID NUMBER PRIMARY KEY,ORDERNO VARCHAR2(64),ORGID VARCHAR2(64),SAMPLECOMPLETEDATE TIMESTAMP);
CREATE TABLE T_EMC_ORDER_RETAIN (ID NUMBER PRIMARY KEY,ORDERID NUMBER,PROJID NUMBER,FOLDERID NUMBER,RETAINQTY NUMBER,RETAINTIMELIMIT TIMESTAMP,RETAINLOC VARCHAR2(100),RETAINUSERID VARCHAR2(64),HANDLEUSERID VARCHAR2(64),HANDLEDATE DATE,HANDLERECORD VARCHAR2(255),CREATEDBYID VARCHAR2(64),CREATEDBYORGID VARCHAR2(64));
INSERT INTO T_CORE_MENU (ID,MENUNAME,ORDERNO) VALUES ('300200','Order retain',11);
INSERT INTO T_CORE_MENU (ID,MENUNAME,ORDERNO) VALUES ('300201','Order retain write',12);
INSERT INTO T_CORE_ROLE_PERMISSION VALUES (102,'T_CORE_MENU','300200');
INSERT INTO T_CORE_ROLE_PERMISSION VALUES (102,'T_CORE_MENU','300201');
INSERT INTO T_CORE_ROLE_PERMISSION VALUES (103,'T_CORE_MENU','300200');
INSERT INTO T_EMC_ORDER VALUES (1,'ORD-A-001','org-a',TIMESTAMP '2026-01-01 00:00:00');
INSERT INTO T_EMC_ORDER VALUES (2,'ORD-B-001','org-b',TIMESTAMP '2026-01-02 00:00:00');
INSERT INTO T_EMC_ORDER_RETAIN VALUES (1,1,101,201,2,TIMESTAMP '2026-06-01 00:00:00','Shelf A','matrix-manager-a',NULL,NULL,NULL,'matrix-manager-a','org-a');
INSERT INTO T_EMC_ORDER_RETAIN VALUES (2,2,102,202,3,TIMESTAMP '2026-06-02 00:00:00','Shelf B','matrix-manager-b',NULL,NULL,NULL,'matrix-manager-b','org-b');
COMMIT;""",'order retain fixture')
    def ui(self):
        self.configuration_overrides = {
            'owned.identity.order-retain-menu-ids': '300200',
            'owned.identity.order-retain-write-menu-ids': '300201',
            'owned.order-retain.write.mode': 'isolated',
        }
        self.start('isolated')
        actor = next(a for a in role_fixture.actors() if a['user_id'] == 'matrix-manager-a')
        self.ab('fill', '#login-id', actor['user_id']); self.ab('fill', '#login-password', self.credential); self.ab('click', '#login-submit')
        self.ab('wait', '#workspace-view:not([hidden])'); self.ab('wait', '#orderRetains-link'); self.ab('click', '#orderRetains-link')
        self.ab('wait', '#order-retains-table'); self.ab('wait', '[data-order-retain-id="1"]')
        self.check('order retain page renders through distribution', '留样记录' in self.text('#owned-workspace'))
        self.check('order retain hides foreign organization row', int(self.evaluate("document.querySelectorAll('#order-retains-table tbody tr').length")) == 1)
        self.ab('fill', '#order-retain-order-no', 'ORD-A-001'); self.ab('find', 'role', 'button', 'click', '--name', '查询', '--exact')
        self.ab('wait', '--fn', "document.querySelectorAll('#order-retains-table tbody tr').length === 1 && document.querySelector('#order-retains-table tbody tr')?.getAttribute('data-order-retain-id') === '1'")
        self.check('order retain order filter narrows rows', int(self.evaluate("document.querySelectorAll('#order-retains-table tbody tr').length")) == 1)
        detail = self.evaluate("fetch('/secure/core/module/bp/order/order-retains/1',{method:'POST',headers:{'Content-Type':'application/json'},body:'{}'}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check('order retain detail returns same organization row', detail['status'] == 200 and json.loads(detail['body']).get('id') == 1)
        self.ab('click', '#order-retain-handle-1'); self.ab('fill', '#order-retain-handle-record', 'Disposed safely'); self.ab('click', '#order-retain-handle-save'); self.ab('wait', '--text', '留样已处置。')
        handled = self.query("SELECT HANDLEUSERID||'|'||HANDLERECORD FROM T_EMC_ORDER_RETAIN WHERE ID=1 AND HANDLEDATE IS NOT NULL;", 'order retain handle persistence')
        self.check('isolated order retain handling persists actor, date and record', handled == 'matrix-manager-a|Disposed safely')
        self.query("UPDATE T_EMC_ORDER_RETAIN SET HANDLEUSERID=NULL,HANDLEDATE=NULL,HANDLERECORD=NULL WHERE ID=1;\nCOMMIT;", 'order retain handle fixture restore')
        self.ab('click', '#order-retain-create'); self.ab('fill', '#order-retain-order-id', '1'); self.ab('fill', '#order-retain-project-id', '101'); self.ab('fill', '#order-retain-folder-id', '201'); self.ab('fill', '#order-retain-qty', '4'); self.ab('fill', '#order-retain-days', '30'); self.ab('fill', '#order-retain-location', 'Shelf C'); self.ab('click', '#order-retain-save'); self.ab('wait', '--text', 'Shelf C')
        created_id = self.evaluate("(() => { const row = Array.from(document.querySelectorAll('#order-retains-table tbody tr')).find(r => r.textContent.includes('Shelf C')); return row && row.getAttribute('data-order-retain-id'); })()")
        self.check('isolated order retain insert renders scoped row', created_id not in (None, ''))
        self.ab('click', '#order-retain-delete-' + str(created_id)); self.ab('wait', '--fn', "!document.body.textContent.includes('Shelf C')")
        restored = self.query("SELECT COUNT(*) FROM T_EMC_ORDER_RETAIN WHERE RETAINLOC='Shelf C';", 'order retain browser baseline')
        self.check('order retain insert/delete restores baseline', restored == '0')
        self.report['http_cases'] = [
            {'route':'GET /?page=orderRetains','role':'manager','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'POST /secure/core/module/bp/order/order-retains/queries/raw','role':'manager','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'POST /secure/core/module/bp/order/order-retains/1','role':'manager','organization':'org-a','mode':'isolated','status':200,'expected_status':200,'label':'order-retain-detail'},
            {'route':'POST /secure/core/module/bp/order/order-retains/action/handle','role':'manager','organization':'org-a','mode':'isolated','status':200,'expected_status':200,'label':'order-retain-handle'},
            {'route':'POST /secure/core/module/bp/order/order-retains','role':'manager','organization':'org-a','mode':'isolated','status':200,'expected_status':200,'label':'order-retain-insert'},
            {'route':'DELETE /secure/core/module/bp/order/order-retains/{id}','role':'manager','organization':'org-a','mode':'isolated','status':200,'expected_status':200,'label':'order-retain-delete'},
        ]
        self.ab('click', '#logout'); self.ab('wait', '--text', '已退出登录。')
        reader = next(a for a in role_fixture.actors() if a['user_id'] == 'matrix-reader-a')
        self.ab('fill', '#login-id', reader['user_id']); self.ab('fill', '#login-password', self.credential); self.ab('click', '#login-submit'); self.ab('wait', '#workspace-view:not([hidden])')
        self.check('reader cannot open order retain page', '留样记录' not in self.text('#menu-nav')); self.close()
        self.configuration_overrides = {'owned.identity.order-retain-menu-ids': '300200'}
        self.start('default'); self.ab('fill', '#login-id', actor['user_id']); self.ab('fill', '#login-password', self.credential); self.ab('click', '#login-submit'); self.ab('wait', '#workspace-view:not([hidden])'); self.ab('wait', '#orderRetains-link'); self.ab('click', '#orderRetains-link'); self.ab('wait', '#order-retains-table')
        denied = self.evaluate("fetch('/secure/core/module/bp/order/order-retains',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({p:{orderId:1,projId:101,folderId:201,retainQty:1,retainDays:2,retainLoc:'Denied'}})}).then(async r=>({status:r.status,body:await r.text()}))")
        self.check('default order retain write remains denied', denied['status'] == 403)
        self.report['http_cases'].append({'route':'POST /secure/core/module/bp/order/order-retains','role':'manager','organization':'org-a','mode':'default','status':403,'expected_status':403,'label':'default-order-retain-denial'})
        self.ab('click', '#logout'); self.ab('wait', '--text', '已退出登录。'); self.close()

    def audit(self):
        contents='\n'.join(p.read_text() for p in self.audit_paths); events=[json.loads(line) for line in contents.splitlines() if line.strip()]; allowed=[e for e in events if e.get('action')=='authorize' and e.get('resource')=='order-retain.read' and e.get('outcome')=='ALLOWED']; writes=[e for e in events if e.get('action') in ('order-retain-insert','order-retain-delete','order-retain-handle') and e.get('outcome')=='ALLOWED']; denied=[e for e in events if e.get('resource')=='order-retain.write' and e.get('outcome')=='DENIED']; self.check('order retain read has allowed audit',len(allowed)>=1); self.check('order retain insert/delete/handle have allowed audit', {e.get('action') for e in writes} >= {'order-retain-insert','order-retain-delete','order-retain-handle'}); self.check('order retain default denial has denied audit',len(denied)>=1); self.check('order retain audit excludes credentials',all(v not in contents for v in self.secrets)); self.report['audit']={'events':allowed+writes+denied,'expected':[{'action':'authorize','actorId':'matrix-manager-a','resource':'order-retain.read','outcome':'ALLOWED'},{'action':'order-retain-insert','actorId':'matrix-manager-a','outcome':'ALLOWED'},{'action':'order-retain-handle','actorId':'matrix-manager-a','outcome':'ALLOWED'},{'action':'order-retain-delete','actorId':'matrix-manager-a','outcome':'ALLOWED'},{'action':'authorize','actorId':'matrix-manager-a','resource':'order-retain.write','outcome':'DENIED'}],'credentials_excluded':True}

def main():
    parser=module.argparse.ArgumentParser(description=__doc__); parser.add_argument('--output',type=Path,default=ROOT/'.scratch/sunway-supplier-exit-api-first/archive/historical/order-retain-distribution-browser-20261025.json'); args=parser.parse_args(); guard_output_path(args.output)
    with tempfile.TemporaryDirectory(prefix='lims-order-retain-browser-private-') as temporary:
        replay=OrderRetainReplay(temporary)
        try: replay.build(); replay.database(); replay.ui(); replay.audit(); replay.report['status']='executed-pass'
        except Exception as failure: replay.report['reason']=replay.redact(str(failure)); replay.report['diagnostic_traceback']=replay.redact(traceback.format_exc()); replay.diagnostics()
        finally: replay.cleanup(); replay.report['cleanup']['private_temporary_directory_removed']=not Path(temporary).exists(); replay.report['cleanup']['temporary_files_removed']=replay.report['cleanup']['private_temporary_directory_removed']; replay.report['finished_at']=replay.now(); args.output.parent.mkdir(parents=True,exist_ok=True); args.output.write_text(replay.redact(json.dumps(replay.report,ensure_ascii=False,indent=2))+'\n')
    replay.report['cleanup']['private_temporary_directory_removed']=not Path(temporary).exists(); replay.report['cleanup']['temporary_files_removed']=replay.report['cleanup']['private_temporary_directory_removed']; args.output.write_text(replay.redact(json.dumps(replay.report,ensure_ascii=False,indent=2))+'\n'); print('order retain browser: %s (%d checks)'%(replay.report['status'],len(replay.report['checks']))); return 0 if replay.report['status']=='executed-pass' else 1
if __name__=='__main__': raise SystemExit(main())
