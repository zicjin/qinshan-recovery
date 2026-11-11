#!/usr/bin/env python3
"""Activate an already-created, stopped owned application; dry-run by default.

This is deliberately not a container builder: both immutable containers must
already have identical configuration apart from the read-only distribution
bind. Only a local Docker Engine Unix socket is supported. No environment or
raw Docker error is written to stdout, stderr or the private receipt.
"""
from __future__ import annotations

import argparse
import base64
import copy
import hashlib
import http.client
import importlib.util
import json
import os
from pathlib import Path, PurePosixPath
import re
import shutil
import signal
import socket
import stat
import subprocess
import sys
import time
from urllib.parse import quote, urlencode, urlsplit

DISTRIBUTION = '/opt/current-owned'
# Reuse the official package boundary; activation is not a second package gate.
_gate_spec = importlib.util.spec_from_file_location(
    'source_only_checkpoint', Path(__file__).with_name('verify-source-only-checkpoint.py'))
_gate = importlib.util.module_from_spec(_gate_spec)
_gate_spec.loader.exec_module(_gate)


class Refusal(Exception):
    """A public, fixed diagnostic code, never an external exception message."""


class DockerResponseError(Refusal):
    def __init__(self, status):
        super().__init__('docker-request-failed')
        self.status = status


class LocalDocker(http.client.HTTPConnection):
    def __init__(self, path):
        super().__init__('localhost', timeout=45)
        self.path = path
    def connect(self):
        self.sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        self.sock.settimeout(self.timeout)
        self.sock.connect(self.path)


class Engine:
    def __init__(self, path):
        if not Path(path).is_absolute() or not stat.S_ISSOCK(os.stat(path).st_mode):
            raise Refusal('docker-socket-must-be-local-unix')
        self.path = path
    def request(self, method, path, body=None):
        conn = LocalDocker(self.path)
        try:
            raw = None if body is None else json.dumps(body).encode()
            conn.request(method, path, body=raw, headers={'Content-Type': 'application/json'})
            reply = conn.getresponse()
            payload = reply.read(16 * 1024 * 1024)
            if reply.status not in (200, 201, 204, 304):
                raise DockerResponseError(reply.status)
            return json.loads(payload) if payload else None
        except Refusal:
            raise
        except Exception:
            raise Refusal('docker-transport-failed') from None
        finally:
            conn.close()
    def inspect(self, cid):
        value = self.request('GET', '/containers/' + quote(cid, safe='') + '/json')
        if not isinstance(value, dict) or value.get('Id') != cid:
            raise Refusal('container-identity-mismatch')
        return value
    def network(self, name):
        return self.request('GET', '/networks/' + quote(name, safe=''))
    def action(self, cid, action, **query):
        path = '/containers/' + cid + '/' + action
        if query:
            path += '?' + urlencode(query)
        self.request('POST', path)
    def disconnect(self, network, cid):
        self.request('POST', '/networks/' + network + '/disconnect', {'Container': cid, 'Force': False})
    def connect(self, network, cid, endpoint):
        config = {'IPAMConfig': {'IPv4Address': endpoint['IPAddress']}, 'Aliases': endpoint.get('Aliases')}
        self.request('POST', '/networks/' + network + '/connect', {'Container': cid, 'EndpointConfig': config})


def require(condition, code):
    if not condition:
        raise Refusal(code)


def file_hash(path):
    digest = hashlib.sha256()
    with path.open('rb') as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b''):
            digest.update(block)
    return digest.hexdigest()


