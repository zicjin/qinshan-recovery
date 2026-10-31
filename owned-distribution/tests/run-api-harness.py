#!/usr/bin/env python3
"""Exercise the shipped owned distribution through direct loopback HTTP."""
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
import socket
import subprocess
import tempfile
import time
import traceback
import urllib.error
import urllib.request
import uuid
import sys

ROOT = Path(__file__).resolve().parents[2]
DIST = ROOT / 'owned-distribution/target/distribution'
ORACLE_IMAGE = 'ghcr.io/gvenzl/oracle-free@sha256:cd3dff6649f5d67ca3121f5e064b5a5680611e5d34f03442423110778c47dda4'
REDIS_IMAGE = 'public.ecr.aws/docker/library/redis:7.4-alpine@sha256:57e4255cc971b1c8d6f8a6ca283ac01bc3ecdd914273ef6bfaede87aaecd6541'
sys.path.insert(0, str(ROOT / 'scripts'))
from owned_readiness import wait_until

SOURCE_SCOPES = (
    'owned-framework/src', 'owned-personnel/src', 'owned-equipment/src',
    'owned-consumables/src', 'owned-attachments/src', 'owned-report/src',
    'owned-scheduler/src', 'owned-web/src', 'owned-distribution/start.sh',
    'owned-distribution/package.py', 'owned-distribution/tests/run-api-harness.py',
    'scripts/build-owned-distribution.sh', 'scripts/prepare-owned-public-deps.py',
    'scripts/reject-supplier-runtime.sh', 'owned-web/build-owned-web.sh',
    'owned-web/original-personnel-sha256.json', 'owned-web/original-inventory-sha256.json',
)
FORBIDDEN_MARKERS = (
    'sunway-app.so', 'sunway-app.dll', 'sunway.app.license', 'license-machine',
    'MAC_ADDRESS', 'SYSTEM_MAC_ADDRESS_LIST',
    'com/sunwayworld/module/sys/license', '/core/module/sys/licenses',
    '/open/security/licenses',
)


def run(command, timeout=300, **kwargs):
    return subprocess.run([str(value) for value in command], capture_output=True,
                          text=True, timeout=timeout, **kwargs)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def redact_text(value, secrets):
    text = str(value)
    for secret in secrets:
        text = text.replace(str(secret), '<REDACTED>')
    return re.sub(r'(?i)(password\s*[=:]\s*)[^\s,;]+', r'\1<REDACTED>', text)


def forbidden_runtime_markers(value):
    text = str(value)
    return [marker for marker in FORBIDDEN_MARKERS if marker in text]


