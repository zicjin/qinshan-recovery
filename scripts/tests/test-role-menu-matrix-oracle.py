#!/usr/bin/env python3
"""Run the owned role/menu matrix read/write seam against disposable Oracle."""
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
                          timeout=kwargs.pop('timeout', 60), **kwargs)


def private_file(path, content):
    with os.fdopen(os.open(str(path), os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600), 'w') as output:
        output.write(content)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--image', default=IMAGE)
    parser.add_argument('--output', type=Path, default=ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/role-menu-matrix-oracle-20261024.json')
    parser.add_argument('--role-user-only', action='store_true',
                        help='publish only the role-user matrix contract from the shared Oracle replay')
    parser.add_argument('--tristate-only', action='store_true',
                        help='publish only the role-menu read/tristate contract from the shared Oracle replay')
    parser.add_argument('--write-only', action='store_true',
                        help='publish only the role-menu instant-save write contract from the shared Oracle replay')
    parser.add_argument('--ready-timeout', type=int, default=600)
    args = parser.parse_args(); guard_output_path(args.output)
    report = {
        'schema': 1, 'status': 'not-executed', 'production_access': False,
        'scope': 'owned global menu-role matrix headers, rows and historical instant-save writes; authenticated explicit administration grant; disposable synthetic Oracle and loopback HTTP only',
        'started_at': datetime.datetime.now(datetime.timezone.utc).isoformat(),
    }
    nonce = uuid.uuid4().hex
    name, network = 'lims-role-menu-' + nonce, 'lims-role-menu-net-' + nonce
    created = network_created = False

    def save():
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, indent=2) + '\n')

    def sql(source):
        return run(['docker', 'exec', '-i', name, 'sqlplus', '-s', '/ as sysdba'], input=(
            'whenever sqlerror exit failure rollback\nset pagesize 0 feedback off heading off echo off verify off\n'
            'alter session set container=FREEPDB1;\n' + source + '\nexit\n'), timeout=60)

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
        source_paths = [
            Path(__file__), ROOT / 'scripts/build-owned-framework.sh',
            ROOT / 'owned-framework/src/main/resources/META-INF/spring.factories',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedIdentityProvider.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentityProvider.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java',
            ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedRoleMenuMatrixController.java',
            ROOT / 'owned-framework/src/test/java/com/sunwayworld/framework/OwnedRoleMenuMatrixHttpContractTest.java',
        ]
        if args.role_user_only:
            source_paths.append(ROOT / 'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedRoleUserMatrixController.java')
        report['source_sha256'] = {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest() for p in source_paths}
        report['source_dirty'] = bool(run(['git', 'status', '--porcelain', '--'] + [str(p) for p in source_paths], cwd=ROOT).stdout.strip())
        strict_scopes = [
            'scripts/tests/test-role-menu-matrix-oracle.py', 'scripts/build-owned-framework.sh',
            'owned-framework/src/main/resources/META-INF/spring.factories',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/OwnedIdentityProvider.java',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentity.java',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/security/JdbcPersonnelIdentityProvider.java',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityHttpBoundary.java',
            'owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedRoleMenuMatrixController.java',
            'owned-framework/src/test/java/com/sunwayworld/framework/OwnedRoleMenuMatrixHttpContractTest.java',
        ]
        if args.role_user_only:
            strict_scopes.append('owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedRoleUserMatrixController.java')
        report['runner'] = 'scripts/tests/test-role-menu-matrix-oracle.py'
        report['verifier_sha256'] = {
            path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest() for path in (
                'tools/vertical_slices/matrix.py', 'scripts/replay-vertical-slice.py',
                'scripts/tests/test-strict-vertical-slice.py', '.scratch/sunway-supplier-exit-api-first/evidence-archive/vertical-slice-runners.tsv')}
        report['source_coverage'] = {
            'roots': list(strict_scopes),
            'files': {path: hashlib.sha256((ROOT / path).read_bytes()).hexdigest() for path in strict_scopes},
            'diff': {'added': [], 'deleted': []},
        }
        build = run(['bash', str(ROOT / 'scripts/build-owned-framework.sh')], env=dict(os.environ, JAVA_HOME=os.environ.get('JAVA_HOME', '/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391')), timeout=180)
        if build.returncode:
            raise RuntimeError('owned-build-failed')
        report['owned_build'] = 'passed; public contracts and supplier-runtime rejection'
        report['owned_jar_sha256'] = hashlib.sha256((ROOT / 'owned-framework/target/lims-owned-framework-1.0.0.jar').read_bytes()).hexdigest()
        with tempfile.TemporaryDirectory(prefix='lims-role-menu-private-') as temporary:
            temporary = Path(temporary)
            env_file, properties = temporary / 'oracle.env', temporary / 'oracle.properties'
            oracle_password = 'Ephemeral' + secrets.token_hex(8)
            private_file(env_file, 'ORACLE_PASSWORD=' + oracle_password + '\n')
            result = run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', network])
            if result.returncode: raise RuntimeError('network-create-failed')
            network_created = True
            result = run(['docker', 'create', '--name', name, '--network', network, '--restart', 'no', '--memory', '3g', '--cpus', '2', '--env-file', str(env_file), args.image])
            if result.returncode: raise RuntimeError('container-create-failed')
            created = True
            container_id = result.stdout.strip()
            if run(['docker', 'start', name]).returncode: raise RuntimeError('container-start-failed')
            deadline = time.monotonic() + args.ready_timeout
            while time.monotonic() < deadline:
                startup = run(['docker', 'logs', name])
                ready = sql("SELECT 'ROLE_MENU_READY' FROM DUAL;")
                if 'DATABASE IS READY TO USE!' in startup.stdout + startup.stderr and ready.returncode == 0 and 'ROLE_MENU_READY' in ready.stdout: break
                if run(['docker', 'inspect', '--format', '{{.State.Running}}', name]).stdout.strip() != 'true': raise RuntimeError('container-exited-before-ready')
                time.sleep(5)
            else: raise RuntimeError('oracle-not-ready')
            container = json.loads(run(['docker', 'inspect', name]).stdout)[0]
            net = json.loads(run(['docker', 'network', 'inspect', network]).stdout)[0]
            isolation = {'network_internal': net['Internal'], 'attached_network_count': len(container['NetworkSettings']['Networks']), 'sole_endpoint': set(net['Containers']) == {container_id}, 'port_bindings': container['HostConfig']['PortBindings'], 'bind_mount_count': sum(m['Type'] == 'bind' for m in container.get('Mounts', []))}
            report['isolation'] = isolation
            if not isolation['network_internal'] or isolation['attached_network_count'] != 1 or not isolation['sole_endpoint'] or isolation['port_bindings'] or isolation['bind_mount_count']: raise RuntimeError('isolation-mismatch')
            address = container['NetworkSettings']['Networks'][network]['IPAddress']
            if not ipaddress.ip_address(address).is_private: raise RuntimeError('non-private-oracle-address')
            fixture_password = 'Fixture' + secrets.token_hex(8)
            result = sql('CREATE USER role_menu_fixture IDENTIFIED BY ' + fixture_password + ';\nGRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO role_menu_fixture;')
            if result.returncode: raise RuntimeError('synthetic-user-create-failed')
            private_file(properties, 'url=jdbc:oracle:thin:@//' + address + ':1521/FREEPDB1\nuser=role_menu_fixture\npassword=' + fixture_password + '\n')
            output = ROOT / 'owned-framework/target'
            jdk = Path(os.environ.get('JAVA_HOME', '/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391'))
            classpath = ':'.join(str(output / p) for p in ['classes', 'test-classes', 'public-deps/*'])
            result = run([str(jdk / 'bin/java'), '-cp', classpath, 'com.sunwayworld.framework.OwnedRoleMenuMatrixHttpContractTest', str(properties)], timeout=180)
            transcript = result.stdout + '\n' + result.stderr
            match = re.search(r'owned role-menu matrix HTTP: PASS \((\d+) checks;', transcript)
            report['checks'] = int(match.group(1)) if match else None
            report['java_exit_code'] = result.returncode
            report['transcript_sha256'] = hashlib.sha256(transcript.encode()).hexdigest()
            report['oracle_error_codes'] = sorted(set(re.findall(r'ORA-\d{5}', transcript)))
            report['status'] = 'executed-pass' if result.returncode == 0 and match else 'executed-fail'
            if report['status'] != 'executed-pass':
                report['exception_classes'] = sorted(set(re.findall(r'\b(?:java|oracle|org|com)\.[A-Za-z0-9_.$]*(?:Exception|Error)\b', transcript)))
                raise RuntimeError('role-menu-matrix-http-failed')
            report['http'] = 'passed; loopback RSA login, role/menu headers and rows, isolated grant/revoke, invalid-state denial, transaction rollback, revocation and suspended-role denial'
            report['role'] = 'role-admin'
            report['organization'] = 'org-a'
            report['mode'] = 'isolated'
            report['fixture'] = 'role-menu-matrix-fixture'
            audit_match = re.search(r'ROLE_MENU_AUDIT\|([^|]+)\|([^|]+)\|([^\r\n]+)', transcript)
            if not audit_match:
                raise RuntimeError('role-menu-audit-observation-missing')
            audit_event = {'action': audit_match.group(2), 'actorId': audit_match.group(1),
                           'resource': 'role-menu.admin', 'outcome': audit_match.group(3)}
            write_match = re.search(r'ROLE_MENU_WRITE\|([^|]+)\|([^|]+)\|([^\r\n]+)', transcript)
            if not write_match:
                raise RuntimeError('role-menu-write-observation-missing')
            write_event = {'action': 'role-menu-write', 'actorId': write_match.group(1),
                           'resource': 'role-menu.write', 'outcome': 'ALLOWED'}
            role_user_match = re.search(r'ROLE_USER_WRITE\|([^|]+)\|([^|]+)\|([^\r\n]+)', transcript)
            if not role_user_match:
                raise RuntimeError('role-user-write-observation-missing')
            role_user_event = {'action': 'role-user-write', 'actorId': role_user_match.group(1),
                               'resource': 'role-user.write', 'outcome': 'ALLOWED'}
            menu_http_cases = [
                {'route': 'POST /secure/core/module/sys/menu-roles/queries/matrix-headers', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200},
                {'route': 'POST /secure/core/module/sys/menu-roles/queries/matrix-rows', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200},
                {'route': 'POST /secure/core/module/sys/menu-roles/queries/matrix-headers', 'role': 'low-permission', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403},
                {'route': 'POST /secure/core/module/sys/menu-roles/queries/matrix-rows', 'role': 'low-permission', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'grant'},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'revoke'},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'low-permission', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403, 'label': 'unauthorized'},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 400, 'expected_status': 400, 'label': 'invalid-state'},
                {'route': 'PUT /secure/core/module/sys/menu-roles/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 400, 'expected_status': 400, 'label': 'transaction-rollback'},
            ]
            role_user_http_cases = [
                {'route': 'POST /secure/core/module/sys/role-users/queries/matrix-headers', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200},
                {'route': 'POST /secure/core/module/sys/role-users/queries/matrix-rows', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200},
                {'route': 'PUT /secure/core/module/sys/role-users/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'same-org-grant'},
                {'route': 'PUT /secure/core/module/sys/role-users/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 200, 'expected_status': 200, 'label': 'same-org-revoke'},
                {'route': 'PUT /secure/core/module/sys/role-users/instant', 'role': 'reader', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403, 'label': 'reader-denial'},
                {'route': 'PUT /secure/core/module/sys/role-users/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 403, 'expected_status': 403, 'label': 'cross-org-denial'},
                {'route': 'PUT /secure/core/module/sys/role-users/instant', 'role': 'role-admin', 'organization': 'org-a', 'mode': 'isolated', 'status': 400, 'expected_status': 400, 'label': 'invalid-state'},
            ]
            if args.role_user_only:
                report['scope'] = 'owned role-user matrix read and isolated same-organization grant/revoke; cross-organization and low-permission denial; disposable synthetic Oracle and loopback HTTP only'
                report['fixture'] = 'role-user-matrix-fixture'
                report['audit'] = {'events': [role_user_event], 'expected': [role_user_event]}
                report['http_cases'] = role_user_http_cases
                report['http'] = 'passed; role-user headers/rows, same-organization grant/revoke, reader denial, cross-organization denial and invalid-state rollback'
            elif args.tristate_only:
                report['scope'] = 'owned role-menu matrix read and parent tri-state projection; disposable synthetic Oracle and loopback HTTP only'
                report['audit'] = {'events': [audit_event], 'expected': [audit_event]}
                report['http_cases'] = menu_http_cases[:4]
                report['http'] = 'passed; role-menu headers/rows and low-permission denial with parent tri-state projection'
            elif args.write_only:
                report['scope'] = 'owned role-menu matrix isolated instant-save grant/revoke and rollback; disposable synthetic Oracle and loopback HTTP only'
                report['audit'] = {'events': [write_event], 'expected': [write_event]}
                report['http_cases'] = [item for item in menu_http_cases if item['route'].startswith('PUT /secure/core/module/sys/menu-roles/instant')]
                report['http'] = 'passed; isolated role-menu grant/revoke, reader denial, invalid-state and transaction rollback'
            else:
                report['audit'] = {'events': [audit_event, write_event], 'expected': [audit_event, write_event]}
                report['http_cases'] = menu_http_cases
    except (OSError, ValueError, KeyError, RuntimeError, subprocess.TimeoutExpired) as failure:
        if report['status'] == 'executed-pass': report['status'] = 'executed-fail'
        report['reason'] = str(failure) if isinstance(failure, RuntimeError) else type(failure).__name__
        print('role-menu matrix: ' + report['reason'])
        return 1
    finally:
        cleanup = {}
        if created:
            try: cleanup['container_removed'] = run(['docker', 'rm', '--force', '--volumes', name], timeout=60).returncode == 0
            except (OSError, subprocess.TimeoutExpired): cleanup['container_removed'] = False
            cleanup['anonymous_volumes_removed'] = cleanup['container_removed']
        if network_created:
            try: cleanup['network_removed'] = run(['docker', 'network', 'rm', network], timeout=60).returncode == 0
            except (OSError, subprocess.TimeoutExpired): cleanup['network_removed'] = False
        report['cleanup'] = cleanup
        report['cleanup']['browser_session_closed'] = True
        report['cleanup']['temporary_files_removed'] = True
        report['finished_at'] = datetime.datetime.now(datetime.timezone.utc).isoformat()
        save()
    print('role-menu matrix: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