def distribution(source):
    root = Path(source)
    require(root.is_absolute() and root.resolve() == root and root.is_dir(), 'distribution-path-invalid')
    require(not any(path.is_symlink() for path in root.rglob('*')), 'distribution-symlink-refused')
    manifest_path = root / 'manifest.json'
    require(manifest_path.is_file(), 'distribution-manifest-missing')
    data = json.loads(manifest_path.read_text())
    require(data.get('distribution_mode') == 'source-only-reconstituted'
            and data.get('supplier_runtime_included') is False, 'distribution-mode-refused')
    files = data.get('files')
    app = data.get('app_jar')
    require(isinstance(files, dict) and files and isinstance(app, str) and app in files,
            'distribution-files-invalid')
    for name, digest in files.items():
        require(isinstance(name, str) and isinstance(digest, str), 'distribution-files-invalid')
        path = PurePosixPath(name)
        require(name and not path.is_absolute() and str(path) == name
                and '..' not in path.parts and '\\' not in name
                and re.fullmatch('[0-9a-f]{64}', digest), 'distribution-path-or-digest-invalid')
        require(not _gate.FORBIDDEN_NAME.search(name) and not _gate.PRIVATE_MARKER.search(name),
                'supplier-runtime-file-refused')
        item = root / name
        require(item.is_file() and item.resolve().is_relative_to(root), 'distribution-file-missing')
        require(file_hash(item) == digest, 'distribution-hash-mismatch')
    require({p.relative_to(root).as_posix() for p in root.rglob('*') if p.is_file()}
            == set(files) | {'manifest.json'}, 'distribution-unlisted-file')
    classpath = root / 'lib/classpath.txt'
    if classpath.is_file():
        require(not any(_gate.PRIVATE_MARKER.search(entry)
                        for entry in re.split(r'[:\s]+', classpath.read_text())),
                'supplier-runtime-classpath-refused')
    return {'manifest_sha256': file_hash(manifest_path), 'app_sha256': files[app], 'file_count': len(files)}


def normalized_container(container):
    mounts = copy.deepcopy(container.get('Mounts', []))
    selected = [mount for mount in mounts if mount.get('Destination') == DISTRIBUTION]
    require(len(selected) == 1 and selected[0].get('Type') == 'bind'
            and selected[0].get('RW') is False, 'readonly-distribution-bind-required')
    source = selected[0]['Source']
    selected[0]['Source'] = '<distribution>'
    require(len({m['Destination'] for m in mounts}) == len(mounts), 'duplicate-mount-destination')
    for destination in ('/var/lib/lims-owned', '/opt/report-engine'):
        require(any(m.get('Destination') == destination and m.get('RW') is True for m in mounts),
                'required-working-mount-missing')
    require(any(m.get('Destination', '').startswith('/run/config/') and m.get('RW') is False
                for m in mounts), 'readonly-configuration-mount-required')
    host = copy.deepcopy(container['HostConfig'])
    require(not host.get('AutoRemove'), 'auto-remove-container-refused')
    binds = host.get('Binds') or []
    matches = []
    for index, bind in enumerate(binds):
        parts = bind.split(':')
        if len(parts) >= 2 and parts[1] == DISTRIBUTION:
            require(len(parts) == 3 and parts[0] == source and 'ro' in parts[2].split(','),
                    'distribution-bind-mismatch')
            matches.append(index)
            binds[index] = '<distribution>:' + ':'.join(parts[1:])
    require(len(matches) == 1, 'distribution-bind-mismatch')
    host['Binds'] = sorted(binds)
    return {'Config': container['Config'], 'HostConfig': host, 'Image': container.get('Image'),
            'Mounts': sorted(mounts, key=lambda mount: mount['Destination'])}, source


def networks(container):
    return container['NetworkSettings']['Networks']


def safe_state(container, running):
    state = container['State']
    return (state.get('Running') is running and not state.get('Paused')
            and not state.get('Restarting') and not state.get('Dead'))


def endpoint_matches(container, plan, *, stopped=False):
    values = networks(container)
    if set(values) != {plan['network_name']}:
        return False
    endpoint = values[plan['network_name']]
    address = (endpoint.get('IPAMConfig') or {}).get('IPv4Address') if stopped else endpoint.get('IPAddress')
    return (endpoint.get('NetworkID') == plan['network_id']
            and address == plan['endpoint']['IPAddress']
            and endpoint.get('Aliases') == plan['endpoint'].get('Aliases'))