class ApiReplay:
    def __init__(self, temporary):
        self.temporary = Path(temporary)
        self.name = 'lims-owned-api-' + uuid.uuid4().hex
        self.redis_name = self.name + '-redis'
        self.network = self.name + '-net'
        self.oracle_created = False
        self.redis_created = False
        self.network_created = False
        self.process = None
        self.log_handle = None
        self.log = self.temporary / 'application.log'
        self.audit_path = self.temporary / 'audit.jsonl'
        self.secrets = []
        self.report = {
            'schema': 1,
            'status': 'executed-fail',
            'production_access': False,
            'scope': 'owned distribution direct loopback HTTP bootstrap and runtime boundary; synthetic Oracle and Redis only',
            'started_at': self.now(),
            'checks': [],
            'http_cases': [],
            'fixture': 'owned-api-harness-fixture',
            'role': 'anonymous',
            'organization': 'none',
            'mode': 'isolated',
            'cleanup': {},
            'phase_timings_ms': {},
        }

    @staticmethod
    def now():
        return datetime.datetime.now(datetime.timezone.utc).isoformat()

    def check(self, label, condition):
        if not condition:
            raise RuntimeError(label)
        self.report['checks'].append(label)

    def passed(self, result, label):
        if result.returncode:
            raise RuntimeError(label + ' failed: ' + redact_text(
                (result.stdout + result.stderr)[-4000:], self.secrets))
        return result.stdout

    def sql(self, source, fixture=False):
        schema = 'ALTER SESSION SET CURRENT_SCHEMA=query_fixture;\n' if fixture else ''
        text = ('whenever sqlerror exit failure rollback\n'
                'set pagesize 0 feedback off heading off echo off verify off linesize 32767 trimspool on\n'
                'ALTER SESSION SET CONTAINER=FREEPDB1;\n' + schema + source + '\nexit\n')
        return run(['docker', 'exec', '-i', self.name, 'sqlplus', '-s', '/ as sysdba'],
                   input=text, timeout=60)

    def build(self):
        listing = self.passed(run(['git', 'ls-files', '-z', '--', *SOURCE_SCOPES], cwd=ROOT),
                              'source inventory')
        sources = [ROOT / item for item in listing.split('\0') if item]
        self.report['source_commit'] = self.passed(run(['git', 'rev-parse', 'HEAD'], cwd=ROOT),
                                                    'source commit').strip()
        self.report['source_dirty'] = bool(self.passed(
            run(['git', 'status', '--porcelain', '--', *SOURCE_SCOPES], cwd=ROOT),
            'source status').strip())
        self.report['source_sha256'] = {str(path.relative_to(ROOT)): digest(path) for path in sources}
        self.report['source_coverage'] = {'roots': list(SOURCE_SCOPES), 'files': self.report['source_sha256']}
        self.passed(run([ROOT / 'scripts/build-owned-distribution.sh'], timeout=1800,
                        env=dict(os.environ, OWNED_DISTRIBUTION_REUSE='1')),
                    'owned distribution build')
        self.check('distribution launcher and packaged application exist',
                   (DIST / 'start.sh').is_file()
                   and (DIST / 'app/lims-owned-application.jar').is_file()
                   and (DIST / 'manifest.json').is_file())
        manifest = json.loads((DIST / 'manifest.json').read_text())
        self.report['distribution_manifest_sha256'] = digest(DIST / 'manifest.json')
        self.report['artifact_sha256'] = {
            str(path.relative_to(DIST)): digest(path)
            for path in [DIST / 'app/lims-owned-application.jar'] + sorted((DIST / 'lib').glob('*.jar'))}
        self.check('original application main is packaged',
                   manifest.get('main_class') == 'com.sunwayworld.emc.projectfunction.GikamWpjcApplication')

    def database(self):
        image = json.loads(self.passed(run(['docker', 'image', 'inspect', ORACLE_IMAGE]),
                                        'Oracle image inspect'))[0]
        self.check('pinned Linux amd64 Oracle image', image['Os'] == 'linux' and image['Architecture'] == 'amd64')
        self.report['image_reference'], self.report['image_id'] = ORACLE_IMAGE, image['Id']
        oracle_password = 'Ephemeral' + uuid.uuid4().hex[:16]
        self.secrets.append(oracle_password)
        env = self.temporary / 'oracle.env'
        env.write_text('ORACLE_PASSWORD=' + oracle_password + '\n')
        env.chmod(0o600)
        self.passed(run(['docker', 'network', 'create', '--internal', '--driver', 'bridge', self.network]),
                    'network create')
        self.network_created = True
        self.passed(run(['docker', 'create', '--name', self.name, '--network', self.network,
                         '--restart', 'no', '--memory', '2g', '--cpus', '2', '--env-file', env, ORACLE_IMAGE]),
                    'Oracle container create')
        self.oracle_created = True
        self.passed(run(['docker', 'start', self.name]), 'Oracle container start')

        def oracle_ready():
            result = self.sql("SELECT 'READY' FROM V$PDBS WHERE NAME='FREEPDB1' AND OPEN_MODE='READ WRITE';")
            logs = run(['docker', 'logs', self.name])
            return result.returncode == 0 and 'READY' in result.stdout and 'DATABASE IS READY TO USE!' in logs.stdout + logs.stderr

        ready = wait_until('Oracle readiness', oracle_ready, timeout=300, interval=2)
        self.report['phase_timings_ms']['oracle_readiness'] = ready['elapsed_ms']
        container = json.loads(self.passed(run(['docker', 'inspect', self.name]), 'Oracle inspect'))[0]
        network = json.loads(self.passed(run(['docker', 'network', 'inspect', self.network]), 'network inspect'))[0]
        endpoint = network['Containers'][container['Id']]['IPv4Address'].split('/', 1)[0]
        isolation = {
            'network_internal': network['Internal'],
            'sole_endpoint': set(network['Containers']) == {container['Id']},
            'attached_network_count': len(container['NetworkSettings']['Networks']),
            'port_bindings': container['HostConfig']['PortBindings'],
            'bind_mount_count': sum(mount['Type'] == 'bind' for mount in container['Mounts']),
        }
        self.report['isolation'] = isolation
        self.check('Oracle has one internal network without published ports or bind mounts',
                   isolation['network_internal'] and isolation['sole_endpoint']
                   and isolation['attached_network_count'] == 1
                   and not isolation['port_bindings'] and isolation['bind_mount_count'] == 0)
        fixture_password = 'Fixture' + uuid.uuid4().hex[:16]
        self.secrets.append(fixture_password)
        self.passed(self.sql('CREATE USER query_fixture IDENTIFIED BY ' + fixture_password + ';\n'
                             'GRANT CREATE SESSION, CREATE TABLE, UNLIMITED TABLESPACE TO query_fixture;',
                             fixture=False), 'fixture schema')
        self.report['database_observation'] = {
            'oracle_ready': True,
            'fixture_schema_created': True,
            'schema': 'query_fixture',
        }
        self.properties = self.temporary / 'jdbc.properties'
        self.properties.write_text('url=jdbc:oracle:thin:@//' + endpoint + ':1521/FREEPDB1\n'
                                   'user=query_fixture\npassword=' + fixture_password + '\n')
        self.properties.chmod(0o600)

        redis_image = json.loads(self.passed(run(['docker', 'image', 'inspect', REDIS_IMAGE]),
                                             'Redis image inspect'))[0]
        self.check('pinned Linux amd64 Redis image', redis_image['Os'] == 'linux' and redis_image['Architecture'] == 'amd64')
        self.passed(run(['docker', 'create', '--name', self.redis_name, '--network', self.network,
                         '--network-alias', 'redis', '--restart', 'no', '--memory', '256m', '--cpus', '1',
                         REDIS_IMAGE, 'redis-server', '--save', '', '--appendonly', 'no']),
                    'Redis container create')
        self.redis_created = True
        self.passed(run(['docker', 'start', self.redis_name]), 'Redis container start')

        def redis_ready():
            result = run(['docker', 'exec', self.redis_name, 'redis-cli', 'ping'])
            return result.returncode == 0 and result.stdout.strip() == 'PONG'

        redis_ready_result = wait_until('Redis readiness', redis_ready, timeout=30, interval=.5)
        self.report['phase_timings_ms']['redis_readiness'] = redis_ready_result['elapsed_ms']
        redis_container = json.loads(self.passed(run(['docker', 'inspect', self.redis_name]),
                                                 'Redis inspect'))[0]
        self.report['redis'] = {
            'image_reference': REDIS_IMAGE,
            'image_id': redis_image['Id'],
            'container_id': redis_container['Id'],
            'network_internal': network['Internal'],
            'attached_network_count': len(redis_container['NetworkSettings']['Networks']),
            'port_bindings': redis_container['HostConfig']['PortBindings'],
            'bind_mount_count': sum(mount['Type'] == 'bind' for mount in redis_container.get('Mounts', [])),
        }
        self.check('Redis has one internal network without published ports or bind mounts',
                   network['Internal'] is True
                   and len(redis_container['NetworkSettings']['Networks']) == 1
                   and not redis_container['HostConfig']['PortBindings']
                   and sum(mount['Type'] == 'bind' for mount in redis_container.get('Mounts', [])) == 0)

    def start(self):
        self.audit_path = self.temporary / 'audit.jsonl'
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as listener:
            listener.bind(('127.0.0.1', 0))
            port = listener.getsockname()[1]
        jdbc = dict(line.split('=', 1) for line in self.properties.read_text().splitlines() if '=' in line)
        config = self.temporary / 'owned-api.properties'
        config.write_text('\n'.join([
            'server.address=127.0.0.1', 'server.port=' + str(port),
            'spring.main.banner-mode=off', 'spring.main.log-startup-info=false',
            'spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration',
            'spring.datasource.type=org.springframework.jdbc.datasource.DriverManagerDataSource',
            'spring.datasource.driver-class-name=oracle.jdbc.OracleDriver',
            'spring.datasource.url=' + jdbc['url'], 'spring.datasource.username=query_fixture',
            'spring.datasource.password=' + jdbc['password'],
            'spring.redis.url=redis://' + self.report['redis'].get('endpoint', 'redis') + ':6379',
            'spring.redis.timeout=5000', 'sunway.user.password.encoder.type=MD5',
            'owned.identity.audit.type=file', 'owned.identity.audit.path=' + str(self.audit_path),
            'owned.attachments.enabled=false', 'owned.erp.mode=disabled',
            'owned.report.replay.mode=disabled', 'owned.scheduler.mode=disabled',
        ]) + '\n')
        # Use the Redis service alias inside the internal Docker network.
        config.write_text(config.read_text().replace(
            'spring.redis.url=redis://' + self.report['redis'].get('endpoint', 'redis') + ':6379',
            'spring.redis.url=redis://redis:6379'))
        config.chmod(0o600)
        self.log_handle = self.log.open('w')
        self.process = subprocess.Popen([str(DIST / 'start.sh'), str(config)], stdout=self.log_handle,
                                        stderr=subprocess.STDOUT, cwd=self.temporary,
                                        env=dict(os.environ, JAVA_HOME=str(ROOT / 'build/jdk1.8.0_391')))
        ready = wait_until('packaged application loopback readiness',
                           lambda: self.process.poll() is None and self._port_accepts(port),
                           timeout=180, interval=.25)
        self.report['phase_timings_ms']['application_readiness'] = ready['elapsed_ms']
        self.check('packaged application starts on loopback', self.process.poll() is None and self._port_accepts(port))
        self.url = 'http://127.0.0.1:' + str(port)
        self.opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor())

    @staticmethod
    def _port_accepts(port):
        try:
            with socket.create_connection(('127.0.0.1', port), timeout=.3):
                return True
        except OSError:
            return False

    def http(self, method, path):
        request = urllib.request.Request(self.url + path, method=method)
        try:
            with self.opener.open(request, timeout=20) as response:
                body = response.read(4096).decode('utf-8', errors='replace')
                status = response.status
        except urllib.error.HTTPError as failure:
            body = failure.read(4096).decode('utf-8', errors='replace')
            status = failure.code
        result = {'method': method, 'path': path, 'status': status,
                  'body_sha256': hashlib.sha256(body.encode()).hexdigest(), 'body_length': len(body)}
        self.report['http_cases'].append(result)
        return result, body

    def smoke(self):
        result, body = self.http('GET', '/open/security/public-key')
        self.check('public security key API responds through packaged loopback app',
                   result['status'] == 200 and len(body.strip()) > 20)
        for path in ('/core/module/sys/licenses', '/core/module/sys/licenses/number-of-online-users', '/open/security/licenses'):
            result, _ = self.http('GET', path)
            self.check('removed license route returns 404: ' + path, result['status'] == 404)
        self.scan_process_boundary()
        text = self.audit_path.read_text(errors='replace') if self.audit_path.exists() else ''
        self.report['audit'] = {'event_count': len([line for line in text.splitlines() if line.strip()]),
                                'sha256': hashlib.sha256(text.encode()).hexdigest(),
                                'credentials_excluded': all(secret not in text for secret in self.secrets)}
        self.check('audit report excludes credentials', self.report['audit']['credentials_excluded'])

    def scan_process_boundary(self):
        values = {}
        for name in ('cmdline', 'environ', 'maps'):
            path = Path('/proc') / str(self.process.pid) / name
            values[name] = path.read_text(errors='replace') if path.exists() else ''
        hits = {name: forbidden_runtime_markers(value) for name, value in values.items()}
        hits = {name: markers for name, markers in hits.items() if markers}
        self.report['runtime_boundary'] = {'pid': self.process.pid, 'marker_hits': hits}
        self.check('owned process has no native/license/MAC marker', not hits)

    def stop_and_cleanup(self):
        if self.process and self.process.poll() is None:
            self.process.terminate()
            try:
                self.process.wait(timeout=20)
            except subprocess.TimeoutExpired:
                self.process.kill()
                self.process.wait(timeout=10)
        self.report['cleanup']['application_stopped'] = self.process is None or self.process.poll() is not None
        if self.log_handle:
            self.log_handle.close()
            self.log_handle = None
        if self.redis_created:
            removed = run(['docker', 'rm', '--force', '--volumes', self.redis_name]).returncode == 0
            self.report['cleanup']['redis_container_removed'] = removed
            self.report['cleanup']['redis_anonymous_volumes_removed'] = removed
        if self.oracle_created:
            removed = run(['docker', 'rm', '--force', '--volumes', self.name]).returncode == 0
            self.report['cleanup']['oracle_container_removed'] = removed
            self.report['cleanup']['oracle_anonymous_volumes_removed'] = removed
        if self.network_created:
            self.report['cleanup']['network_removed'] = run(['docker', 'network', 'rm', self.network]).returncode == 0


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output', type=Path, default=ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/owned-api-harness-20261025.json')
    args = parser.parse_args(); guard_output_path(args.output)
    temporary = tempfile.TemporaryDirectory(prefix='lims-owned-api-private-')
    replay = ApiReplay(temporary.name)
    try:
        replay.build()
        replay.database()
        replay.start()
        replay.smoke()
        replay.report['status'] = 'executed-pass'
    except Exception as failure:
        replay.report['reason'] = redact_text(str(failure), replay.secrets)
        replay.report['diagnostic_traceback'] = redact_text(traceback.format_exc(), replay.secrets)
        if replay.log.exists():
            replay.report['diagnostic_log_tail'] = redact_text(replay.log.read_text(errors='replace')[-8000:], replay.secrets)
    finally:
        replay.stop_and_cleanup()
        temporary.cleanup()
        replay.report['cleanup']['private_temporary_directory_removed'] = not Path(temporary.name).exists()
        replay.report['cleanup']['temporary_files_removed'] = replay.report['cleanup']['private_temporary_directory_removed']
        replay.report['finished_at'] = replay.now()
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(redact_text(json.dumps(replay.report, ensure_ascii=False, indent=2), replay.secrets) + '\n')
    print('owned API harness: %s (%d checks)' % (replay.report['status'], len(replay.report['checks'])))
    return 0 if replay.report['status'] == 'executed-pass' else 1


if __name__ == '__main__':
    raise SystemExit(main())
