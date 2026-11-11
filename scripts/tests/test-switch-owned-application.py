#!/usr/bin/env python3
"""Public CLI tests against a local Docker Engine HTTP fixture (no daemon)."""
import base64
import copy
import hashlib
import http.server
import json
import os
from pathlib import Path
import socketserver
import subprocess
import tempfile
import threading
import unittest

SCRIPT = Path(__file__).resolve().parents[1] / 'switch-owned-application.py'
CURRENT, TARGET, ORACLE, REDIS = (char * 64 for char in 'abcd')
NETWORK = 'e' * 64
# DER SubjectPublicKeyInfo RSA key generated solely for this fixture.
KEY = 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAK9LiQ1HTpTjp4nveBwSmEoMfjhR70ckL3mEX1WmRiVtq1oJxBjRPgQ2iO4Kbwrq5eDSwPPEVRc87KzHsQmNkTsCAwEAAQ=='


class UnixHTTP(socketserver.ThreadingMixIn, socketserver.UnixStreamServer):
    daemon_threads = True


class DockerFixture:
    def __init__(self, root):
        self.mutations = []
        self.failure = None
        self.failed = False
        self.fail_after_nth_mutation = None
        self.bad_key = False
        self.bad_key_for_target = False
        self.peer_drift = False
        self.fail_recovery_start = False
        self.containers = {}
        for cid, name, running in ((CURRENT, 'app', True), (TARGET, 'previous', False),
                                   (ORACLE, 'oracle', True), (REDIS, 'redis', True)):
            dist = root / ('candidate' if cid == CURRENT else 'previous')
            self.containers[cid] = {
                'Id': cid, 'Name': '/' + name, 'Image': 'sha256:' + 'f' * 64,
                'Config': {'Image': 'java:8', 'Env': ['SECRET=do-not-print'], 'Cmd': ['start']},
                'HostConfig': {'Binds': [str(dist) + ':/opt/current-owned:ro',
                    str(root / 'config') + ':/run/config/app.properties:ro',
                    str(root / 'work') + ':/var/lib/lims-owned:rw',
                    str(root / 'report') + ':/opt/report-engine:rw'], 'NetworkMode': 'isolated'},
                'Mounts': [{'Type': 'bind', 'Source': str(source), 'Destination': dest,
                            'RW': dest in ('/var/lib/lims-owned', '/opt/report-engine'),
                            'Mode': 'rw' if dest in ('/var/lib/lims-owned', '/opt/report-engine') else 'ro',
                            'Propagation': 'rprivate'} for source, dest in (
                                (dist, '/opt/current-owned'), (root / 'config', '/run/config/app.properties'),
                                (root / 'work', '/var/lib/lims-owned'), (root / 'report', '/opt/report-engine'))],
                'State': {'Running': running, 'Paused': False, 'Restarting': False,
                          'Dead': False, 'StartedAt': '2026-11-11T00:00:00Z'},
                'NetworkSettings': {'Networks': {}},
            }
        for peer in (ORACLE, REDIS):
            self.containers[peer]['Mounts'] = []
        self.containers[CURRENT]['NetworkSettings']['Networks']['isolated'] = {
            'NetworkID': NETWORK, 'IPAddress': '172.19.0.4', 'Aliases': None,
            'IPAMConfig': {'IPv4Address': '172.19.0.4'}, 'GlobalIPv6Address': ''}
        for cid, ip in ((ORACLE, '172.19.0.2'), (REDIS, '172.19.0.3')):
            self.containers[cid]['NetworkSettings']['Networks']['isolated'] = {
                'NetworkID': NETWORK, 'IPAddress': ip, 'Aliases': None}
        fixture = self
        class DockerHandler(http.server.BaseHTTPRequestHandler):
            def log_message(self, *args):
                pass
            def do_GET(self):
                if self.path.startswith('/containers/'):
                    if getattr(fixture, 'backup_probe_error', False) and '-retained-' in self.path:
                        return self.reply(500, {'message': 'SECRET backup probe'})
                    cid = self.path.split('/')[2]
                    if cid not in fixture.containers:
                        return self.reply(404, {'message': 'SECRET docker raw error'})
                    return self.reply(200, fixture.containers[cid])
                if self.path.startswith('/networks/'):
                    members = {cid: {'Name': c['Name'].lstrip('/')} for cid, c in fixture.containers.items()
                               if c['NetworkSettings']['Networks']}
                    return self.reply(200, {'Id': NETWORK, 'Name': 'isolated', 'Internal': True,
                                           'Driver': 'bridge', 'Scope': 'local', 'Containers': members})
                self.reply(404, {})
            def do_POST(self):
                from urllib.parse import urlsplit, parse_qs
                parsed = urlsplit(self.path)
                body = json.loads(self.rfile.read(int(self.headers.get('Content-Length', '0'))) or b'{}')
                parts = parsed.path.strip('/').split('/')
                action = parts[-1]
                if fixture.fail_recovery_start and parts[0] == 'containers' and parts[1] == CURRENT and action == 'start':
                    return self.reply(500, {'message': 'SECRET restoration start rejected'})
                fixture.mutations.append(action)
                if fixture.failure == action and not fixture.failed:
                    fixture.failed = True
                    return self.reply(500, {'message': 'SECRET docker raw error'})
                if parts[0] == 'containers':
                    c = fixture.containers[parts[1]]
                    if action == 'stop':
                        c['State']['Running'] = False
                        for endpoint in c['NetworkSettings']['Networks'].values():
                            endpoint['IPAddress'] = ''
                    elif action == 'start':
                        if fixture.peer_drift and parts[1] == TARGET:
                            fixture.containers[REDIS]['State']['StartedAt'] = 'unexpected-restart'
                        c['State']['Running'] = True
                        c['State']['StartedAt'] = '2026-11-11T01:00:00Z'
                        for endpoint in c['NetworkSettings']['Networks'].values():
                            endpoint['IPAddress'] = endpoint['IPAMConfig']['IPv4Address']
                    elif action == 'rename':
                        name = parse_qs(parsed.query)['name'][0]
                        if any(item['Name'] == '/' + name for item in fixture.containers.values()):
                            return self.reply(409, {'message': 'name conflict'})
                        c['Name'] = '/' + name
                elif action == 'disconnect':
                    fixture.containers[body['Container']]['NetworkSettings']['Networks'] = {}
                elif action == 'connect':
                    c = fixture.containers[body['Container']]
                    config = body['EndpointConfig']
                    ip = config['IPAMConfig']['IPv4Address']
                    if any(n.get('IPAddress') == ip for item in fixture.containers.values()
                           for n in item['NetworkSettings']['Networks'].values()):
                        return self.reply(409, {'message': 'IP conflict'})
                    c['NetworkSettings']['Networks'] = {'isolated': {
                        'NetworkID': NETWORK, 'IPAddress': ip if c['State']['Running'] else '',
                        'IPAMConfig': config['IPAMConfig'],
                        'Aliases': config.get('Aliases'), 'GlobalIPv6Address': ''}}
                if sum(c['State']['Running'] for cid, c in fixture.containers.items()
                       if cid in (CURRENT, TARGET)) > 1:
                    raise AssertionError('second app started')
                if fixture.fail_after_nth_mutation == len(fixture.mutations):
                    fixture.fail_after_nth_mutation = None
                    return self.reply(500, {'message': 'SECRET response lost after mutation'})
                self.reply(204, None)
            def reply(self, status, body):
                raw = b'' if body is None else json.dumps(body).encode()
                self.send_response(status)
                self.send_header('Content-Length', str(len(raw)))
                self.end_headers()
                self.wfile.write(raw)
        class ReadyHandler(http.server.BaseHTTPRequestHandler):
            def log_message(self, *args): pass
            def do_GET(self):
                active = any(c['State']['Running'] and c['NetworkSettings']['Networks']
                             for cid, c in fixture.containers.items() if cid in (CURRENT, TARGET))
                key = b'' if fixture.bad_key or (fixture.bad_key_for_target and fixture.containers[TARGET]['State']['Running']) else KEY.encode()
                self.send_response(200 if active else 503)
                self.end_headers()
                self.wfile.write(key)
        self.socket = str(root / 'docker.sock')
        self.docker = UnixHTTP(self.socket, DockerHandler)
        self.http = http.server.ThreadingHTTPServer(('127.0.0.1', 0), ReadyHandler)
        self.url = 'http://127.0.0.1:%d/open/security/public-key' % self.http.server_port
        for server in (self.docker, self.http):
            threading.Thread(target=server.serve_forever, daemon=True).start()
    def close(self):
        for server in (self.docker, self.http):
            server.shutdown()
            server.server_close()


class SwitchTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='switch-test-')
        self.root = Path(self.temp.name)
        for name in ('candidate', 'previous'):
            dist = self.root / name
            (dist / 'app').mkdir(parents=True)
            (dist / 'app/application.jar').write_bytes(name.encode())
            (dist / 'manifest.json').write_text(json.dumps({
                'distribution_mode': 'source-only-reconstituted', 'supplier_runtime_included': False,
                'app_jar': 'app/application.jar',
                'files': {'app/application.jar': hashlib.sha256(name.encode()).hexdigest()}}))
        self.fixture = DockerFixture(self.root)
        self.before = copy.deepcopy(self.fixture.containers)
    def tearDown(self):
        self.fixture.close()
        self.temp.cleanup()
    def run_cli(self, *extra):
        self.receipt = self.root / ('receipt%d.json' % len(list(self.root.glob('receipt*'))))
        return subprocess.run(['python3', str(SCRIPT), '--current', CURRENT, '--target', TARGET,
            '--network', 'isolated', '--readiness-url', self.fixture.url,
            '--docker-socket', self.fixture.socket, '--receipt', str(self.receipt),
            '--readiness-timeout', '0.15', *extra], text=True, capture_output=True)
    def test_execute_keeps_configuration_mounts_peers_and_one_application(self):
        result = self.run_cli('--execute', '--backup-name', 'candidate-retained')
        self.assertEqual(result.returncode, 0, result.stderr)
        receipt = json.loads(self.receipt.read_text())
        self.assertEqual(receipt['status'], 'switched')
        current, target = self.fixture.containers[CURRENT], self.fixture.containers[TARGET]
        self.assertEqual(current['Name'], '/candidate-retained')
        self.assertFalse(current['State']['Running'])
        self.assertEqual(current['NetworkSettings']['Networks'], {})
        self.assertEqual(target['Name'], '/app')
        self.assertTrue(target['State']['Running'])
        self.assertEqual(target['NetworkSettings']['Networks']['isolated']['IPAddress'], '172.19.0.4')
        self.assertIsNone(target['NetworkSettings']['Networks']['isolated']['Aliases'])
        for cid in (CURRENT, TARGET):
            for field in ('Config', 'HostConfig', 'Mounts'):
                self.assertEqual(self.fixture.containers[cid][field], self.before[cid][field])
        for cid in (ORACLE, REDIS):
            self.assertEqual(self.fixture.containers[cid], self.before[cid])
        self.assertNotIn('SECRET', result.stdout + result.stderr + self.receipt.read_text())

    def test_daemon_error_checking_backup_name_refuses_before_downtime(self):
        self.fixture.backup_probe_error = True
        result = self.run_cli('--execute')
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.fixture.mutations, [])
        self.assertEqual(self.fixture.containers, self.before)

    def assert_restored(self, result):
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(json.loads(self.receipt.read_text())['status'], 'failed-restored')
        current, target = self.fixture.containers[CURRENT], self.fixture.containers[TARGET]
        self.assertEqual(current['Name'], '/app')
        self.assertTrue(current['State']['Running'])
        self.assertEqual(current['NetworkSettings']['Networks']['isolated']['IPAddress'], '172.19.0.4')
        self.assertIsNone(current['NetworkSettings']['Networks']['isolated']['Aliases'])
        self.assertEqual(target['Name'], '/previous')
        self.assertFalse(target['State']['Running'])
        self.assertFalse(target['NetworkSettings']['Networks'])
        for cid in (ORACLE, REDIS):
            self.assertEqual(self.fixture.containers[cid], self.before[cid])
        self.assertNotIn('SECRET', result.stdout + result.stderr + self.receipt.read_text())

    def test_failure_at_each_docker_mutation_restores_current(self):
        for action in ('stop', 'rename', 'disconnect', 'connect', 'start'):
            with self.subTest(action=action):
                self.fixture.failure = action
                self.fixture.failed = False
                self.fixture.mutations.clear()
                self.assert_restored(self.run_cli('--execute'))

    def test_empty_target_rsa_readiness_restores_current_and_reports_failure(self):
        self.fixture.bad_key_for_target = True
        self.assert_restored(self.run_cli('--execute'))
        self.assertEqual(json.loads(self.receipt.read_text())['failure'], 'rsa-readiness-failed')

    def test_dynamic_ip_is_refused_before_stop(self):
        self.fixture.containers[CURRENT]['NetworkSettings']['Networks']['isolated']['IPAMConfig'] = None
        result = self.run_cli('--execute')
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.fixture.mutations, [])

    def test_remote_docker_environment_is_refused_before_stop(self):
        previous = os.environ.get('DOCKER_HOST')
        os.environ['DOCKER_HOST'] = 'tcp://remote.invalid:2375'
        try:
            result = self.run_cli('--execute')
        finally:
            if previous is None: os.environ.pop('DOCKER_HOST')
            else: os.environ['DOCKER_HOST'] = previous
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.fixture.mutations, [])

    def test_ambiguous_response_after_each_mutation_uses_actual_state_to_restore(self):
        for mutation in range(1, 7):
            with self.subTest(mutation=mutation):
                self.fixture.fail_after_nth_mutation = mutation
                self.fixture.mutations.clear()
                self.assert_restored(self.run_cli('--execute'))

    def test_configuration_and_all_mount_differences_refuse_before_stop(self):
        for change in ('env', 'host', 'work', 'report', 'config', 'distribution-mode'):
            with self.subTest(change=change):
                self.fixture.containers = copy.deepcopy(self.before)
                target = self.fixture.containers[TARGET]
                if change == 'env': target['Config']['Env'] = ['SECRET=changed']
                elif change == 'host': target['HostConfig']['Privileged'] = True
                elif change == 'distribution-mode': target['Mounts'][0]['RW'] = True
                else:
                    destination = {'work': '/var/lib/lims-owned', 'report': '/opt/report-engine',
                                   'config': '/run/config/app.properties'}[change]
                    for mount in target['Mounts']:
                        if mount['Destination'] == destination: mount['Source'] += '-changed'
                result = self.run_cli('--execute')
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(self.fixture.mutations, [])
                self.assertNotIn('SECRET', result.stdout + result.stderr + self.receipt.read_text())

    def test_hash_escape_symlink_mode_and_unlisted_files_refuse_before_stop(self):
        directory = self.root / 'previous'
        manifest = directory / 'manifest.json'
        original = manifest.read_text()
        for change in ('hash', 'escape', 'absolute', 'symlink', 'mode', 'supplier', 'missing-app', 'extra'):
            with self.subTest(change=change):
                manifest.write_text(original)
                data = json.loads(original)
                jar = directory / 'app/application.jar'
                jar.unlink()
                jar.write_bytes(b'previous')
                extra = directory / 'extra.jar'
                extra.unlink(missing_ok=True)
                if change == 'hash': jar.write_bytes(b'changed')
                elif change in ('escape', 'absolute'):
                    data['files']['../escape' if change == 'escape' else '/absolute'] = 'a' * 64
                elif change == 'symlink':
                    jar.unlink()
                    jar.symlink_to(self.root / 'candidate/app/application.jar')
                elif change == 'mode': data['distribution_mode'] = 'merged-modules'
                elif change == 'supplier': data['supplier_runtime_included'] = True
                elif change == 'missing-app': data['app_jar'] = 'missing.jar'
                elif change == 'extra': extra.write_bytes(b'unlisted')
                manifest.write_text(json.dumps(data))
                result = self.run_cli('--execute')
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(self.fixture.mutations, [])

    def test_invalid_rsa_response_refuses_before_stop(self):
        self.fixture.bad_key = True
        result = self.run_cli('--execute')
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.fixture.mutations, [])
        self.assertEqual(json.loads(self.receipt.read_text())['status'], 'refused')

    def test_aliases_are_preserved(self):
        self.fixture.containers[CURRENT]['NetworkSettings']['Networks']['isolated']['Aliases'] = ['application', 'owned']
        result = self.run_cli('--execute')
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(self.fixture.containers[TARGET]['NetworkSettings']['Networks']['isolated']['Aliases'],
                         ['application', 'owned'])

    def test_recovery_failure_is_reported_without_claiming_restored(self):
        self.fixture.bad_key_for_target = True
        self.fixture.fail_recovery_start = True
        result = self.run_cli('--execute')
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(json.loads(self.receipt.read_text())['status'], 'failed-recovery-incomplete')
        self.assertFalse(self.fixture.containers[TARGET]['State']['Running'])
        self.assertFalse(self.fixture.containers[TARGET]['NetworkSettings']['Networks'])
        self.assertNotIn('SECRET', result.stdout + result.stderr + self.receipt.read_text())

    def test_changed_protected_peer_prevents_success(self):
        self.fixture.peer_drift = True
        result = self.run_cli('--execute')
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(json.loads(self.receipt.read_text())['failure'], 'protected-peer-changed')
        self.assertTrue(self.fixture.containers[CURRENT]['State']['Running'])
        self.assertFalse(self.fixture.containers[TARGET]['State']['Running'])

    def test_network_or_already_running_target_refuses_before_stop(self):
        for change in ('network', 'running', 'external'):
            with self.subTest(change=change):
                self.fixture.containers = copy.deepcopy(self.before)
                if change == 'running': self.fixture.containers[TARGET]['State']['Running'] = True
                elif change == 'network':
                    self.fixture.containers[TARGET]['NetworkSettings']['Networks'] = {'other': {}}
                else:
                    self.fixture.containers[CURRENT]['NetworkSettings']['Networks']['extra'] = {}
                result = self.run_cli('--execute')
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(self.fixture.mutations, [])

    def test_auto_removed_container_is_refused_before_stop(self):
        for cid in (CURRENT, TARGET):
            self.fixture.containers[cid]['HostConfig']['AutoRemove'] = True
        result = self.run_cli('--execute')
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.fixture.mutations, [])

    def test_correctly_hashed_supplier_runtime_files_are_refused_before_stop(self):
        directory = self.root / 'previous'
        manifest = directory / 'manifest.json'
        original = manifest.read_text()
        for filename in ('sunway-app.so', 'license.lic', 'lib/gikam-core-1.jar'):
            with self.subTest(filename=filename):
                data = json.loads(original)
                item = directory / filename
                item.parent.mkdir(exist_ok=True)
                item.write_bytes(b'controlled-test-residue')
                data['files'][filename] = hashlib.sha256(item.read_bytes()).hexdigest()
                manifest.write_text(json.dumps(data))
                result = self.run_cli('--execute')
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(self.fixture.mutations, [])
                item.unlink()

    def test_default_dry_run_checks_without_changing_containers(self):
        result = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(self.fixture.containers, self.before)
        self.assertEqual(self.fixture.mutations, [])
        self.assertEqual(json.loads(self.receipt.read_text())['status'], 'dry-run')
        self.assertEqual(self.receipt.stat().st_mode & 0o777, 0o600)
        self.assertNotIn('SECRET', result.stdout + result.stderr + self.receipt.read_text())

if __name__ == '__main__':
    unittest.main()
