#!/usr/bin/env python3
"""Exercise the owned identity API in a fresh synthetic, internal-network Oracle."""
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))
from scripts.evidence_output import guard_output_path
import argparse
import datetime
import hashlib
import ipaddress
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
    return subprocess.run(args, capture_output=True, text=True,
                          timeout=kwargs.pop('timeout', 30), **kwargs)


def private_file(path, content):
    with os.fdopen(os.open(str(path), os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600), 'w') as output:
        output.write(content)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--image', default=IMAGE)
    parser.add_argument('--output', type=Path, default=ROOT / 'build/personnel-identity-oracle.json')
    parser.add_argument('--ready-timeout', type=int, default=600)
    args = parser.parse_args(); guard_output_path(args.output)
    report = {'schema': 1, 'status': 'not-executed', 'production_access': False,
              'scope': 'owned identity API, HTTP/session bridge, authorized menu IDs, bounded personnel/equipment/consumable servlet authorization, and real Boot registration; disposable synthetic Oracle only; test guard marker is not resource or page acceptance',
              'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat()}
    nonce = uuid.uuid4().hex
    name, network = 'lims-identity-' + nonce, 'lims-identity-net-' + nonce
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
        sources = [
            Path(__file__),
            ROOT / 'scripts/build-owned-framework.sh',
            ROOT / 'owned-framework/src/main/resources/META-INF/spring.factories',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentityProvider.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedIdentityProvider.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedPrincipal.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/AuditEvent.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/AuditEventSink.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/InMemoryAuditEventSink.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpController.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedMenuController.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessConfiguration.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedQueryAccessFilter.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/RotatingFileAuditEventSink.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedLoginCredentialDecoder.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedRsaLoginCredentialDecoder.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedSecurityKeyController.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedLoginRequest.java',
            ROOT / 'owned-framework/src/test/java/com/sunwayworld/framework/JdbcPersonnelIdentityContractTest.java',
            ROOT / 'owned-framework/src/test/java/com/sunwayworld/framework/OwnedIdentityServletContractTest.java',
            ROOT / 'owned-framework/src/test/fixtures/identity/OwnedIdentityBootContractTest.java',
        ]
        report['source_sha256'] = {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest() for p in sources}
        report['source_dirty'] = bool(run(['git', 'status', '--porcelain', '--'] + [str(p) for p in sources], cwd=ROOT).stdout.strip())
        build = run([str(ROOT / 'scripts/build-owned-framework.sh')], timeout=120)
        if build.returncode:
            raise RuntimeError('owned-build-failed')
        report['owned_build'] = 'passed; declared public contracts and supplier-runtime rejection'
        report['owned_jar_sha256'] = hashlib.sha256((ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar').read_bytes()).hexdigest()
        with tempfile.TemporaryDirectory(prefix='lims-identity-private-') as temporary:
            temporary = Path(temporary)
            boot_classes = temporary / 'boot-classes'
            boot_classes.mkdir()
            jdk = Path(os.environ.get('JAVA_HOME', str(ROOT / 'build/jdk1.8.0_391')))
            output = ROOT / 'owned-framework/target'
            boot_classpath = ':'.join([str(boot_classes), str(output / 'lims-owned-framework-1.0.0.jar'), str(output / 'public-deps/*')])
            compilation = run([str(jdk / 'bin/javac'), '-source', '8', '-target', '8', '-cp', boot_classpath,
                               '-d', str(boot_classes), str(ROOT / 'owned-framework/src/test/fixtures/identity/OwnedIdentityBootContractTest.java')])
            if compilation.returncode:
                raise RuntimeError('boot-fixture-compilation-failed')
            env_file, properties = temporary / 'oracle.env', temporary / 'jdbc.properties'
            # Keep the disposable password within legacy Oracle password limits.
            oracle_password = 'Ephemeral' + secrets.token_hex(8)
            private_file(env_file, 'ORACLE_PASSWORD=' + oracle_password + '\n')
            result = run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', network])
            if result.returncode:
                raise RuntimeError('network-create-failed')
            network_created = True
            result = run(['docker', 'create', '--name', name, '--network', network, '--restart', 'no',
                          '--memory', '3g', '--cpus', '2', '--env-file', str(env_file), args.image])
            if result.returncode:
                raise RuntimeError('container-create-failed')
            created = True
            container_id = result.stdout.strip()
            if run(['docker', 'start', name]).returncode:
                raise RuntimeError('container-start-failed')
            deadline = time.monotonic() + args.ready_timeout
            while time.monotonic() < deadline:
                startup = run(['docker', 'logs', name])
                result = sql("SELECT 'IDENTITY_READY' FROM DUAL;")
                if 'DATABASE IS READY TO USE!' in startup.stdout + startup.stderr and result.returncode == 0 and 'IDENTITY_READY' in result.stdout:
                    break
                state = run(['docker', 'inspect', '--format', '{{.State.Running}}', name]).stdout.strip()
                if state != 'true':
                    raise RuntimeError('container-exited-before-ready')
                time.sleep(5)
            else:
                raise RuntimeError('oracle-not-ready')
            container = json.loads(run(['docker', 'inspect', name]).stdout)[0]
            net = json.loads(run(['docker', 'network', 'inspect', network]).stdout)[0]
            isolation = {'network_internal': net['Internal'],
                         'attached_network_count': len(container['NetworkSettings']['Networks']),
                         'sole_endpoint': set(net['Containers']) == {container_id},
                         'port_bindings': container['HostConfig']['PortBindings'],
                         'bind_mount_count': sum(m['Type'] == 'bind' for m in container.get('Mounts', []))}
            report['isolation'] = isolation
            if not isolation['network_internal'] or isolation['attached_network_count'] != 1 or not isolation['sole_endpoint'] or isolation['port_bindings'] or isolation['bind_mount_count']:
                raise RuntimeError('isolation-mismatch')
            address = container['NetworkSettings']['Networks'][network]['IPAddress']
            if not ipaddress.ip_address(address).is_private:
                raise RuntimeError('non-private-oracle-address')
            # Create a disposable least-privilege schema owner through the container-local SYS connection.
            fixture_password = 'Fixture' + secrets.token_hex(8)
            result = sql('CREATE USER identity_fixture IDENTIFIED BY ' + fixture_password + ';\nGRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO identity_fixture;')
            if result.returncode:
                report['oracle_error_codes'] = sorted(set(re.findall(r'ORA-\d{5}', result.stdout + result.stderr)))
                raise RuntimeError('synthetic-user-create-failed')
            private_file(properties, 'url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=identity_fixture\npassword=' + fixture_password + '\n')
            classpath = ':'.join(str(output / p) for p in ['classes', 'test-classes', 'public-deps/*'])
            result = run([str(jdk / 'bin/java'), '-cp', classpath,
                          'com.sunwayworld.framework.JdbcPersonnelIdentityContractTest', str(properties)], timeout=120)
            transcript = result.stdout + '\n' + result.stderr
            match = re.search(r'JDBC personnel identity: PASS \((\d+) checks; synthetic Oracle only\)', transcript)
            report['checks'] = int(match.group(1)) if match else None
            report['java_exit_code'] = result.returncode
            report['oracle_error_codes'] = sorted(set(re.findall(r'ORA-\d{5}', transcript)))
            report['transcript_sha256'] = hashlib.sha256(transcript.encode()).hexdigest()
            report['status'] = 'executed-pass' if result.returncode == 0 and match else 'executed-fail'
            if report['status'] != 'executed-pass':
                # No unrestricted Java/Oracle output: connection exceptions may contain endpoint data.
                report['exception_classes'] = sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))
                raise RuntimeError('identity-test-failed')
            servlet = run([str(jdk / 'bin/java'), '-cp', classpath,
                           'com.sunwayworld.framework.OwnedIdentityServletContractTest', str(properties)], timeout=120)
            servlet_transcript = servlet.stdout + '\n' + servlet.stderr
            if servlet.returncode != 0 or 'owned identity servlet: PASS' not in servlet_transcript:
                report['servlet_http'] = 'failed'
                report['status'] = 'executed-fail'
                report['servlet_exception_classes'] = sorted(set(re.findall(r'\b(?:java|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', servlet_transcript)))
                raise RuntimeError('servlet-http-test-failed')
            report['servlet_transcript_sha256'] = hashlib.sha256(servlet_transcript.encode()).hexdigest()
            report['servlet_http'] = 'passed; loopback public key, RSA login, invalid transport rejection, session permission, current menu IDs and Oracle revocation, logout'
            boot = run([str(jdk / 'bin/java'), '-cp', boot_classpath, 'OwnedIdentityBootContractTest',
                        str(properties), str(temporary / 'audit.jsonl')], cwd=temporary, timeout=120)
            boot_transcript = boot.stdout + '\n' + boot.stderr
            report['boot_transcript_sha256'] = hashlib.sha256(boot_transcript.encode()).hexdigest()
            boot_match = re.search(r'owned identity Boot HTTP: PASS \((\d+) checks;', boot_transcript)
            if boot.returncode != 0 or not boot_match:
                report['boot_http'] = 'failed'
                report['boot_assertions'] = re.findall(r'AssertionError: ([^\r\n]+)', boot_transcript)
                report['boot_exception_classes'] = sorted(set(re.findall(r'\b(?:java|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', boot_transcript)))
                raise RuntimeError('boot-http-test-failed')
            report['boot_checks'] = int(boot_match.group(1))
            report['boot_http'] = 'passed; packaged Boot routes, Oracle identity, session rotation, current menu ID grants/revocation/suspension, bounded three-domain query guard role matrix, readonly page restrictions, unknown-write rejection, current-session revocation/suspension, ambiguous paths, bounded file audit, no-datasource denial'
            print('JDBC personnel identity: PASS (' + str(report['checks']) + ' checks; synthetic Oracle only)')
            return 0
    except (OSError, ValueError, KeyError, RuntimeError, subprocess.TimeoutExpired) as failure:
        if report['status'] == 'executed-pass':
            report['status'] = 'executed-fail'
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        print('JDBC personnel identity: ' + report['reason'])
        return 1
    finally:
        cleanup = {}
        if created:
            try:
                cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name], timeout=60).returncode == 0
            except (OSError, subprocess.TimeoutExpired):
                cleanup['container_removed'] = False
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created:
            try:
                cleanup['network_removed'] = run(['docker', 'network', 'rm', network], timeout=60).returncode == 0
            except (OSError, subprocess.TimeoutExpired):
                cleanup['network_removed'] = False
        report['cleanup'] = cleanup
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        save()


if __name__ == '__main__':
    raise SystemExit(main())
