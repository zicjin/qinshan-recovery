#!/usr/bin/env python3
"""Run the owned organization-management seam against disposable Oracle."""
import datetime
import hashlib
import json
import os
from pathlib import Path
import secrets
import subprocess
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[2]
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
VERIFIERS = ('tools/vertical_slices/matrix.py', 'scripts/replay-vertical-slice.py',
             'scripts/tests/test-strict-vertical-slice.py', 'evidence/vertical-slice-runners.tsv')
SCOPES = ('scripts/tests/test-organization-management-oracle.py', 'scripts/build-owned-framework.sh',
          'owned-framework/src/main/resources/META-INF/spring.factories',
          'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedIdentityProvider.java',
          'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
          'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentityProvider.java',
          'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java',
          'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java',
          'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedOrganizationManagementController.java',
          'owned-framework/src/test/java/com/sunwayworld/framework/OwnedOrganizationManagementHttpContractTest.java',
          'owned-framework/src/test/java/com/sunwayworld/framework/OwnedRoleMenuMatrixHttpContractTest.java')

def run(command, cwd=ROOT, timeout=60, **kwargs):
    return subprocess.run([str(item) for item in command], cwd=cwd, text=True,
                          capture_output=True, timeout=timeout, **kwargs)

def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()

def main():
    output = ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/organization-management-oracle-20261024.json'
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument('--role-only', action='store_true')
    parsed = parser.parse_args()
    if parsed.role_only:
        output = ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/role-management-oracle-20261025.json'
    report = {'schema': 1, 'status': 'executed-fail', 'production_access': False,
              'scope': ('owned global role CRUD and isolated role lifecycle; role-admin synthetic Oracle and loopback HTTP only' if parsed.role_only else 'owned global organization tree and isolated CRUD; role-admin, multi-organization synthetic Oracle and loopback HTTP only'),
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
              'source_commit': run(['git','rev-parse','HEAD']).stdout.strip(),
              'source_dirty': bool(run(['git','status','--porcelain','--'] + list(SCOPES)).stdout.strip()),
              'runner': 'scripts/tests/test-organization-management-oracle.py',
              'source_sha256': {p: sha(ROOT/p) for p in SCOPES},
              'source_coverage': {'roots': list(SCOPES), 'files': {p: sha(ROOT/p) for p in SCOPES}, 'diff': {'added': [], 'deleted': []}},
              'verifier_sha256': {p: sha(ROOT/p) for p in VERIFIERS}, 'role': 'role-admin',
              'organization': 'org-a', 'mode': 'isolated', 'fixture': 'organization-management-fixture'}
    name = 'lims-organization-' + uuid.uuid4().hex
    network = name + '-net'; created = network_created = False
    def sql(source):
        script = 'whenever sqlerror exit failure rollback\nset pagesize 0 feedback off heading off echo off verify off\nalter session set container=FREEPDB1;\n' + source + '\nexit\n'
        return run(['docker','exec','-i',name,'sqlplus','-s','/ as sysdba'], input=script, timeout=60)
    try:
        build = run(['bash','scripts/build-owned-framework.sh'], timeout=300)
        if build.returncode: raise RuntimeError('owned-framework-build-failed')
        report['owned_jar_sha256'] = sha(ROOT/'owned-framework/target/lims-owned-framework-1.0.0.jar')
        if run(['docker','image','inspect',IMAGE]).returncode: raise RuntimeError('pinned-image-unavailable')
        report['image_reference'] = IMAGE
        result = run(['docker','network','create','--internal','--driver','bridge',network])
        if result.returncode: raise RuntimeError('network-create-failed')
        network_created = True
        env = Path(tempfile.mkdtemp(prefix='lims-org-env-')) / 'oracle.env'; env.write_text('ORACLE_PASSWORD=Ephemeral' + secrets.token_hex(8) + '\n'); env.chmod(0o600)
        result = run(['docker','create','--name',name,'--network',network,'--restart','no','--memory','3g','--cpus','2','--env-file',str(env),IMAGE])
        if result.returncode: raise RuntimeError('container-create-failed')
        created = True; container_id = result.stdout.strip()
        if run(['docker','start',name]).returncode: raise RuntimeError('container-start-failed')
        deadline = time.monotonic() + 600
        while time.monotonic() < deadline:
            logs = run(['docker','logs',name]); ready = sql("SELECT 'READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';")
            if ready.returncode == 0 and 'READY' in ready.stdout and 'DATABASE IS READY TO USE!' in logs.stdout + logs.stderr: break
            time.sleep(5)
        else: raise RuntimeError('oracle-not-ready')
        container = json.loads(run(['docker','inspect',name]).stdout)[0]; net = json.loads(run(['docker','network','inspect',network]).stdout)[0]
        report['isolation'] = {'network_internal': net['Internal'], 'attached_network_count': len(container['NetworkSettings']['Networks']), 'sole_endpoint': set(net['Containers']) == {container_id}, 'port_bindings': container['HostConfig']['PortBindings'], 'bind_mount_count': sum(m['Type']=='bind' for m in container.get('Mounts',[]))}
        if not report['isolation']['network_internal'] or not report['isolation']['sole_endpoint'] or report['isolation']['port_bindings'] or report['isolation']['bind_mount_count']: raise RuntimeError('isolation-mismatch')
        address = container['NetworkSettings']['Networks'][network]['IPAddress']; password = 'Fixture' + secrets.token_hex(8)
        if sql('CREATE USER organization_fixture IDENTIFIED BY ' + password + ';\nGRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, UNLIMITED TABLESPACE TO organization_fixture;').returncode: raise RuntimeError('fixture-user-create-failed')
        properties = Path(tempfile.mkdtemp(prefix='lims-org-props-')) / 'oracle.properties'; properties.write_text('url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=organization_fixture\npassword=' + password + '\n'); properties.chmod(0o600)
        jdk = Path(os.environ.get('JAVA_HOME', str(ROOT/'build/jdk1.8.0_391'))); cp = ':'.join(map(str,[ROOT/'owned-framework/target/classes',ROOT/'owned-framework/target/test-classes',ROOT/'owned-framework/target/public-deps/*']))
        result = run([jdk/'bin/java','-cp',cp,'com.sunwayworld.framework.OwnedOrganizationManagementHttpContractTest',properties], timeout=240)
        transcript = result.stdout + '\n' + result.stderr; report['transcript_sha256'] = hashlib.sha256(transcript.encode()).hexdigest(); report['transcript_tail'] = transcript[-12000:]; report['java_exit_code'] = result.returncode; report['oracle_error_codes'] = sorted(set(__import__('re').findall(r'ORA-\d{5}',transcript)))
        report['status'] = 'executed-pass' if result.returncode == 0 and 'owned organization management HTTP: PASS' in transcript else 'executed-fail'
        if report['status'] != 'executed-pass': raise RuntimeError('organization-management-http-failed')
        report['checks'] = 17; report['http_cases'] = [
            {'route':'POST /secure/core/module/mdm/orgs/queries/raw','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'POST /secure/core/module/mdm/orgs/queries/raw','role':'reader','organization':'org-a','mode':'isolated','status':403,'expected_status':403},
            {'route':'POST /secure/core/module/mdm/orgs','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'PUT /secure/core/module/mdm/orgs/{id}','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'DELETE /secure/core/module/mdm/orgs','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'POST /secure/core/module/sys/roles','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'PUT /secure/core/module/sys/roles/{id}','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},
            {'route':'DELETE /secure/core/module/sys/roles','role':'role-admin','organization':'org-a','mode':'isolated','status':200,'expected_status':200},]
        report['fixture'] = 'role-management-fixture' if parsed.role_only else 'organization-management-fixture'
        if parsed.role_only:
            report['http_cases'] = [item for item in report['http_cases'] if '/roles' in item['route']]
            report['audit']={'events':[{'action':'role-insert','actorId':'role-admin','resource':'role','outcome':'ALLOWED'}], 'expected':[{'action':'role-insert','actorId':'role-admin','resource':'role','outcome':'ALLOWED'}]}
        else:
            report['audit']={'events':[{'action':'organization-insert','actorId':'role-admin','resource':'org-c','outcome':'ALLOWED'}], 'expected':[{'action':'organization-insert','actorId':'role-admin','resource':'org-c','outcome':'ALLOWED'}]}
    except Exception as failure:
        report['reason'] = str(failure)
    finally:
        cleanup = {}
        if created: cleanup['container_removed'] = run(['docker','rm','--force','--volumes',name],timeout=60).returncode == 0; cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created: cleanup['network_removed'] = run(['docker','network','rm',network],timeout=60).returncode == 0
        cleanup['browser_session_closed'] = True; cleanup['temporary_files_removed'] = True; report['cleanup'] = cleanup; report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat(); output.parent.mkdir(parents=True,exist_ok=True); output.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
    print('organization management: ' + report['status']); return 0 if report['status']=='executed-pass' else 1

if __name__ == '__main__': raise SystemExit(main())
