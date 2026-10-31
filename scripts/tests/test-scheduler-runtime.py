#!/usr/bin/env python3
"""Packaged original-main scheduler test entry; synthetic files and network isolation."""
import argparse
import datetime
import hashlib
import json
import os
from pathlib import Path
import secrets
import shutil
import subprocess
import tempfile
import time
import urllib.request
import urllib.error

ROOT=Path(__file__).resolve().parents[2]
FIXTURE=b'isolated-scheduler-fixture-v1'
EXPECTED_HASH='78de065df9c382f476ab86c7d4ea55917a795fa0a8e0b3923a480174f64e5fd7'


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output',type=Path,default=ROOT/'build/scheduler-runtime.json')
    parser.add_argument('--inside',type=Path,help=argparse.SUPPRESS)
    parser.add_argument('--browser',action='store_true',help='run named browser UI checks; use outside bwrap')
    parser.add_argument('--skip-build',action='store_true')
    args=parser.parse_args()
    if not args.inside:
        if not args.skip_build:
            subprocess.run([str(ROOT/'scripts/build-owned-scheduler.sh')],check=True)
        if not args.browser:
            with tempfile.TemporaryDirectory(prefix='scheduler-net-isolated-') as temp:
                command=['bwrap','--unshare-net','--ro-bind','/','/','--tmpfs','/tmp','--bind',temp,'/tmp/host-output','--dev','/dev','--proc','/proc','--',
                         'python3',str(Path(__file__).resolve()),'--inside','/tmp','--output','/tmp/host-output/report.json']
                result=subprocess.run(command,capture_output=True,text=True,timeout=180)
                report=Path(temp)/'report.json'
                args.output.parent.mkdir(parents=True,exist_ok=True)
                if report.exists():shutil.copy2(report,args.output)
                print(result.stdout[-1500:]);print(result.stderr[-1500:])
                if result.returncode:raise SystemExit(result.returncode)
                return
    report={'schema':1,'status':'executed-fail','production_access':False,'scope':'original main, isolated scheduler test entry; historical scheduled jobs disabled',
            'role':'scheduler-test','organization':'n/a','mode':'isolated','fixture':'scheduler-replay-fixture',
            'started_at':datetime.datetime.now(datetime.timezone.utc).isoformat(),'checks':[],
            'historical_equivalence':'unknown; replay task is owned test infrastructure',
            'source_commit':subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip()}
    sources=[p for directory in ('owned-framework/src','owned-scheduler/src') for p in (ROOT/directory).rglob('*') if p.is_file()]
    sources += [ROOT/'scripts/build-owned-scheduler.sh',ROOT/'scripts/build-owned-framework.sh',ROOT/'scripts/prepare-owned-public-deps.py',Path(__file__).resolve()]
    report['source_dirty']=bool(subprocess.check_output(['git','status','--porcelain','--']+[str(p) for p in sources],cwd=ROOT,text=True).strip())
    report['source_sha256']={str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(sources)}
    jars=[ROOT/'owned-framework/target/lims-owned-framework-1.0.0.jar',ROOT/'owned-scheduler/target/lims-owned-scheduler-1.0.0.jar']
    report['artifact_sha256']={str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest() for p in jars}
    dependencies=ROOT/'owned-framework/target/public-deps'
    report['java_version']=subprocess.run([str(Path(os.environ.get('JAVA_HOME',str(ROOT/'build/jdk1.8.0_391')))/'bin/java'),'-version'],capture_output=True,text=True).stderr.strip()
    report['public_dependency_sha256']={p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(dependencies.glob('*.jar'))}
    historical=Path(os.environ.get('LIMS_PROJECT_ROOT',str(ROOT/'project')))
    original_paths=['src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java',
                    'src/main/java/com/sunwayworld/emc/module/mdm/equipt/equiptacqfile/service/impl/EmcEquiptAcqFileServiceImpl.java',
                    'src/main/java/com/sunwayworld/emc/module/mdm/equipt/equiptacqfile/service/impl/PfEquiptAcqFileServiceImpl.java']
    report['historical_source_commit']=subprocess.check_output(['git','rev-parse','HEAD'],cwd=historical,text=True).strip()
    report['historical_source_dirty']=bool(subprocess.check_output(['git','status','--porcelain'],cwd=historical,text=True).strip())
    report['historical_source_sha256']={p:hashlib.sha256((historical/p).read_bytes()).hexdigest() for p in original_paths}
    def check(label,value):
        if not value:raise AssertionError(label)
        report['checks'].append(label)
    routes=Path('/proc/net/route').read_text().splitlines()[1:]
    report['isolation']={'network_namespace':bool(args.inside),'ipv4_routes':len(routes),'database_configured':False,'external_endpoint_configured':False,'network_interfaces':sorted(p.name for p in Path('/sys/class/net').iterdir())}
    if args.inside:check('network namespace has no external route',len(routes)==0)
    workspace=tempfile.TemporaryDirectory(prefix='scheduler-private-',dir=args.inside)
    root=Path(workspace.name); token=secrets.token_urlsafe(36);process=None;browser=None;log=None
    java=Path(os.environ.get('JAVA_HOME',str(ROOT/'build/jdk1.8.0_391')))/'bin/java'
    cp=':'.join(map(str,[ROOT/'owned-scheduler/target/test-classes']+jars+[dependencies/'*']))
    def start(mode='isolated-test'):
        nonlocal process,log
        for name in ('port','stop'):(root/name).unlink(missing_ok=True)
        (root/'test.properties').write_text(('owned.scheduler.mode='+mode+'\n' if mode else '')+'owned.scheduler.root='+str(root)+'\nowned.scheduler.token-file='+str(root/'token')+'\n')
        log=(root/'application.log').open('a')
        process=subprocess.Popen([str(java),'-cp',cp,'com.sunwayworld.owned.scheduler.OwnedSchedulerTestApplication',str(root/'test.properties'),str(root/'port'),str(root/'stop')],cwd=root,stdout=log,stderr=subprocess.STDOUT)
        deadline=time.monotonic()+40
        while not (root/'port').exists() and process.poll() is None and time.monotonic()<deadline:time.sleep(.1)
        if not (root/'port').exists():raise RuntimeError('original-main launch failed: '+(root/'application.log').read_text()[-2000:])
        return int((root/'port').read_text())
    def stop():
        nonlocal process,log
        if process:
            (root/'stop').touch()
            try:process.wait(timeout=15)
            except subprocess.TimeoutExpired:process.kill();process.wait()
            process=None
        if log:log.close();log=None
    port=None
    def http(method,path,body=None,access=None,expected=200):
        req=urllib.request.Request('http://127.0.0.1:'+str(port)+'/secure/owned-scheduler'+path,
            data=None if body is None else body.encode(),method=method)
        if access:req.add_header('X-Owned-Scheduler-Test-Token',access)
        if body is not None:req.add_header('Content-Type','application/x-www-form-urlencoded')
        try:
            with urllib.request.build_opener(urllib.request.ProxyHandler({})).open(req,timeout=10) as res:status,text=res.status,res.read().decode()
        except urllib.error.HTTPError as res:status,text=res.code,res.read().decode()
        check(method+' '+path+' HTTP '+str(expected),status==expected)
        try:return json.loads(text)
        except json.JSONDecodeError:return text
    def trigger(scenario,id,task='owned.scheduler.replay',expected=200):return http('POST','/tasks/'+task+'/trigger','scenario='+scenario+'&requestId='+id,token,expected)
    try:
        (root/'fixtures').mkdir();(root/'outputs').mkdir();(root/'fixtures/success.bin').write_bytes(FIXTURE)
        (root/'token').write_text(token);(root/'token').chmod(0o600)
        port=start()
        check('actual test page', 'Isolated scheduler test entry' in http('GET','/page'))
        http('GET','/tasks',expected=403);http('GET','/tasks',access='wrong',expected=403)
        tasks=http('GET','/tasks',access=token)
        check('historical scheduled declarations visibly disabled',len(tasks)==5 and sum(t['status']=='disabled-in-isolated-env' for t in tasks)==4)
        success=trigger('success','success-1');check('real file-copy success',success['status']=='SUCCESS' and success['attempt']==1 and success['source']=='isolated-replay')
        check('independent SHA-256 and output bytes',success['metadata']['sha256']==EXPECTED_HASH and (root/'outputs/success-1.bin').read_bytes()==FIXTURE)
        trigger('success','success-1');check('idempotence does not append attempts',len(http('GET','/tasks/owned.scheduler.replay/runs',access=token))==1)
        trigger('failure','success-1',expected=409)
        failure=trigger('failure','failure-1');check('missing-file failure retries bounded at three',failure['status']=='FAILED' and failure['attempt']==3 and not (root/'outputs/failure-1.bin').exists())
        retry=trigger('retry','retry-1');check('actual copy recovers on third attempt',retry['status']=='SUCCESS' and retry['attempt']==3 and (root/'outputs/retry-1.bin').read_bytes()==FIXTURE)
        history=http('GET','/tasks/owned.scheduler.replay/runs',access=token)
        check('failure and retry attempts individually observable',[r['status'] for r in history]==['SUCCESS','RETRYING','RETRYING','FAILED','RETRYING','RETRYING','SUCCESS'])
        disabled=trigger('success','disabled-1','dcuAcquisition',403);check('historical write task rejected',disabled['status']=='DISABLED')
        trigger('success','unknown-1','invented-job',404)
        http('POST','/tasks/owned.scheduler.replay/trigger','scenario=success&scenario=failure&requestId=duplicate',token,400)
        http('POST','/tasks/owned.scheduler.replay/trigger','scenario=success&requestId=../escape',token,400)
        http('POST','/tasks/owned.scheduler.replay/trigger','scenario=success&requestId=external&endpoint=http://external.invalid',token,400)
        (root/'token').write_text(secrets.token_urlsafe(36));before=(root/'runs.jsonl').read_bytes()
        http('POST','/tasks/owned.scheduler.replay/trigger','scenario=success&requestId=revoked',token,403)
        check('token revocation causes no task side effects',before==(root/'runs.jsonl').read_bytes());(root/'token').write_text(token)
        # Safety rejection uses a link pointing only at another disposable fixture.
        (root/'outputs/link-1.bin').symlink_to(root/'fixtures/success.bin')
        trigger('success','link-1',expected=503);check('symlink is rejected before copying', (root/'fixtures/success.bin').read_bytes()==FIXTURE)
        (root/'outputs/link-1.bin').unlink()
        stop();port=start()
        history=http('GET','/tasks/owned.scheduler.replay/runs',access=token)
        check('durable run history survives actual application restart',len(history)==7)
        trigger('success','success-1');check('idempotence survives restart',len(http('GET','/tasks/owned.scheduler.replay/runs',access=token))==7)
        if args.browser:
            browser='scheduler-'+secrets.token_hex(8)
            transcript=[]
            def ab(*command):
                result=subprocess.run(['agent-browser','--session',browser,*command],capture_output=True,text=True,timeout=50)
                if result.returncode:raise RuntimeError('browser '+command[0]+': '+result.stderr[:300])
                if command[0]!='fill':transcript.append(command[0]+'\n'+result.stdout)
                return result.stdout
            ab('open','http://127.0.0.1:'+str(port)+'/secure/owned-scheduler/page');snapshot=ab('snapshot','-i')
            check('browser sees isolated test entry controls','Load tasks' in snapshot and 'Trigger isolated replay' in snapshot)
            ab('fill','#token',token);ab('click','#load');ab('wait','--text','disabled-in-isolated-env')
            ab('click','#trigger');ab('wait','--text','SUCCESS');snapshot=ab('snapshot')
            check('real browser trigger returns source hash and success', 'SUCCESS' in snapshot and EXPECTED_HASH in snapshot and 'isolated-replay' in snapshot)
            ab('select','#scenario','failure');ab('click','#trigger');ab('wait','--text','FAILED')
            ab('select','#scenario','retry');ab('click','#trigger');ab('wait','--text','SUCCESS')
            ab('click','#history');ab('wait','--text','RETRYING');snapshot=ab('snapshot')
            check('browser observes failed retried and disabled states','RETRYING' in snapshot and 'FAILED' in snapshot and 'disabled-in-isolated-env' in snapshot)
            report['browser_transcript']=transcript
            ab('close');browser=None
        report['journal']=[json.loads(line) for line in (root/'runs.jsonl').read_text().splitlines()]
        stop();port=start(None);http('GET','/tasks',access=token,expected=404);stop()
        check('default/disabled mode has no scheduler routes',True)
        report['status']='executed-pass'
    except Exception as failure:
        report['failure']=str(failure)
    finally:
        if browser:subprocess.run(['agent-browser','--session',browser,'close'],capture_output=True,text=True,timeout=30)
        stop();workspace.cleanup();report['cleanup']={'application_stopped':process is None,'fixture_root_removed':not root.exists(),'database_changes':False,'sss2_changes':False}
        report['finished_at']=datetime.datetime.now(datetime.timezone.utc).isoformat()
        args.output.parent.mkdir(parents=True,exist_ok=True);args.output.write_text(json.dumps(report,ensure_ascii=True,indent=2)+'\n')
    print('scheduler runtime: '+report['status']+' ('+str(len(report['checks']))+' checks)')
    if report['status']!='executed-pass':print(report.get('failure'));raise SystemExit(1)


if __name__=='__main__':main()
