#!/usr/bin/env python3
"""Run the owned consumables read seam against a disposable internal Oracle."""
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
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path
IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'

def run(args, **kwargs):
    return subprocess.run(args, capture_output=True, text=True, timeout=kwargs.pop('timeout', 30), **kwargs)

def private_file(path, content):
    with os.fdopen(os.open(str(path), os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600), 'w') as output:
        output.write(content)

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--image', default=IMAGE)
    parser.add_argument('--http', action='store_true', default=True, help='exercise original consumables HTTP read routes')
    parser.add_argument('--debug-transcript', type=Path, help='private sanitized HTTP diagnostic transcript outside evidence')
    parser.add_argument('--output', type=Path, default=ROOT.parent / '.scratch/sunway-supplier-exit-api-first/diagnostic-evidence/legacy/consumables-runtime-oracle.json')
    parser.add_argument('--ready-timeout', type=int, default=600)
    args = parser.parse_args()
    report = {'schema': 1, 'status': 'not-executed', 'strict_evidence': False, 'production_access': False,
              'scope': ('owned consumables read service + packaged original application HTTP runtime; disposable synthetic Oracle only; no browser/page acceptance' if args.http else 'owned consumables read service; disposable synthetic Oracle only; no HTTP/page acceptance'),
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat()}
    nonce = uuid.uuid4().hex
    name, network = 'lims-consumables-query-' + nonce, 'lims-consumables-query-net-' + nonce
    created = network_created = False
    def save():
        guard_output_path(args.output)
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
        if args.http:
            sources += sorted(path for path in (ROOT / 'owned-consumables/src').rglob('*') if path.is_file())
            sources.append(ROOT / 'scripts/build-owned-consumables.sh')
        sources = sorted(set(sources))
        report['source_sha256'] = {str(path.relative_to(ROOT)): hashlib.sha256(path.read_bytes()).hexdigest() for path in sources}
        source_dirs = [ROOT / 'owned-framework/src'] + sources
        if args.http:
            source_dirs.append(ROOT / 'owned-consumables/src')
        report['source_dirty'] = bool(run(['git', 'status', '--porcelain', '--'] + [str(path) for path in source_dirs], cwd=ROOT).stdout.strip())
        build_script = 'scripts/build-owned-consumables.sh' if args.http else 'scripts/build-owned-framework.sh'
        build = run([str(ROOT / build_script)], timeout=180)
        if build.returncode:
            raise RuntimeError('owned-build-failed')
        report['owned_build'] = 'passed; exact owned public contracts and supplier-runtime rejection'
        jars = [ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar']
        if args.http:
            jars.append(ROOT / 'owned-consumables/target/lims-owned-consumables-1.0.0.jar')
            project = Path(os.environ.get('LIMS_PROJECT_ROOT', str(ROOT / 'project'))).resolve()
            manifest = (ROOT / 'owned-consumables/target/original-sources.sha256').read_text().splitlines()
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
        with tempfile.TemporaryDirectory(prefix='lims-consumables-query-private-') as temporary:
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
            result = sql('CREATE USER query_fixture IDENTIFIED BY ' + fixture_password + ';\nGRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO query_fixture;')
            if result.returncode:
                raise RuntimeError('synthetic-user-create-failed')
            private_file(properties, 'url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=query_fixture\npassword=' + fixture_password + '\n')
            jdk = Path(os.environ.get('JAVA_HOME', str(ROOT / 'build/jdk1.8.0_391')))
            output = ROOT / 'owned-framework/target'
            consumables_output = ROOT / 'owned-consumables/target'
            classpath = ':'.join([str(output / 'classes'), str(output / 'test-classes'), str(output / 'public-deps/*'), str(consumables_output / 'classes'), str(consumables_output / 'test-classes')])
            report['status'] = 'executed-pass'
            if args.http:
                http_classpath = ':'.join([str(output / 'lims-owned-framework-1.0.0.jar'), str(output / 'public-deps/*'), str(consumables_output / 'lims-owned-consumables-1.0.0.jar'), str(consumables_output / 'test-classes')])
                result = run([str(jdk / 'bin/java'), '-cp', http_classpath, 'com.sunwayworld.framework.OwnedConsumablesRuntimeHttpTest', str(properties)], timeout=120)
                transcript = result.stdout + '\n' + result.stderr
                if args.debug_transcript:
                    private_file(args.debug_transcript, transcript.replace(fixture_password, '[redacted]').replace(oracle_password, '[redacted]'))
                match = re.search(r'consumables runtime HTTP: PASS \((\d+) checks; Boot original application, loopback HTTP and synthetic Oracle only\)', transcript)
                assertion = re.search(r'AssertionError: ([^\r\n]+)', transcript)
                report['http'] = {'checks': int(match.group(1)) if match else None, 'exit_code': result.returncode,
                                  'transcript_sha256': hashlib.sha256(transcript.encode()).hexdigest(),
                                  'oracle_error_codes': sorted(set(re.findall(r'ORA-\d{5}', transcript))),
                                  'exception_classes': sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))}
                if assertion: report['http']['assertion'] = assertion.group(1)
                if result.returncode != 0 or not match:
                    report['status'] = 'executed-fail'
                    raise RuntimeError('consumables-http-test-failed')
            if args.http:
                print('consumables runtime HTTP: PASS (' + str(report['http']['checks']) + ' checks; packaged original application)')

            return 0
    except (OSError, ValueError, KeyError, RuntimeError, subprocess.TimeoutExpired) as failure:
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        print('consumables query service: ' + report['reason'])
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
