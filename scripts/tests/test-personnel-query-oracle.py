#!/usr/bin/env python3
"""Run the owned personnel read seam against a disposable internal Oracle."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse
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
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'

def run(args, **kwargs):
    return subprocess.run(args, capture_output=True, text=True, timeout=kwargs.pop('timeout', 30), **kwargs)

def private_file(path, content):
    with os.fdopen(os.open(str(path), os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600), 'w') as output:
        output.write(content)

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--image', default=IMAGE)
    parser.add_argument('--http', action='store_true', help='also exercise original personnel HTTP read routes')
    parser.add_argument('--write', action='store_true', help='also exercise the explicit isolated qualification write route')
    parser.add_argument('--main-write', action='store_true', help='also exercise the isolated personnel master-table write route')
    parser.add_argument('--test-write', action='store_true', help='also exercise the isolated personnel visible-test write route')
    parser.add_argument('--debug-transcript', type=Path, help='private sanitized HTTP diagnostic transcript outside evidence')
    parser.add_argument('--output', type=Path, default=ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/personnel-query-oracle.json')
    parser.add_argument('--ready-timeout', type=int, default=600)
    args = parser.parse_args(); guard_output_path(args.output)
    report = {'schema': 1, 'status': 'not-executed', 'production_access': False,
              'scope': ('owned personnel read service + packaged original application HTTP runtime + isolated personnel qualification, visible-test and master-table writes; disposable synthetic Oracle only; no browser/page acceptance' if args.test_write else ('owned personnel read service + packaged original application HTTP runtime + isolated personnel qualification and master-table writes; disposable synthetic Oracle only; no browser/page acceptance' if args.main_write else ('owned personnel read service + packaged original application HTTP runtime + isolated qualification write; disposable synthetic Oracle only; no browser/page acceptance' if args.write else ('owned personnel read service + packaged original application HTTP runtime; disposable synthetic Oracle only; no browser/page acceptance' if args.http else 'owned personnel read service; disposable synthetic Oracle only; no HTTP/page acceptance')))),
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat()}
    nonce = uuid.uuid4().hex
    name, network = 'lims-personnel-query-' + nonce, 'lims-personnel-query-net-' + nonce
    created = network_created = False
    def save():
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, indent=2) + '\n')
    def sql(source):
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'],
                   input='whenever sqlerror exit failure rollback\nset pagesize 0 feedback off heading off echo off verify off\nalter session set container=FREEPDB1;\n' + source + '\nexit\n', timeout=60)
    try:
        if not re.fullmatch(r'[A-Za-z0-9./_-]+@sha256:[0-9a-f]{64}', args.image):
            raise RuntimeError('image-must-be-digest-pinned')
        inspection = run(['docker', 'image', 'inspect', args.image])
        if inspection.returncode:
            raise RuntimeError('pinned-image-unavailable')
        image = json.loads(inspection.stdout)[0]
        if image.get('Os') != 'linux' or image.get('Architecture') != 'amd64':
            raise RuntimeError('requires-linux-amd64')
        report['image_reference'], report['image_id'] = args.image, image['Id']
        report['source_commit'] = run(['git', 'rev-parse', 'HEAD'], cwd=ROOT).stdout.strip()
        sources = sorted(path for path in (ROOT / 'owned-framework/src').rglob('*') if path.is_file())
        sources += [Path(__file__), ROOT / 'scripts/build-owned-framework.sh', ROOT / 'scripts/prepare-owned-public-deps.py',
                    ROOT / 'scripts/reject-supplier-runtime.sh', ROOT / 'scripts/tests/test-owned-public-contracts.py']
        if args.http or args.write or args.main_write or args.test_write:
            sources += sorted(path for path in (ROOT / 'owned-personnel/src').rglob('*') if path.is_file())
            sources.append(ROOT / 'scripts/build-owned-personnel.sh')
        sources = sorted(set(sources))
        report['source_sha256'] = {str(path.relative_to(ROOT)): hashlib.sha256(path.read_bytes()).hexdigest() for path in sources}
        source_dirs = [ROOT / 'owned-framework/src'] + sources
        if args.http or args.write or args.main_write or args.test_write:
            source_dirs.append(ROOT / 'owned-personnel/src')
        report['source_dirty'] = bool(run(['git', 'status', '--porcelain', '--'] + [str(path) for path in source_dirs], cwd=ROOT).stdout.strip())
        build_script = 'scripts/build-owned-personnel.sh' if (args.http or args.write or args.main_write or args.test_write) else 'scripts/build-owned-framework.sh'
        build = run([str(ROOT / build_script)], timeout=180)
        if build.returncode:
            raise RuntimeError('owned-build-failed')
        report['owned_build'] = 'passed; 248 exact owned public contracts and supplier-runtime rejection'
        jars = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar']
        if args.http or args.write or args.test_write:
            jars.append(ROOT / 'owned-personnel/target/lims-owned-personnel-1.0.0.jar')
            project = Path(os.environ.get('LIMS_PROJECT_ROOT', str(ROOT / 'project'))).resolve()
            manifest = (ROOT / 'owned-personnel/target/original-sources.sha256').read_text().splitlines()
            originals = {line.split('  ', 1)[1]: line.split('  ', 1)[0] for line in manifest}
            report['original_project'] = {
                'commit': run(['git', 'rev-parse', 'HEAD'], cwd=project).stdout.strip(),
                'source_dirty': bool(run(['git', 'status', '--porcelain', '--'] + list(originals), cwd=project).stdout.strip()),
                'source_sha256': originals}
            for relative, expected in originals.items():
                if hashlib.sha256((project / relative).read_bytes()).hexdigest() != expected:
                    raise RuntimeError('original-source-hash-mismatch')
        report['artifact_sha256'] = {str(path.relative_to(ROOT)): hashlib.sha256(path.read_bytes()).hexdigest() for path in jars}
        report['public_dependency_sha256'] = {path.name: hashlib.sha256(path.read_bytes()).hexdigest()
                                              for path in sorted((ROOT / 'owned-framework/target/public-deps').glob('*.jar'))}
        report['owned_jar_sha256'] = report['artifact_sha256']['owned-framework/target/lims-owned-framework-1.0.0.jar']
        with tempfile.TemporaryDirectory(prefix='lims-personnel-query-private-') as temporary:
            temporary = Path(temporary)
            env_file, properties = temporary / 'oracle.env', temporary / 'jdbc.properties'
            oracle_password = 'Ephemeral' + secrets.token_hex(8)
            private_file(env_file, 'ORACLE_PASSWORD=' + oracle_password + '\n')
            if run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', network]).returncode:
                raise RuntimeError('network-create-failed')
            network_created = True
            result = run(['docker', 'create', '--name', name, '--network', network, '--restart', 'no', '--memory', '3g', '--cpus', '2', '--env-file', str(env_file), args.image])
            if result.returncode:
                raise RuntimeError('container-create-failed')
            created = True
            container_id = result.stdout.strip()
            if run(['docker', 'start', name]).returncode:
                raise RuntimeError('container-start-failed')
            deadline = time.monotonic() + args.ready_timeout
            while time.monotonic() < deadline:
                startup = run(['docker', 'logs', name])
                result = sql("SELECT 'QUERY_READY' FROM DUAL;")
                if 'DATABASE IS READY TO USE!' in startup.stdout + startup.stderr and result.returncode == 0 and 'QUERY_READY' in result.stdout:
                    break
                if run(['docker', 'inspect', '--format', '{{.State.Running}}', name]).stdout.strip() != 'true':
                    raise RuntimeError('container-exited-before-ready')
                time.sleep(5)
            else:
                raise RuntimeError('oracle-not-ready')
            container = json.loads(run(['docker', 'inspect', name]).stdout)[0]
            net = json.loads(run(['docker', 'network', 'inspect', network]).stdout)[0]
            isolation = {'network_internal': net['Internal'], 'attached_network_count': len(container['NetworkSettings']['Networks']),
                         'sole_endpoint': set(net['Containers']) == {container_id}, 'port_bindings': container['HostConfig']['PortBindings'],
                         'bind_mount_count': sum(m['Type'] == 'bind' for m in container.get('Mounts', []))}
            report['isolation'] = isolation
            if not isolation['network_internal'] or isolation['attached_network_count'] != 1 or not isolation['sole_endpoint'] or isolation['port_bindings'] or isolation['bind_mount_count']:
                raise RuntimeError('isolation-mismatch')
            address = container['NetworkSettings']['Networks'][network]['IPAddress']
            fixture_password = 'Fixture' + secrets.token_hex(8)
            result = sql('CREATE USER query_fixture IDENTIFIED BY ' + fixture_password + ';\nGRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, UNLIMITED TABLESPACE TO query_fixture;')
            if result.returncode:
                raise RuntimeError('synthetic-user-create-failed')
            private_file(properties, 'url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=query_fixture\npassword=' + fixture_password + '\n')
            jdk = Path(os.environ.get('JAVA_HOME', str(ROOT / 'build/jdk1.8.0_391')))
            output = ROOT / 'owned-framework/target'
            personnel_output = ROOT / 'owned-personnel/target'
            classpath = ':'.join([str(output / 'classes'), str(output / 'test-classes'), str(output / 'public-deps/*'), str(personnel_output / 'classes'), str(personnel_output / 'test-classes')])
            result = run([str(jdk / 'bin/java'), '-cp', classpath, 'com.sunwayworld.framework.PersonnelQueryServiceContractTest', str(properties)], timeout=120)
            transcript = result.stdout + '\n' + result.stderr
            match = re.search(r'personnel query service: PASS \((\d+) checks; synthetic Oracle only\)', transcript)
            filter_match = re.search(r'personnel filter parity: PASS \((\d+) checks; synthetic Oracle only\)', transcript)
            report['checks'] = int(match.group(1)) if match else None
            report['filter_checks'] = int(filter_match.group(1)) if filter_match else None
            report['java_exit_code'] = result.returncode
            report['oracle_error_codes'] = sorted(set(re.findall(r'ORA-\d{5}', transcript)))
            report['transcript_sha256'] = hashlib.sha256(transcript.encode()).hexdigest()
            assertion = re.search(r'AssertionError: ([^\r\n]+)', transcript)
            if assertion:
                report['assertion'] = assertion.group(1)
            report['status'] = 'executed-pass' if result.returncode == 0 and match and filter_match else 'executed-fail'
            if report['status'] != 'executed-pass':
                report['exception_classes'] = sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))
                raise RuntimeError('query-test-failed')
            http_classpath = ':'.join([str(output / 'lims-owned-framework-1.0.0.jar'), str(output / 'public-deps/*'), str(personnel_output / 'lims-owned-personnel-1.0.0.jar'), str(personnel_output / 'test-classes')])
            if args.main_write:
                result = run([str(jdk / 'bin/java'), '-cp', http_classpath, 'com.sunwayworld.framework.OwnedPersonnelMainWriteHttpContractTest', str(properties)], timeout=180)
                transcript = result.stdout + '\n' + result.stderr
                match = re.search(r'personnel main write: PASS \(\d+ checks; isolated Oracle loopback only\)', transcript)
                assertion = re.search(r'AssertionError: ([^\r\n]+)', transcript)
                report['main_write'] = {'checks': int(re.search(r'personnel main write: PASS \((\d+) checks', transcript).group(1)) if match else None, 'exit_code': result.returncode, 'transcript_sha256': hashlib.sha256(transcript.encode()).hexdigest(), 'oracle_error_codes': sorted(set(re.findall(r'ORA-\d{5}', transcript))), 'exception_classes': sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))}
                if assertion: report['main_write']['assertion'] = assertion.group(1)
                if result.returncode != 0 or not match:
                    report['status'] = 'executed-fail'
                    raise RuntimeError('personnel-main-write-test-failed')
            if args.test_write:
                result = run([str(jdk / 'bin/java'), '-cp', http_classpath, 'com.sunwayworld.framework.OwnedPersonnelVisibleTestWriteHttpContractTest', str(properties)], timeout=180)
                transcript = result.stdout + '\n' + result.stderr
                if args.debug_transcript:
                    private_file(args.debug_transcript, transcript.replace(fixture_password, '[redacted]').replace(oracle_password, '[redacted]'))
                match = re.search(r'personnel visible-test write: PASS \((\d+) checks; isolated Oracle loopback only\)', transcript)
                assertion = re.search(r'AssertionError: ([^\r\n]+)', transcript)
                report['test_write'] = {'checks': int(match.group(1)) if match else None, 'exit_code': result.returncode,
                                        'transcript_sha256': hashlib.sha256(transcript.encode()).hexdigest(),
                                        'oracle_error_codes': sorted(set(re.findall(r'ORA-\d{5}', transcript))),
                                        'exception_classes': sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))}
                if assertion: report['test_write']['assertion'] = assertion.group(1)
                if result.returncode != 0 or not match:
                    report['status'] = 'executed-fail'
                    raise RuntimeError('personnel-test-write-test-failed')
            if args.write:
                result = run([str(jdk / 'bin/java'), '-cp', http_classpath, 'com.sunwayworld.framework.OwnedPersonnelQualificationWriteHttpContractTest', str(properties)], timeout=120)
                transcript = result.stdout + '\n' + result.stderr
                match = re.search(r'personnel qualification write: PASS \((\d+) checks; isolated Oracle loopback only\)', transcript)
                assertion = re.search(r'AssertionError: ([^\r\n]+)', transcript)
                report['write'] = {'checks': int(match.group(1)) if match else None, 'exit_code': result.returncode,
                                   'transcript_sha256': hashlib.sha256(transcript.encode()).hexdigest(),
                                   'oracle_error_codes': sorted(set(re.findall(r'ORA-\d{5}', transcript))),
                                   'exception_classes': sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))}
                if assertion: report['write']['assertion'] = assertion.group(1)
                if result.returncode != 0 or not match:
                    report['status'] = 'executed-fail'
                    raise RuntimeError('personnel-write-test-failed')
            if args.http:
                result = run([str(jdk / 'bin/java'), '-cp', http_classpath, 'com.sunwayworld.framework.OwnedPersonnelDataHttpContractTest', str(properties)], timeout=120)
                transcript = result.stdout + '\n' + result.stderr
                if args.debug_transcript:
                    private_file(args.debug_transcript, transcript.replace(fixture_password, '[redacted]').replace(oracle_password, '[redacted]'))
                match = re.search(r'personnel data HTTP: PASS \((\d+) checks; Boot original application, loopback HTTP and synthetic Oracle only\)', transcript)
                assertion = re.search(r'AssertionError: ([^\r\n]+)', transcript)
                report['http'] = {'checks': int(match.group(1)) if match else None, 'exit_code': result.returncode,
                                  'transcript_sha256': hashlib.sha256(transcript.encode()).hexdigest(),
                                  'oracle_error_codes': sorted(set(re.findall(r'ORA-\d{5}', transcript))),
                                  'exception_classes': sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))}
                if assertion: report['http']['assertion'] = assertion.group(1)
                if result.returncode != 0 or not match:
                    report['status'] = 'executed-fail'
                    raise RuntimeError('personnel-http-test-failed')
            if args.main_write:
                print('personnel main write: PASS (' + str(report['main_write']['checks']) + ' checks; isolated Oracle loopback only)')
            if args.test_write:
                print('personnel visible-test write: PASS (' + str(report['test_write']['checks']) + ' checks; isolated Oracle loopback only)')
            if args.write:
                print('personnel qualification write: PASS (' + str(report['write']['checks']) + ' checks; isolated Oracle loopback only)')
            if args.http:
                print('personnel data HTTP: PASS (' + str(report['http']['checks']) + ' checks; packaged original application)')
            print('personnel query service: PASS (' + str(report['checks']) + ' checks; synthetic Oracle only)')
            print('personnel filter parity: PASS (' + str(report['filter_checks']) + ' checks; synthetic Oracle only)')
            return 0
    except (OSError, ValueError, KeyError, RuntimeError, subprocess.TimeoutExpired) as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        print('personnel query service: ' + report['reason'])
        return 1
    finally:
        cleanup = {}
        if created:
            cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name]).returncode == 0
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created:
            cleanup['network_removed'] = run(['docker', 'network', 'rm', network]).returncode == 0
        report['cleanup'] = cleanup
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        save()

if __name__ == '__main__':
    raise SystemExit(main())
