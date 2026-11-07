#!/usr/bin/env python3
"""Exercise prepared admission through the runtime CLI in a clean source checkout."""
import copy
import hashlib
import importlib.util
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import threading
import json
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location('milestone_capture_fixtures',
                                             ROOT / 'scripts/tests/test-local-runtime-milestone.py')
fixtures = importlib.util.module_from_spec(spec)
spec.loader.exec_module(fixtures)


class Handler(BaseHTTPRequestHandler):
    requests = 0

    def do_GET(self):
        Handler.requests += 1
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        self.wfile.write(b'{"status":"ok"}')

    def log_message(self, *_):
        pass


class PreparedAdmissionTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.repo = self.root / 'repo'
        (self.repo / 'scripts').mkdir(parents=True)
        for source in (ROOT / 'scripts').glob('*.py'):
            shutil.copyfile(source, self.repo / 'scripts' / source.name)
        for directory in ('project', 'owned-framework'):
            (self.repo / directory).mkdir()
            (self.repo / directory / '.keep').touch()
        self.git('init', '-q')
        self.git('add', '.')
        self.git('-c', 'user.name=Runtime Test', '-c', 'user.email=runtime@example.invalid',
                 'commit', '-qm', 'clean source checkpoint')
        self.private = self.root / 'private'
        self.private.mkdir()
        self.manifest = self.private / 'prepared.json'
        self.report = {
            'runtime_manifest_schema': 'local-prepared-v1',
            'status': 'local-prepared-unverified', 'execution': 'local-prepared-unverified',
            'lifecycle_phase': 'prepared', 'strict_evidence': False, 'production_access': False,
            'existing_application_preserved': True, 'loopback_url': 'http://127.0.0.1:18087',
            'execution_location': 'local-wsl-docker', 'runtime_profile': 'local-wsl',
            'oracle_instance_id': 'lims-local-runtime-oracle', 'oracle_container': 'lims-local-runtime-oracle',
            'oracle_reused': True, 'redis_container': 'lims-local-runtime-redis', 'redis_reused': True,
            'baseline_schema': 'LIMS_BASELINE_LOCAL', 'runtime_schema': 'LIMS_RUNTIME_LOCAL',
            'baseline_schema_hash': 'a'*64, 'runtime_schema_hash_before': 'b'*64,
            'baseline_key_data_hash': 'c'*64, 'runtime_key_data_hash_before': 'd'*64,
            'runtime_schema_hash_after': None, 'runtime_key_data_hash_after': None,
            'baseline_schema_immutable': None, 'runtime_schema_restored': None, 'runtime_dirty': None,
            'schema_copy_method': 'supplied-runtime-before-content-restore-target', 'baseline_refreshed': False,
            'route_execution_model': {'boundary_process': False, 'internal_http_bridge': False,
                                      'jni_replacement_process': False},
            'milestone_id': 'milestone-test', 'clone_identity': 'prepared-local-test',
            'schema_policy_id': 'equal-schema',
        }
        self.report['schema_policy_sha256'] = hashlib.sha256(b'{"id":"equal-schema"}').hexdigest()
        self.report['source_dirty'] = False
        self.report.update(self.python_json('from source_binding import capture_binding,binding_metadata; '
            'import json; print(json.dumps(binding_metadata(capture_binding())))'))

    def git(self, *args):
        return subprocess.run(['git', '-C', str(self.repo), *args], check=True, capture_output=True, text=True)

    def python_json(self, code):
        result = subprocess.run([sys.executable, '-c', code], cwd=self.repo / 'scripts',
                                check=True, capture_output=True, text=True)
        return json.loads(result.stdout)

    def admit(self):
        self.manifest.write_text(json.dumps(self.report))
        return subprocess.run([sys.executable, str(self.repo / 'scripts/owned_runtime_contract.py'),
                               '--report', str(self.manifest), '--mode', 'strict'],
                              capture_output=True, text=True, timeout=30)

    def valid_captures(self):
        observation = fixtures.observation('2026-11-07T02:00:00+00:00')
        baseline = fixtures.fingerprint(fixtures.OWNERS[0])
        runtime = fixtures.fingerprint(fixtures.OWNERS[1], '3'*64)
        self.report.update(
            baseline_schema_hash=observation['schemas'][fixtures.OWNERS[0]]['column_metadata']['sha256'],
            runtime_schema_hash_before=observation['schemas'][fixtures.OWNERS[1]]['column_metadata']['sha256'],
            baseline_key_data_hash=baseline['schema_sha256'], runtime_key_data_hash_before=runtime['schema_sha256'],
            runtime_identity={'network_id': observation['network']['id'],
                              'container_ids': {role: row['id'] for role, row in observation['containers'].items()},
                              'containers': observation['containers']})
        self.report['before_files'] = {}
        for role, value in [('observation_before', observation), ('baseline_before', baseline),
                            ('runtime_before', runtime)]:
            self.report['before_files'][role] = self.pin(role + '.json', value)
        distribution = self.private / 'distribution'
        distribution.mkdir()
        (distribution / 'lib').mkdir()
        (distribution / 'app').mkdir()
        (distribution / 'app/lims-owned-application.jar').write_bytes(b'owned app test archive')
        (distribution / 'start.sh').write_bytes(b'#!/bin/sh\nexec java -jar /opt/current-owned/app/lims-owned-application.jar\n')
        (distribution / 'lib/public.jar').write_bytes(b'public dependency test archive')
        (distribution / 'lib/classpath.txt').write_text('public.jar\n')
        self.artifact = distribution / 'app/lims-owned-application.jar'
        artifact_hash = hashlib.sha256(self.artifact.read_bytes()).hexdigest()
        self.report['artifact_sha256'] = {str(self.artifact): artifact_hash}
        self.report['operated_artifact'] = {'path': str(self.artifact), 'sha256': artifact_hash,
                                            'container_path': '/opt/current-owned/app/lims-owned-application.jar'}
        acquisition = {'kind': 'local-operated-artifacts', 'runtime_identity': self.report['runtime_identity'],
                       'artifact_sha256': self.report['artifact_sha256'],
                       'mount': {'source': str(distribution), 'destination': '/opt/current-owned', 'read_only': True},
                       'files': [{'path': str(path), 'sha256': hashlib.sha256(path.read_bytes()).hexdigest(),
                                  'container_path': '/opt/current-owned/' + str(path.relative_to(distribution))}
                                 for path in [self.artifact, distribution / 'start.sh', distribution / 'lib/public.jar', distribution / 'lib/classpath.txt']]}
        acquisition['classpath_files'] = [row['path'] for row in acquisition['files']]
        self.report['artifact_observation'] = self.pin('operated.json', acquisition)
        self.bind_runtime()

    def pin(self, name, value):
        raw = json.dumps(value).encode()
        (self.private / name).write_bytes(raw)
        return {'path': name, 'sha256': hashlib.sha256(raw).hexdigest()}

    def bind_runtime(self):
        self.manifest.write_text(json.dumps(self.report))
        self.report.update(self.python_json(
            'from source_binding import capture_binding; import json; '
            'report=json.load(open(' + repr(str(self.manifest)) + ')); '
            'binding=capture_binding(); local=capture_binding(runtime_contract=report); '
            'binding.update({k:local[k] for k in ("runtime_contract_binding","runtime_contract_digest")}); '
            'print(json.dumps(binding))'))

    def test_observed_prepared_runtime_admits_only_before_state(self):
        self.valid_captures()
        result = self.admit()
        self.assertEqual(result.returncode, 0, result.stderr)
        admitted = json.loads(result.stdout)
        self.assertTrue(admitted['cleanup_pending'])
        self.assertFalse(admitted['strict_evidence'])
        self.assertIsNone(admitted['runtime_key_data_hash_after'])
        self.assertEqual(admitted['runtime_contract_binding'], self.report['runtime_contract_binding'])

    def run_http_batch(self, **changes):
        self.manifest.write_text(json.dumps(self.report))
        document = {'cases': [{'id': 'read', 'method': 'GET', 'path': '/ok', 'expected_status': 200}]}
        arguments = {'base_url': self.report['loopback_url'], 'mode': 'strict', 'role': 'anonymous',
                     'organization': 'none', 'clone_identity': None, 'artifacts': [str(self.artifact)],
                     'output': str(self.private / 'report.json'), 'timeout': 3,
                     'runtime_report': str(self.manifest), 'defer_cleanup': True}
        arguments.update(changes)
        code = ('import json; from pathlib import Path; from spring_api_contract_runner import run_cases; '
                'kw=json.loads(' + repr(json.dumps(arguments)) + '); '
                'kw["artifacts"]=[Path(p) for p in kw["artifacts"]]; '
                'kw["output"]=Path(kw["output"]); kw["runtime_report"]=Path(kw["runtime_report"]); '
                'print(json.dumps(run_cases(json.loads(' + repr(json.dumps(document)) + '), **kw)))')
        return subprocess.run([sys.executable, '-c', code], cwd=self.repo / 'scripts',
                              capture_output=True, text=True, timeout=30)

    def start_http(self):
        server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        thread = threading.Thread(target=server.serve_forever, daemon=True)
        thread.start()
        self.addCleanup(thread.join, 5)
        self.addCleanup(server.server_close)
        self.addCleanup(server.shutdown)
        self.report['loopback_url'] = 'http://127.0.0.1:' + str(server.server_address[1])

    def test_real_http_prepared_batch_preserves_runtime_identity_and_waits_for_cleanup(self):
        self.start_http()
        self.valid_captures()
        result = self.run_http_batch()
        self.assertEqual(result.returncode, 0, result.stderr)
        executed = json.loads(result.stdout)
        self.assertEqual(executed['status'], 'executed-pass-pending-cleanup')
        self.assertEqual(executed['milestone_id'], 'milestone-test')
        self.assertEqual(executed['clone_identity'], 'prepared-local-test')
        self.assertEqual(executed['cases'][0]['runtime_contract_binding'], self.report['runtime_contract_binding'])
        self.assertEqual(executed['runtime_contract_digest'], self.report['runtime_contract_digest'])
        self.assertTrue(executed['cleanup_pending'])
        self.assertIsNone(executed['runtime_contract']['runtime_schema_restored'])

    def test_runner_rejects_wrong_url_identity_artifact_or_future_cleanup_before_http(self):
        self.start_http()
        self.valid_captures()
        alien = self.private / 'unrelated.jar'
        alien.write_bytes(b'unrelated archive')
        for changes in ({'base_url': 'http://127.0.0.1:1'}, {'clone_identity': 'wrong-runtime'},
                        {'artifacts': [str(alien)]}, {'defer_cleanup': False},
                        {'cleanup_contract': {'application_stopped': True, 'runtime_schema_restored': True}}):
            with self.subTest(changes=changes):
                before = Handler.requests
                result = self.run_http_batch(**changes)
                self.assertNotEqual(result.returncode, 0, result.stdout)
                self.assertIn('prepared runtime', result.stderr)
                self.assertEqual(Handler.requests, before)

    def test_prepared_rejects_corrupt_content_pins_and_operated_artifact(self):
        self.valid_captures()
        original = copy.deepcopy(self.report)
        for role in ('observation_before', 'baseline_before', 'runtime_before'):
            with self.subTest(role=role):
                self.report = copy.deepcopy(original)
                self.report['before_files'][role]['sha256'] = '0'*64
                self.assertNotEqual(self.admit().returncode, 0)
        self.report = original
        self.artifact.write_bytes(b'changed application')
        self.assertNotEqual(self.admit().returncode, 0)

    def test_public_prepare_output_is_accepted_by_strict_admission(self):
        # Acquire through the public preparation CLI, including fake Docker
        # inspect/mounted-file hashes, then admit the exact emitted manifest.
        path = ROOT / 'scripts/tests/test-lifecycle-proof.py'
        spec = importlib.util.spec_from_file_location('public_prepare_boundary', path)
        boundary = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(boundary)
        prepared = boundary.LifecycleProofTest()
        prepared.setUp()
        self.addCleanup(prepared.doCleanups)
        result = prepared.cli('scripts/owned_runtime_contract.py', '--report', prepared.prepared_path,
                              '--mode', 'strict')
        admitted = json.loads(result.stdout)
        self.assertEqual(admitted['runtime_manifest_schema'], 'local-prepared-v1')
        self.assertIsNone(admitted['runtime_schema_hash_after'])
        self.assertTrue(admitted['cleanup_pending'])

    def test_prepared_rejects_missing_binding_before_any_replay(self):
        del self.report['implementation_binding']
        result = self.admit()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('implementation_binding', result.stderr)

    def test_prepared_rejects_missing_policy_and_future_success(self):
        self.valid_captures()
        original = copy.deepcopy(self.report)
        for key, value in [('schema_policy_id', None), ('schema_policy_sha256', None),
                           ('runtime_schema_hash_after', 'b'*64), ('runtime_key_data_hash_after', 'd'*64),
                           ('runtime_schema_restored', True), ('runtime_dirty', False),
                           ('baseline_schema_immutable', True), ('source_dirty', True),
                           ('application_stopped', True), ('lifecycle_verified', True),
                           ('cleanup', {'application_stopped': True}),
                           ('baseline_schema_hash', 'missing'), ('runtime_key_data_hash_before', '0'*64)]:
            with self.subTest(key=key):
                self.report = dict(original, **{key: value})
                result = self.admit()
                self.assertNotEqual(result.returncode, 0)
                self.assertIn('prepared runtime', result.stderr)

    def test_prepared_rejects_wrong_location_and_source_changes(self):
        self.report['execution_location'] = 'sss2'
        self.assertIn('execution_location', self.admit().stderr)
        self.report['execution_location'] = 'local-wsl-docker'
        path = self.repo / 'scripts/spring_api_contract_runner.py'
        path.write_text(path.read_text() + '\n# source changed after capture\n')
        result = self.admit()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn('binding', result.stderr)


if __name__ == '__main__':
    unittest.main()
