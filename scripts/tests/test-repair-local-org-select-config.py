#!/usr/bin/env python3
"""Exercise organization configuration repair through real temporary HTTP APIs."""
import copy
import importlib.util
import json
from pathlib import Path
import stat
import subprocess
import sys
import tempfile
import threading
import unittest
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

SCRIPTS = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPTS))
from fast_differential_api_runner import Session
spec = importlib.util.spec_from_file_location('org_repair', SCRIPTS / 'repair-local-org-select-config.py')
M = importlib.util.module_from_spec(spec)
spec.loader.exec_module(M)
ROOT = '/secure/core/module/admin/select-configs'
TARGET = '/secure/core/module/mdm/orgs/queries/selectable'
ORIGINAL = {'id': 2399, 'category': 'org', 'name': 'Organization', 'cacheable': '1',
            'url': TARGET, 'valueField': 'id', 'textField': 'orgName'}
CONFLICT = {'id': 1497029569951488, 'category': 'org', 'name': 'Organization codes', 'cacheable': '1',
            'url': '/secure/core/module/sys/codes/queries/selectable?codeCategoryId=org',
            'valueField': 'code', 'textField': 'codeName'}


class RepairTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.journal = Path(self.temp.name) / 'org.jsonl'
        self.configs = copy.deepcopy([CONFLICT, ORIGINAL])
        self.calls = []
        self.failure = None
        self.inventory_reads = 0
        self.target_reads = 0
        owner = self

        class Handler(BaseHTTPRequestHandler):
            def log_message(self, *args):
                pass

            def do_POST(self): self.handle_api()
            def do_GET(self): self.handle_api()
            def do_PUT(self): self.handle_api()

            def handle_api(self):
                raw = self.rfile.read(int(self.headers.get('Content-Length', 0)))
                body = json.loads(raw) if raw else None
                owner.calls.append((self.command, self.path, body))
                status, result = owner.api(self.command, self.path, body)
                self.send_response(status)
                if status == 302:
                    self.send_header('Location', '/forbidden-redirect')
                self.send_header('Content-Type', 'application/json')
                self.end_headers()
                self.wfile.write(json.dumps(result).encode())

        self.server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        self.thread = threading.Thread(target=self.server.serve_forever, daemon=True)
        self.thread.start()
        self.session = M.local_session('http://127.0.0.1:' + str(self.server.server_port), timeout=2)

    def tearDown(self):
        self.server.shutdown()
        self.server.server_close()
        self.thread.join()
        self.temp.cleanup()

    def api(self, method, path, body):
        if self.failure == 'redirect':
            return 302, None
        if method == 'POST' and path == ROOT + '/queries/raw':
            self.assertEqual(body, {'p': {'f': {'category_SEQ': 'org'}, 'n': 1, 's': 100}})
            self.inventory_reads += 1
            if self.failure == 'changed' and self.inventory_reads == 2:
                self.configs[0]['name'] = 'Concurrent edit'
            return 200, {'rows': copy.deepcopy(self.configs),
                         'total': len(self.configs) + int(self.failure == 'truncated')}
        if method == 'GET' and path == ROOT + '/queries/selectable':
            row = max(self.configs, key=lambda r: int(r['id']))
            if self.failure == 'wrong-effective':
                return 200, [dict(row, valueField='code')]
            return 200, [ORIGINAL if self.failure == 'cached' else row]
        if method == 'GET' and path == TARGET + '?sampleDepartment_SEQ=1':
            self.target_reads += 1
            if self.failure == 'target-after-write' and self.target_reads > 1:
                return 500, {'error': 'private HTTP response'}
            if self.failure == 'empty-target':
                return 200, []
            if self.failure == 'target':
                return 500, {'error': 'private HTTP response'}
            return 200, [{'id': 2054, 'orgName': 'Sample department'}]
        if method == 'PUT' and path == ROOT + '/1497029569951488':
            records = [json.loads(line) for line in self.journal.read_text().splitlines()]
            self.assertIn(records[-1]['event'], ('update-intent', 'rollback-intent'))
            self.assertEqual(stat.S_IMODE(self.journal.stat().st_mode), 0o600)
            self.assertEqual(body['p'], {})
            self.assertEqual(body['b'][0], '')
            self.assertEqual(len(body['b'][1]), 1)
            row = body['b'][1][0]
            self.assertEqual(row['id'], CONFLICT['id'])
            if self.failure == 'put':
                return 500, {'error': 'private HTTP response'}
            self.configs[0].update(row)
            return 200, None
        self.fail('unexpected HTTP request: ' + method + ' ' + path)

    def test_apply_updates_only_three_fields_then_checks_effective_and_real_target(self):
        result = M.repair(self.session.request, self.journal, apply=True)
        self.assertEqual(result['updated'], 1)
        self.assertFalse(result['strict_evidence'])
        self.assertEqual(self.configs[1], ORIGINAL)
        self.assertEqual(self.configs[0]['name'], CONFLICT['name'])
        self.assertEqual(self.configs[0]['cacheable'], CONFLICT['cacheable'])
        writes = [(path, body) for method, path, body in self.calls if method == 'PUT']
        self.assertEqual(writes, [(ROOT + '/1497029569951488', {
            'b': ['', [{'id': 1497029569951488, 'url': TARGET,
                        'valueField': 'id', 'textField': 'orgName'}]], 'p': {}})])
        self.assertEqual(self.calls[-2:], [('GET', ROOT + '/queries/selectable', None),
                                         ('GET', TARGET + '?sampleDepartment_SEQ=1', None)])
        records = [json.loads(line) for line in self.journal.read_text().splitlines()]
        intent = next(r for r in records if r['event'] == 'update-intent')
        self.assertEqual(intent['before'], CONFLICT)
        self.assertEqual(intent['after']['url'], TARGET)
        self.assertEqual(records[-1]['event'], 'complete')
        rollback = M.rollback(self.session.request, self.journal, apply=True)
        self.assertTrue(rollback['rollback_complete'])
        self.assertEqual(self.configs, [CONFLICT, ORIGINAL])
        self.assertEqual(M.rollback(self.session.request, self.journal, apply=True)['restored'], 0)

    def test_stale_effective_config_identity_is_not_success(self):
        self.failure = 'cached'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.assertEqual(json.loads(self.journal.read_text().splitlines()[-1])['event'], 'failed')
        self.failure = None
        self.assertTrue(M.rollback(self.session.request, self.journal, apply=True)['rollback_complete'])

    def test_unrecognized_inventory_never_writes(self):
        variants = []
        wrong_original = copy.deepcopy([CONFLICT, ORIGINAL])
        wrong_original[1]['textField'] = 'codeName'
        variants.append(wrong_original)
        unknown = copy.deepcopy([CONFLICT, ORIGINAL])
        unknown[0]['url'] = '/unrecognized'
        variants.append(unknown)
        third = dict(ORIGINAL, id=2400)
        variants.append(copy.deepcopy([CONFLICT, ORIGINAL, third]))
        for configs in variants:
            with self.subTest(configs=configs):
                self.configs = configs
                with self.assertRaises(M.RepairError):
                    M.repair(self.session.request, self.journal, apply=True)
                self.assertFalse(self.journal.exists())
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_failed_target_prevents_apply_and_does_not_strip_filter(self):
        self.failure = 'target'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.assertFalse(self.journal.exists())
        self.assertEqual(self.configs, [CONFLICT, ORIGINAL])
        self.assertEqual(self.calls[-1], ('GET', TARGET + '?sampleDepartment_SEQ=1', None))

    def test_failed_write_retains_journal_and_can_rollback_ambiguous_intent(self):
        self.failure = 'put'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.failure = None
        self.assertTrue(M.rollback(self.session.request, self.journal, apply=True)['rollback_complete'])
        self.assertEqual(self.configs, [CONFLICT, ORIGINAL])

    def test_rollback_refuses_record_changed_since_apply(self):
        M.repair(self.session.request, self.journal, apply=True)
        self.configs[0]['name'] = 'changed by someone else'
        writes = sum(method == 'PUT' for method, _, _ in self.calls)
        with self.assertRaises(M.RepairError):
            M.rollback(self.session.request, self.journal, apply=True)
        self.assertEqual(sum(method == 'PUT' for method, _, _ in self.calls), writes)

    def test_rollback_dry_run_is_read_only_and_tampering_is_rejected(self):
        M.repair(self.session.request, self.journal, apply=True)
        content = self.journal.read_bytes()
        writes = sum(method == 'PUT' for method, _, _ in self.calls)
        self.assertEqual(M.rollback(self.session.request, self.journal)['mode'], 'rollback-dry-run')
        self.assertEqual(self.journal.read_bytes(), content)
        self.journal.write_bytes(content.replace(b'codeName', b'codeEvil'))
        with self.assertRaises(M.RepairError):
            M.rollback(self.session.request, self.journal, apply=True)
        self.assertEqual(sum(method == 'PUT' for method, _, _ in self.calls), writes)

    def test_existing_journal_is_not_overwritten(self):
        self.journal.write_text('existing')
        with self.assertRaises(FileExistsError):
            M.repair(self.session.request, self.journal, apply=True)
        self.assertEqual(self.journal.read_text(), 'existing')
        self.assertEqual(self.configs, [CONFLICT, ORIGINAL])

    def test_redirect_is_rejected_without_following(self):
        self.failure = 'redirect'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.assertEqual(len(self.calls), 1)
        self.assertFalse(self.journal.exists())

    def test_cli_rejects_other_ports_before_authentication_without_secret_output(self):
        password = Path(self.temp.name) / 'secret'
        password.write_text('TEST_PASSWORD=private-password-sentinel')
        password.chmod(0o600)
        for base in ('http://127.0.0.1:' + str(self.server.server_port),
                     'http://127.0.0.1:18081', 'http://example.invalid:18087',
                     'http://user:secret@localhost:18087', 'http://localhost:18087/extra'):
            result = subprocess.run([sys.executable, str(SCRIPTS / 'repair-local-org-select-config.py'),
                                     '--base-url', base, '--user', 'private-user-sentinel',
                                     '--password-file', str(password), '--journal', str(self.journal)],
                                    capture_output=True, text=True, check=False)
            self.assertEqual(result.returncode, 1)
            self.assertNotIn('sentinel', result.stdout + result.stderr)
            self.assertNotIn('Traceback', result.stderr)
        self.assertEqual(self.calls, [])

    def test_incomplete_inventory_or_empty_target_never_writes(self):
        for failure in ('truncated', 'empty-target'):
            with self.subTest(failure=failure):
                self.failure = failure
                with self.assertRaises(M.RepairError):
                    M.repair(self.session.request, self.journal, apply=True)
                self.assertFalse(self.journal.exists())
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_change_between_inventory_and_put_prevents_write(self):
        self.failure = 'changed'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))
        self.assertEqual(json.loads(self.journal.read_text().splitlines()[-1])['event'], 'failed')

    def test_effective_cache_must_match_repaired_fields(self):
        self.failure = 'wrong-effective'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.failure = None
        self.assertTrue(M.rollback(self.session.request, self.journal, apply=True)['rollback_complete'])

    def test_already_correct_configuration_is_not_written(self):
        self.configs[0].update(url=TARGET, valueField='id', textField='orgName')
        result = M.repair(self.session.request, self.journal, apply=True)
        self.assertEqual(result['updated'], 0)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))
        self.assertTrue(M.rollback(self.session.request, self.journal, apply=True)['rollback_complete'])

    def test_failed_target_after_write_retains_recoverable_intent(self):
        self.failure = 'target-after-write'
        with self.assertRaises(M.RepairError):
            M.repair(self.session.request, self.journal, apply=True)
        self.assertEqual(self.configs[0]['url'], TARGET)
        self.failure = None
        result = M.rollback(self.session.request, self.journal, apply=True)
        self.assertTrue(result['rollback_complete'])
        self.assertEqual(self.configs, [CONFLICT, ORIGINAL])

    def test_default_dry_run_identifies_conflict_without_writing(self):
        result = M.repair(self.session.request, self.journal)
        self.assertFalse(result['strict_evidence'])
        self.assertEqual(result['mode'], 'dry-run')
        self.assertEqual(result['planned'], 1)
        self.assertEqual(result['updated'], 0)
        self.assertEqual(self.configs, [CONFLICT, ORIGINAL])
        self.assertFalse(self.journal.exists())
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))


if __name__ == '__main__':
    unittest.main()