def peer_snapshot(engine, network, exclude):
    peers = {}
    for cid in network.get('Containers', {}):
        if cid in exclude:
            continue
        container = engine.inspect(cid)
        require(safe_state(container, True), 'protected-peer-not-running')
        require(not any(m.get('Destination') == DISTRIBUTION for m in container.get('Mounts', [])),
                'second-application-on-network')
        peers[cid] = {'started_at': container['State']['StartedAt'], 'name': container['Name']}
    require(len(peers) >= 2, 'protected-oracle-redis-peers-required')
    return peers


def check_peers(engine, plan):
    live = peer_snapshot(engine, engine.network(plan['network_id']), {plan['current'], plan['target']})
    require(live == plan['peers'], 'protected-peer-changed')


def readiness(url, timeout):
    parsed = urlsplit(url)
    deadline = time.monotonic() + timeout
    while True:
        conn = http.client.HTTPConnection(parsed.hostname, parsed.port, timeout=min(3, max(timeout, 0.1)))
        try:
            conn.request('GET', parsed.path)
            response = conn.getresponse()
            raw = response.read(65537)
            if response.status == 200 and 0 < len(raw) <= 65536:
                decoded = base64.b64decode(raw.strip(), validate=True)
                result = subprocess.run(['openssl', 'rsa', '-pubin', '-inform', 'DER', '-noout'],
                                        input=decoded, capture_output=True, timeout=3, check=False)
                if decoded and result.returncode == 0:
                    return hashlib.sha256(decoded).hexdigest()
        except Exception:
            pass
        finally:
            conn.close()
        remaining = deadline - time.monotonic()
        if remaining <= 0:
            raise Refusal('rsa-readiness-failed')
        time.sleep(min(0.2, remaining))


def preflight(engine, args):
    current, target = engine.inspect(args.current), engine.inspect(args.target)
    require(safe_state(current, True) and safe_state(target, False), 'current-running-target-stopped-required')
    left, current_source = normalized_container(current)
    right, target_source = normalized_container(target)
    require(left == right, 'container-configuration-mismatch')
    require(current_source != target_source, 'different-distribution-binds-required')
    net = engine.network(args.network)
    require(net.get('Internal') is True and net.get('Scope') == 'local'
            and net.get('Driver') == 'bridge', 'single-isolated-bridge-required')
    require(set(networks(current)) == {net['Name']} and not networks(target),
            'current-single-network-target-disconnected-required')
    endpoint = networks(current)[net['Name']]
    require(endpoint.get('NetworkID') == net['Id'] and endpoint.get('IPAddress')
            and (endpoint.get('IPAMConfig') or {}).get('IPv4Address') == endpoint.get('IPAddress')
            and not (endpoint.get('IPAMConfig') or {}).get('IPv6Address')
            and not endpoint.get('GlobalIPv6Address') and not endpoint.get('Links')
            and not endpoint.get('DriverOpts'), 'unsupported-endpoint-configuration')
    require(current['Id'] in net.get('Containers', {}) and target['Id'] not in net['Containers'],
            'network-membership-mismatch')
    original_name = current['Name'].lstrip('/')
    backup = args.backup_name or original_name + '-retained-' + current['Id'][:12]
    require(re.fullmatch('[a-zA-Z0-9][a-zA-Z0-9_.-]+', backup)
            and backup not in (original_name, target['Name'].lstrip('/')), 'backup-name-invalid')
    # Reserve the backup name before any downtime. Names are not echoed on errors.
    try:
        engine.request('GET', '/containers/' + quote(backup, safe='') + '/json')
    except DockerResponseError as error:
        require(error.status == 404, 'backup-name-check-failed')
    else:
        raise Refusal('backup-name-already-exists')
    require(shutil.which('openssl') is not None, 'openssl-required')
    plan = {'current': current['Id'], 'target': target['Id'], 'current_name': original_name,
            'target_original_name': target['Name'].lstrip('/'), 'backup_name': backup,
            'network_id': net['Id'], 'network_name': net['Name'],
            'endpoint': {'IPAddress': endpoint['IPAddress'], 'Aliases': endpoint.get('Aliases')},
            'peers': peer_snapshot(engine, net, {current['Id'], target['Id']}),
            'current_distribution': distribution(current_source), 'target_distribution': distribution(target_source)}
    plan['current_readiness_sha256'] = readiness(args.readiness_url, args.readiness_timeout)
    return plan


