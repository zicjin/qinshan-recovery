#!/usr/bin/env python3
"""Public prepare -> actual verifier -> export -> finalize with fake Docker."""
import copy
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location('milestone_fixture', ROOT/'scripts/tests/test-local-runtime-milestone.py')
fixture = importlib.util.module_from_spec(spec); spec.loader.exec_module(fixture)

class LifecycleProofTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(); self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name); self.repo = self.root/'repo'; self.repo.mkdir()
        shutil.copytree(ROOT/'scripts', self.repo/'scripts', ignore=shutil.ignore_patterns('__pycache__'))
        shutil.copytree(ROOT/'tools', self.repo/'tools', ignore=shutil.ignore_patterns('__pycache__'))
        (self.repo/'project').mkdir(); (self.repo/'owned-framework').mkdir()
        self.call(['git','init','-q'], cwd=self.repo)
        self.call(['git','add','.'], cwd=self.repo)
        self.call(['git','-c','user.name=Test','-c','user.email=test@example.invalid','commit','-qm','fixture'], cwd=self.repo)
        self.private = self.root/'private'; self.private.mkdir()
        self.dist = self.private/'dist'; (self.dist/'app').mkdir(parents=True); (self.dist/'lib').mkdir()
        for name, data in [('app/lims-owned-application.jar','owned app'),('start.sh','owned start'),('lib/classpath.txt','public.jar\n'),('lib/public.jar','public dependency')]:
            (self.dist/name).write_text(data)
        self.docker = self.private/'docker'
        self.docker.write_text('''#!/usr/bin/env python3
import json,hashlib,os,sys
from pathlib import Path
args=sys.argv[1:]; assert args[:2]==['--host','unix:///var/run/docker.sock']; args=args[2:]
scenario=os.environ.get('FAKE_PHASE','prepare'); dist=Path(os.environ['FAKE_DIST'])
if args[:2]==['network','inspect']: print(json.dumps({'name':'lims-local-runtime-isolated','id':'d'*64,'internal':True}))
elif args[:1]==['inspect'] and args[-2]=='{{json .Mounts}}':
 print(json.dumps([{'Destination':'/opt/current-owned','Source':str(dist),'RW':False,'Type':'bind'}]))
elif args[:1]==['inspect']:
 role=args[-1].rsplit('-',1)[-1]
 print(json.dumps({'name':'/'+args[-1],'id':{'app':'a','oracle':'b','redis':'c'}[role]*64,'running':role!='app' or scenario!='cleanup','started_at':'2026-11-07T01:00:00Z','image_id':'sha256:'+'e'*64,'networks':{'lims-local-runtime-isolated':{'NetworkID':'d'*64}}}))
elif args[:1]==['exec']:
 assert args[1:4]==['lims-local-runtime-app','sha256sum','--']
 p=args[4]; local=dist/p.removeprefix('/opt/current-owned/')
 print(hashlib.sha256(local.read_bytes()).hexdigest()+'  '+p)
else: raise AssertionError(args)
'''); self.docker.chmod(0o755)
        self.env = dict(os.environ, FAKE_DIST=str(self.dist), FAKE_PHASE='prepare', SUNWAY_LIFECYCLE_PROOF_STORE=str(self.private/'store'))
        (self.private/'store').mkdir()
        self.snapshots = {'observation_before':fixture.observation('2026-11-07T02:00:00+00:00'), 'observation_after':fixture.observation('2026-11-07T04:00:00+00:00'),
            'baseline_before':fixture.fingerprint(fixture.OWNERS[0]),'baseline_after':fixture.fingerprint(fixture.OWNERS[0]),
            'runtime_before':fixture.fingerprint(fixture.OWNERS[1],'3'*64),'runtime_after':fixture.fingerprint(fixture.OWNERS[1],'3'*64)}
        self.pins = {}
        for name, data in self.snapshots.items():
            path=self.private/(name+'.json'); self.write(path,data); self.pins[name]=self.pin(path)
        seed={'before_files':{name:self.pins[name] for name in ('observation_before','baseline_before','runtime_before')}, 'distribution_dir':str(self.dist), 'milestone_id':'focused-read', 'clone_identity':'same-local-app', 'loopback_url':'http://127.0.0.1:18087'}
        self.write(self.private/'seed.json',seed)
        self.prepared_path=self.private/'prepared.json'
        self.cli('scripts/lifecycle_proof.py','--prepare-input',self.private/'seed.json','--docker',self.docker,'--output',self.prepared_path)
        self.prepared=json.loads(self.prepared_path.read_text())
        runtime=copy.deepcopy(self.prepared); runtime['prepared_manifest']=self.pin(self.prepared_path)
        self.report={**self.prepared,'schema':3,'status':'executed-pass-pending-cleanup','strict_evidence':True,'execution':'strict','batch_id':'batch-focused','runtime_contract':runtime,
            'started_at':'2026-11-07T02:30:00+00:00','finished_at':'2026-11-07T03:30:00+00:00','cases':[]}
        case={'id':'read','route':{'method':'GET','path':'/read','operation':'read'},'status':200,'fixture':{'restore':'not-required'},'audit':'not-required', 'request_fingerprint':'req-literal','response_fingerprint':'res-literal','stimulus_contract_version':'v1','normalizer_version':'v1','validation_mode':'executed','family_fingerprint':'family-literal','passed':True}
        for key in ('batch_id','application_binding','harness_binding','runtime_contract_binding'): case[key]=self.report[key]
        self.report['cases']=[case]
        self.report_path=self.private/'pending.json'; self.write(self.report_path,self.report)
        context={key:self.report[key] for key in ('batch_id','milestone_id','clone_identity','application_binding','harness_binding','runtime_contract_binding','artifact_sha256')}
        self.manifest={'schema_version':1,'kind':'local-runtime-milestone-inputs','files':{role:{'path':role+'.json','sha256':pin['sha256']} for role,pin in self.pins.items()},'context':context,'prepared_runtime':self.pin(self.prepared_path)}
        self.manifest_path=self.private/'manifest.json'; self.write(self.manifest_path,self.manifest)
        self.lifecycle_path=self.private/'lifecycle.json'

    def call(self,args,**kwargs):
        result=subprocess.run([str(a) for a in args],capture_output=True,text=True,timeout=60,**kwargs)
        self.assertEqual(result.returncode,0,result.stdout+result.stderr); return result
    def cli(self,script,*args,ok=True,phase=None):
        env=dict(self.env)
        if phase: env['FAKE_PHASE']=phase
        result=subprocess.run([sys.executable,str(self.repo/script),*[str(a) for a in args]],env=env,capture_output=True,text=True,timeout=60)
        if ok: self.assertEqual(result.returncode,0,result.stdout+result.stderr)
        else: self.assertNotEqual(result.returncode,0,result.stdout)
        return result
    def write(self,path,value): path.write_text(json.dumps(value))
    def pin(self,path): return {'path':str(path),'sha256':hashlib.sha256(path.read_bytes()).hexdigest()}
    def verify(self,phase='cleanup',ok=True):
        result=self.cli('scripts/verify-local-runtime-milestone.py','--manifest',self.manifest_path,'--docker',self.docker,phase=phase,ok=ok)
        self.lifecycle_path.write_text(result.stdout); return json.loads(result.stdout)
    def export(self,ok=True):
        return self.cli('scripts/lifecycle_proof.py','--export-cleanup','--report',self.report_path,'--lifecycle',self.lifecycle_path,'--output',self.private/'cleanup.json',ok=ok)
    def finalize(self,ok=True):
        return self.cli('tools/finalize_api_report.py','--input',self.report_path,'--cleanup-contract',self.private/'cleanup.json','--output',self.private/'final.json',ok=ok)

    def test_verified_read_only_report_exports_and_finalizes_after_same_app_restart(self):
        lifecycle=self.verify(); self.assertTrue(lifecycle['lifecycle_verified'])
        self.export()
        # No current runtime state is consulted when historical stop is replayed.
        self.env['FAKE_PHASE']='prepare'
        self.finalize()
        result=json.loads((self.private/'final.json').read_text())
        self.assertEqual(result['status'],'executed-pass')
        self.assertIsNone(result['runtime_contract']['runtime_schema_hash_after'])
        gate = self.call([sys.executable,'-c',
            'import json,sys; sys.path.insert(0,sys.argv[1]); from evidence_output import strict_case_contract; ok,reason=strict_case_contract(json.load(open(sys.argv[2]))); assert ok,reason',
            self.repo/'scripts',self.private/'final.json'],env=self.env)
        self.assertEqual(gate.returncode, 0)

    def test_running_application_cannot_export(self):
        self.verify(phase='prepare',ok=False); self.export(ok=False)

    def test_batch_source_artifact_identity_and_registration_mismatches_fail(self):
        self.verify(); self.export()
        original=copy.deepcopy(self.report)
        for key,value in [('batch_id','other'),('application_binding','app-other'),('artifact_sha256',{'other':'0'*64}),('clone_identity','other')]:
            self.report=copy.deepcopy(original); self.report[key]=value; self.write(self.report_path,self.report)
            self.finalize(ok=False)
        self.report=original; self.write(self.report_path,self.report)
        (self.private/'store/lifecycle-index.tsv').unlink(); self.finalize(ok=False)

    def test_snapshot_tamper_and_wrong_identity_cannot_export(self):
        self.verify()
        path=self.private/'runtime_after.json'; path.write_text(path.read_text()+' ')
        self.export(ok=False)

    def test_registered_bundle_tamper_cannot_finalize(self):
        self.verify(); self.export()
        obj=next((self.private/'store/sha256').iterdir()); obj.write_bytes(obj.read_bytes()+b' ')
        self.finalize(ok=False)

    def test_archived_proof_replays_when_original_private_captures_and_distribution_are_gone(self):
        self.verify(); self.export()
        shutil.rmtree(self.dist)
        for path in self.private.glob('*.json'):
            if path.name not in ('pending.json','cleanup.json'): path.unlink()
        self.finalize()


    def test_lifecycle_raw_context_and_snapshot_pins_are_rechecked_before_export(self):
        self.verify()
        original = json.loads(self.lifecycle_path.read_text())
        for field in ('context', 'inputs', 'cleanup_inspections'):
            with self.subTest(field=field):
                bad = copy.deepcopy(original)
                if field == 'context': bad[field]['batch_id'] = 'other'
                if field == 'inputs': bad[field]['runtime_after']['sha256'] = '0'*64
                if field == 'cleanup_inspections':
                    for row in bad[field]: row['containers']['app']['id'] = 'f'*64
                self.write(self.lifecycle_path, bad)
                self.export(ok=False)
        self.write(self.lifecycle_path, original)

    def test_manifest_unrelated_prepared_runtime_and_dirty_source_are_rejected(self):
        self.manifest['context']['milestone_id'] = 'other'
        self.write(self.manifest_path, self.manifest)
        failed = self.verify(ok=False)
        self.assertFalse(failed['lifecycle_verified'])
        self.assertEqual(failed['status'], 'diagnostic-failed')
        self.export(ok=False)

    def test_declared_or_local_only_write_proof_cannot_be_finalized(self):
        self.verify(); self.export()
        case = self.report['cases'][0]
        case['route'] = {'method':'POST','path':'/write','operation':'create'}
        case['fixture'] = {'restore':'restored','business_write_via':'http'}
        case['audit'] = {'status':'observed'}
        case['http_write_proof'] = {'schema':1,'storage':'local-pending','path':str(self.private/'unregistered'),'sha256':'0'*64}
        self.write(self.report_path, self.report)
        self.finalize(ok=False)

    def test_tampered_operated_artifact_refuses_preparation(self):
        # Acquisition checks the actual local and container-mapped files; no declared true seam.
        self.prepared['artifact_sha256'][str(self.dist/'app/lims-owned-application.jar')] = '0'*64
        self.write(self.prepared_path, self.prepared)
        self.verify(ok=False)

if __name__=='__main__': unittest.main()
