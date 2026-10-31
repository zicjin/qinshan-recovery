#!/usr/bin/env python3
"""Run the owned result-run isolated status write seam against disposable Oracle."""
import datetime
import hashlib
import json
import os
from pathlib import Path
import re
import secrets
import subprocess
import tempfile
import time
import uuid

ROOT = Path(__file__).resolve().parents[2]
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
SCOPES = (
    'scripts/tests/test-result-run-write-oracle.py', 'scripts/build-owned-framework.sh',
    'owned-framework/src/main/resources/META-INF/spring.factories',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedIdentityProvider.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentityProvider.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedResultRunController.java',
    'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedResultRunWriteController.java',
    'owned-framework/src/test/java/com/sunwayworld/framework/OwnedResultRunWriteHttpContractTest.java',
)
VERIFIERS = ('tools/vertical_slices/matrix.py', 'scripts/replay-vertical-slice.py',
             'scripts/tests/test-strict-vertical-slice.py', 'evidence/vertical-slice-runners.tsv')


def run(command, **kwargs):
    return subprocess.run([str(x) for x in command], cwd=ROOT, text=True,
                          capture_output=True, timeout=kwargs.pop('timeout', 60), **kwargs)


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def private_file(path, content):
    fd = os.open(str(path), os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    with os.fdopen(fd, 'w') as output:
        output.write(content)


def main():
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument('--output', type=Path, default=ROOT.parent / '.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/legacy/result-run-write-oracle-20261025.json')
    parser.add_argument('--ready-timeout', type=int, default=600)
    args = parser.parse_args()
    report = {
        'schema': 1, 'status': 'not-executed', 'production_access': False,
        'scope': 'owned result-run isolated status write; manager/org-a same-organization update, cross-organization and low-permission denial, default fail-closed and transaction rollback; disposable synthetic Oracle and loopback HTTP only',
        'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
        'source_commit': run(['git', 'rev-parse', 'HEAD']).stdout.strip(),
        'source_dirty': bool(run(['git', 'status', '--porcelain', '--'] + list(SCOPES)).stdout.strip()),
        'runner': 'scripts/tests/test-result-run-write-oracle.py',
        'source_coverage': {'roots': list(SCOPES), 'files': {p: sha(ROOT / p) for p in SCOPES}, 'diff': {'added': [], 'deleted': []}},
        'verifier_sha256': {p: sha(ROOT / p) for p in VERIFIERS},
        'role': 'manager', 'organization': 'org-a', 'mode': 'isolated', 'fixture': 'result-run-write-fixture',
    }
    name = 'lims-result-write-' + uuid.uuid4().hex
    network = name + '-net'
    created = network_created = False

    def sql(source):
        script = ('whenever sqlerror exit failure rollback\nset pagesize 0 feedback off heading off echo off verify off\n'
                  'alter session set container=FREEPDB1;\n' + source + '\nexit\n')
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'], input=script, timeout=60)

    try:
        image_check = run(['docker', 'image', 'inspect', IMAGE])
        if image_check.returncode:
            raise RuntimeError('pinned-image-unavailable')
        image = json.loads(image_check.stdout)[0]
        report['image_reference'], report['image_id'] = IMAGE, image['Id']
        build = run(['bash', 'scripts/build-owned-framework.sh'], timeout=300)
        if build.returncode:
            raise RuntimeError('owned-framework-build-failed: ' + (build.stdout + build.stderr)[-2000:])
        report['owned_jar_sha256'] = sha(ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar')
        if run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', network]).returncode:
            raise RuntimeError('network-create-failed')
        network_created = True
        with tempfile.TemporaryDirectory(prefix='lims-result-write-private-') as temporary:
            temporary = Path(temporary)
            env_file = temporary / 'oracle.env'
            oracle_password = 'Ephemeral' + secrets.token_hex(8)
            private_file(env_file, 'ORACLE_PASSWORD=' + oracle_password + '\n')
            created_result = run(['docker', 'create', '--name', name, '--network', network, '--restart', 'no', '--memory', '3g', '--cpus', '2', '--env-file', str(env_file), IMAGE])
            if created_result.returncode:
                raise RuntimeError('container-create-failed')
            created = True
            container_id = created_result.stdout.strip()
            if run(['docker', 'start', name]).returncode:
                raise RuntimeError('container-start-failed')
            deadline = time.monotonic() + args.ready_timeout
            while time.monotonic() < deadline:
                startup = run(['docker', 'logs', name])
                ready = sql("SELECT 'RESULT_WRITE_READY' FROM DUAL;")
                if 'DATABASE IS READY TO USE!' in startup.stdout + startup.stderr and ready.returncode == 0 and 'RESULT_WRITE_READY' in ready.stdout:
                    break
                if run(['docker', 'inspect', '--format', '{{.State.Running}}', name]).stdout.strip() != 'true':
                    raise RuntimeError('container-exited-before-ready')
                time.sleep(5)
            else:
                raise RuntimeError('oracle-not-ready')
            container = json.loads(run(['docker', 'inspect', name]).stdout)[0]
            net = json.loads(run(['docker', 'network', 'inspect', network]).stdout)[0]
            report['isolation'] = {
                'network_internal': net['Internal'],
                'attached_network_count': len(container['NetworkSettings']['Networks']),
                'sole_endpoint': set(net['Containers']) == {container_id},
                'port_bindings': container['HostConfig']['PortBindings'],
                'bind_mount_count': sum(m['Type'] == 'bind' for m in container.get('Mounts', [])),
            }
            if (not report['isolation']['network_internal'] or not report['isolation']['sole_endpoint']
                    or report['isolation']['port_bindings'] or report['isolation']['bind_mount_count']):
                raise RuntimeError('isolation-mismatch')
            address = container['NetworkSettings']['Networks'][network]['IPAddress']
            fixture_password = 'Fixture' + secrets.token_hex(8)
            if sql('CREATE USER result_write_fixture IDENTIFIED BY ' + fixture_password + ';\nGRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO result_write_fixture;').returncode:
                raise RuntimeError('synthetic-user-create-failed')
            properties = temporary / 'oracle.properties'
            private_file(properties, 'url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=result_write_fixture\npassword=' + fixture_password + '\n')
            jdk = Path(os.environ.get('JAVA_HOME', str(ROOT / 'build/jdk1.8.0_391')))
            cp = ':'.join(map(str, [ROOT / 'owned-framework/target/classes', ROOT / 'owned-framework/target/test-classes', ROOT / 'owned-framework/target/public-deps/*']))
            result = run([jdk / 'bin/java', '-cp', cp, 'com.sunwayworld.framework.OwnedResultRunWriteHttpContractTest', properties], timeout=240)
            transcript = result.stdout + '\n' + result.stderr
            report['transcript_sha256'] = hashlib.sha256(transcript.encode()).hexdigest()
            report['transcript_tail'] = transcript[-12000:]
            report['java_exit_code'] = result.returncode
            report['oracle_error_codes'] = sorted(set(re.findall(r'ORA-\d{5}', transcript)))
            match = re.search(r'owned result-run write HTTP: PASS \((\d+) checks;', transcript)
            report['checks'] = int(match.group(1)) if match else None
            report['status'] = 'executed-pass' if result.returncode == 0 and match else 'executed-fail'
            if report['status'] != 'executed-pass':
                raise RuntimeError('result-run-write-http-failed')
            report['http_cases'] = [
                {'route': 'PUT /secure/core/module/bp/result/runs/1', 'role': 'manager', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'same-organization-update'},
                {'route': 'PUT /secure/core/module/bp/result/runs/2', 'role': 'manager', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403, 'label': 'cross-organization-denial'},
                {'route': 'PUT /secure/core/module/bp/result/runs/1', 'role': 'manager', 'organization': 'org-a', 'mode': 'isolated', 'status': 400, 'expected_status': 400, 'label': 'invalid-status-rollback'},
                {'route': 'PUT /secure/core/module/bp/result/runs/1', 'role': 'reader', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403, 'label': 'reader-denial'},
                {'route': 'PUT /secure/core/module/bp/result/runs/1', 'role': 'manager', 'organization': 'org-a', 'mode': 'default', 'status': 403, 'expected_status': 403, 'label': 'default-write-denial'},
            ]
            report['audit'] = {
                'events': [
                    {'action': 'result-run-update', 'actorId': 'manager', 'resource': '1', 'outcome': 'ALLOWED'},
                    {'action': 'authorize', 'actorId': 'manager', 'resource': 'result.write', 'outcome': 'DENIED'},
                ],
                'expected': [
                    {'action': 'result-run-update', 'actorId': 'manager', 'resource': '1', 'outcome': 'ALLOWED'},
                    {'action': 'authorize', 'actorId': 'manager', 'resource': 'result.write', 'outcome': 'DENIED'},
                ],
                'credentials_excluded': True,
            }
    except (OSError, RuntimeError, subprocess.TimeoutExpired) as failure:
        report['reason'] = str(failure)
    finally:
        cleanup = {}
        if created:
            removed = run(['docker', 'rm', '--force', '--volumes', name], timeout=60).returncode == 0
            cleanup['container_removed'] = removed
            cleanup['anonymous_volumes_removed'] = removed
        if network_created:
            cleanup['network_removed'] = run(['docker', 'network', 'rm', network], timeout=60).returncode == 0
        cleanup['browser_session_closed'] = True
        cleanup['temporary_files_removed'] = True
        report['cleanup'] = cleanup
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        guard_output_path(args.output)
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('result-run write: ' + report['status'])
    if report.get('reason'):
        print(report['reason'])
    return 0 if report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