def validate_active(engine, plan, target_active):
    current, target = engine.inspect(plan['current']), engine.inspect(plan['target'])
    active, stopped = (target, current) if target_active else (current, target)
    require(safe_state(active, True) and safe_state(stopped, False), 'application-running-state-mismatch')
    require(active['Name'] == '/' + plan['current_name'] and endpoint_matches(active, plan),
            'active-identity-or-endpoint-mismatch')
    require(not networks(stopped), 'retained-container-must-be-disconnected')
    expected = plan['backup_name'] if target_active else plan['target_original_name']
    require(stopped['Name'] == '/' + expected, 'retained-name-mismatch')
    check_peers(engine, plan)


def activate(engine, plan, args, update):
    def step(label, action):
        update('switching', phase=label)
        action()
        check_peers(engine, plan)
    step('stop-current', lambda: engine.action(plan['current'], 'stop', t=30))
    require(safe_state(engine.inspect(plan['current']), False), 'current-did-not-stop')
    step('retain-current-name', lambda: engine.action(plan['current'], 'rename', name=plan['backup_name']))
    step('disconnect-current', lambda: engine.disconnect(plan['network_id'], plan['current']))
    require(not networks(engine.inspect(plan['current'])), 'current-did-not-disconnect')
    step('connect-target', lambda: engine.connect(plan['network_id'], plan['target'], plan['endpoint']))
    step('activate-target-name', lambda: engine.action(plan['target'], 'rename', name=plan['current_name']))
    step('start-target', lambda: engine.action(plan['target'], 'start'))
    validate_active(engine, plan, True)
    key = readiness(args.readiness_url, args.readiness_timeout)
    validate_active(engine, plan, True)
    update('switched', readiness_sha256=key)


def recover(engine, plan, args, update):
    update('recovering')
    errors = []
    def attempt(action):
        try:
            action()
        except Exception:
            errors.append('recovery-operation-failed')
    # Reinspect after every potentially ambiguous Docker response. Never reconnect
    # or start the old application while the target can still hold the IP/run.
    target = engine.inspect(plan['target'])
    if target['State']['Running']:
        attempt(lambda: engine.action(plan['target'], 'stop', t=30))
    require(safe_state(engine.inspect(plan['target']), False), 'recovery-target-not-stopped')
    if networks(engine.inspect(plan['target'])):
        attempt(lambda: engine.disconnect(plan['network_id'], plan['target']))
    require(not networks(engine.inspect(plan['target'])), 'recovery-target-not-disconnected')
    if engine.inspect(plan['target'])['Name'] != '/' + plan['target_original_name']:
        attempt(lambda: engine.action(plan['target'], 'rename', name=plan['target_original_name']))
    require(engine.inspect(plan['target'])['Name'] == '/' + plan['target_original_name'], 'recovery-target-name-failed')
    current = engine.inspect(plan['current'])
    if current['Name'] != '/' + plan['current_name']:
        attempt(lambda: engine.action(plan['current'], 'rename', name=plan['current_name']))
    if not networks(engine.inspect(plan['current'])):
        attempt(lambda: engine.connect(plan['network_id'], plan['current'], plan['endpoint']))
    require(endpoint_matches(engine.inspect(plan['current']), plan,
                             stopped=not engine.inspect(plan['current'])['State']['Running']),
            'recovery-current-endpoint-failed')
    if not engine.inspect(plan['current'])['State']['Running']:
        attempt(lambda: engine.action(plan['current'], 'start'))
    validate_active(engine, plan, False)
    key = readiness(args.readiness_url, args.readiness_timeout)
    validate_active(engine, plan, False)
    update('failed-restored', recovery_errors=errors, restored_readiness_sha256=key)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--current', required=True, help='full 64-character running container ID')
    parser.add_argument('--target', required=True, help='full 64-character stopped, disconnected retained container ID')
    parser.add_argument('--network', required=True, help='single existing internal bridge network')
    parser.add_argument('--readiness-url', required=True, help='loopback HTTP /open/security/public-key URL')
    parser.add_argument('--receipt', required=True, type=Path, help='new private receipt file (0600)')
    parser.add_argument('--backup-name', help='retained current name; default NAME-retained-ID12')
    parser.add_argument('--docker-socket', default='/var/run/docker.sock', help='absolute local Unix socket path')
    parser.add_argument('--readiness-timeout', type=float, default=360,
                        help='seconds to await a valid RSA key (default: 360)')
    parser.add_argument('--execute', action='store_true', help='perform the switch; default validates only')
    args = parser.parse_args()
    receipt = {'schema': 1, 'status': 'preflight', 'execute': args.execute}
    descriptor = None
    plan = None
    switching = False
    def update(status, **fields):
        receipt.update(status=status, **fields)
        os.lseek(descriptor, 0, os.SEEK_SET)
        os.ftruncate(descriptor, 0)
        os.write(descriptor, (json.dumps(receipt, sort_keys=True, indent=2) + '\n').encode())
        os.fsync(descriptor)
    def record_safely(status, **fields):
        # Receipt I/O failure must not prevent restoration of the old process.
        try:
            update(status, **fields)
        except Exception:
            pass
    try:
        require(args.receipt.is_absolute(), 'receipt-must-be-absolute')
        descriptor = os.open(args.receipt, os.O_CREAT | os.O_EXCL | os.O_WRONLY | os.O_NOFOLLOW, 0o600)
        os.fchmod(descriptor, 0o600)
        update('preflight')
        require(re.fullmatch('[0-9a-f]{64}', args.current) and re.fullmatch('[0-9a-f]{64}', args.target)
                and args.current != args.target, 'distinct-full-container-ids-required')
        url = urlsplit(args.readiness_url)
        require(url.scheme == 'http' and url.hostname in ('127.0.0.1', '::1', 'localhost')
                and url.path == '/open/security/public-key' and not url.username and not url.password
                and not url.query and not url.fragment, 'loopback-public-key-url-required')
        require(0 < args.readiness_timeout <= 600, 'readiness-timeout-invalid')
        require(not os.environ.get('DOCKER_HOST') and not os.environ.get('DOCKER_CONTEXT'),
                'docker-host-or-context-environment-refused')
        engine = Engine(args.docker_socket)
        plan = preflight(engine, args)
        receipt['plan'] = plan
        update('ready')
        if not args.execute:
            update('dry-run')
            print('dry-run: retained application switch validated; no containers changed')
            return 0
        switching = True
        activate(engine, plan, args, update)
        print('switched: target is ready; previous application retained and stopped')
        return 0
    except BaseException as error:
        code = str(error) if isinstance(error, Refusal) else 'operation-failed'
        if descriptor is not None:
            receipt['failure'] = code
            if switching and plan:
                try:
                    recover(engine, plan, args, record_safely)
                except BaseException:
                    record_safely('failed-recovery-incomplete', recovery_failure='manual-recovery-required')
            else:
                record_safely('refused')
        print('switch failed: ' + code + '; inspect private receipt', file=sys.stderr)
        return 1
    finally:
        if descriptor is not None:
            os.close(descriptor)


if __name__ == '__main__':
    def interrupted(_signal, _frame):
        raise InterruptedError()
    signal.signal(signal.SIGTERM, interrupted)
    sys.exit(main())
