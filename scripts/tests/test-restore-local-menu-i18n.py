#!/usr/bin/env python3
"""Public restore seam over a real local HTTP server; no database or live app."""
import importlib.util
import json
from pathlib import Path
import sys
import tempfile
import threading
import unittest
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

SCRIPTS = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPTS))
from fast_differential_api_runner import Session
spec = importlib.util.spec_from_file_location('restore_menu', SCRIPTS / 'restore-local-menu-i18n.py')
M = importlib.util.module_from_spec(spec)
spec.loader.exec_module(M)
ROOT = '/secure/core/module/sys/i18ns'
ENTRY = {'code': 'CORE.MENU.MENUNAME.LES.DS', 'description': 'original',
         'defaultMessage': 'original default', 'messages': {'zh-CN': '原始中文', 'en-US': 'Original English'}}


class RestoreTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.journal = Path(self.temp.name) / 'private.jsonl'
        self.calls = []
        self.keys = {}
        self.messages = {}
        self.failure = None
        owner = self
        class Handler(BaseHTTPRequestHandler):
            def log_message(self, *args):
                pass
            def do_POST(self): self.handle_api()
            def do_PUT(self): self.handle_api()
            def do_DELETE(self): self.handle_api()
            def handle_api(self):
                body = json.loads(self.rfile.read(int(self.headers.get('Content-Length', 0))))
                owner.calls.append((self.command, self.path, body))
                if owner.failure == 'redirect':
                    self.send_response(302)
                    self.send_header('Location', 'http://supplier.invalid:18081/unsafe')
                    self.end_headers()
                    return
                status, result = owner.api(self.command, self.path, body)
                data = json.dumps(result).encode()
                self.send_response(status)
                self.send_header('Content-Type', 'application/json')
                self.end_headers()
                self.wfile.write(data)
        self.server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        self.thread = threading.Thread(target=self.server.serve_forever, daemon=True)
        self.thread.start()
        self.session = Session('http://127.0.0.1:' + str(self.server.server_port), 2)

    def tearDown(self):
        self.server.shutdown()
        self.server.server_close()
        self.thread.join()
        self.temp.cleanup()

    def api(self, method, path, body):
        if path == ROOT + '/queries/raw':
            code = body['p']['f']['code_SEQ']
            return 200, {'rows': list(self.keys.get(code, [])), 'total': len(self.keys.get(code, []))}
        if method == 'POST' and path == ROOT:
            owner_journal = [json.loads(x) for x in self.journal.read_text().splitlines()]
            self.assertEqual(owner_journal[-1]['event'], 'create-intent')
            self.assertEqual(body, {'b': ['', [{k: ENTRY[k] for k in ('code', 'description', 'defaultMessage')}]], 'p': {}})
            self.keys[ENTRY['code']] = [{'id': 41, 'code': ENTRY['code']}]
            self.messages[41] = [{'id': 42, 'i18nId': 41, 'i18nConfigId': 'zh-CN', 'message': ENTRY['defaultMessage']},
                                 {'id': 43, 'i18nId': 41, 'i18nConfigId': 'en-US', 'message': ENTRY['defaultMessage']}]
            return 200, (None if self.failure == 'missing-id' else 41)
        if method == 'POST' and path == ROOT + '/41/i18n-messages/queries':
            return 200, {'rows': self.messages.get(41, []), 'total': len(self.messages.get(41, []))}
        if method == 'PUT' and path.startswith(M.MESSAGE_ROOT + '/'):
            if self.failure == 'put':
                return 500, {'error': 'private original text must never be reported'}
            item = body['b'][1][0]
            self.assertEqual(body['p'], {})
            for row in self.messages[41]:
                if str(row['id']) == path.rsplit('/', 1)[1]:
                    self.assertEqual(item['id'], row['id'])
                    row['message'] = item['message']
                    return 200, None
        if method == 'DELETE' and path == ROOT:
            self.assertEqual(body, {'b': ['', [{'id': 41}]], 'p': {}})
            self.keys.pop(ENTRY['code'], None)
            self.messages.pop(41, None)
            return 200, None
        self.fail('unexpected HTTP request: ' + method + ' ' + path)

    def test_dry_run_reads_inventory_without_writes_or_journal(self):
        result = M.restore(self.session.request, [ENTRY], self.journal)
        self.assertEqual(result['missing'], 1)
        self.assertFalse(self.journal.exists())
        self.assertEqual(self.calls, [('POST', ROOT + '/queries/raw',
                                      {'p': {'f': {'code_SEQ': ENTRY['code']}, 'n': 1, 's': 100}})])

    def test_apply_restores_original_locale_values_and_preserves_existing_keys(self):
        old = dict(ENTRY, code='CORE.MENU.EXISTING')
        self.keys[old['code']] = [{'id': 9, 'code': old['code']}]
        result = M.restore(self.session.request, [old, ENTRY], self.journal, apply=True)
        self.assertEqual((result['preexisting'], result['created']), (1, 1))
        self.assertEqual([m['message'] for m in self.messages[41]], ['原始中文', 'Original English'])
        journal = self.journal.read_text()
        self.assertNotIn('原始中文', journal)
        self.assertNotIn('original default', journal)
        self.assertEqual(self.journal.stat().st_mode & 0o777, 0o600)
        self.assertEqual(self.keys[old['code']][0]['id'], 9)

    def test_partial_failure_retains_created_identity_and_missing_id_never_succeeds(self):
        for failure in ('put', 'missing-id'):
            with self.subTest(failure=failure):
                self.keys.clear()
                self.messages.clear()
                self.journal = Path(self.temp.name) / (failure + '.jsonl')
                self.failure = failure
                with self.assertRaises(M.RestoreError):
                    M.restore(self.session.request, [ENTRY], self.journal, apply=True)
                records = [json.loads(x) for x in self.journal.read_text().splitlines()]
                self.assertEqual(records[-1]['event'], 'failed')
                self.assertNotIn('private original text', self.journal.read_text())
                created = [r for r in records if r['event'] == 'created']
                self.assertEqual(len(created), int(failure == 'put'))
                if created:
                    self.assertEqual(created[0]['id'], 41)

    def test_rollback_deletes_only_created_records_verifies_absence_and_is_repeatable(self):
        self.keys['CORE.MENU.EXISTING'] = [{'id': 9, 'code': 'CORE.MENU.EXISTING'}]
        M.restore(self.session.request, [ENTRY], self.journal, apply=True)
        before = self.journal.read_bytes()
        preview = M.rollback(self.session.request, self.journal)
        self.assertEqual(preview['pending'], 1)
        self.assertEqual(self.journal.read_bytes(), before)
        result = M.rollback(self.session.request, self.journal, apply=True)
        self.assertTrue(result['rollback_complete'])
        self.assertEqual(result['deleted'], 1)
        self.assertNotIn(ENTRY['code'], self.keys)
        self.assertEqual(self.keys['CORE.MENU.EXISTING'][0]['id'], 9)
        self.assertTrue(self.journal.read_bytes().startswith(before))
        self.assertTrue(M.rollback(self.session.request, self.journal, apply=True)['rollback_complete'])

    def test_unresolved_create_never_claims_complete_rollback(self):
        self.failure = 'missing-id'
        with self.assertRaises(M.RestoreError):
            M.restore(self.session.request, [ENTRY], self.journal, apply=True)
        result = M.rollback(self.session.request, self.journal, apply=True)
        self.assertFalse(result['rollback_complete'])
        self.assertEqual(result['unresolved'], 1)
        self.assertIn(ENTRY['code'], self.keys)
        self.assertFalse(any(method == 'DELETE' for method, _, _ in self.calls))

    def test_local_url_and_input_scope_reject_unsafe_values(self):
        for url in ('http://127.0.0.1:18081', 'https://127.0.0.1:18087',
                    'http://supplier:18087', 'http://user:pass@127.0.0.1:18087',
                    'http://127.0.0.1:18087/secure', 'http://127.0.0.1:18087?x=1'):
            with self.subTest(url=url), self.assertRaises(M.RestoreError):
                M.local_url(url)
        self.assertEqual(M.local_url('http://127.0.0.1:18087'), 'http://127.0.0.1:18087')
        self.assertEqual(M.local_url('http://localhost:18187/'), 'http://localhost:18187')
        with self.assertRaises(M.RestoreError):
            M.restore(self.session.request, [dict(ENTRY, code='BUSINESS.OTHER')], self.journal, apply=True)
        self.assertFalse(self.calls)

    def test_single_approved_locale_leaves_other_new_locale_at_original_default(self):
        entry = dict(ENTRY, messages={'zh-CN': ENTRY['messages']['zh-CN']})
        result = M.restore(self.session.request, [entry], self.journal, apply=True)
        self.assertEqual(result['created'], 1)
        self.assertEqual([m['message'] for m in self.messages[41]], ['原始中文', 'original default'])

    def test_rollback_refuses_changed_identity_and_tampered_journal(self):
        M.restore(self.session.request, [ENTRY], self.journal, apply=True)
        self.keys[ENTRY['code']][0]['id'] = 99
        with self.assertRaises(M.RestoreError):
            M.rollback(self.session.request, self.journal, apply=True)
        self.assertFalse(any(method == 'DELETE' for method, _, _ in self.calls))
        text = self.journal.read_text().replace('"id": 41', '"id": 99', 1)
        self.journal.write_text(text)
        with self.assertRaises(M.RestoreError):
            M.rollback(self.session.request, self.journal, apply=True)
        self.assertFalse(any(method == 'DELETE' for method, _, _ in self.calls))

    def test_existing_journal_cannot_be_reused_for_new_apply(self):
        M.restore(self.session.request, [ENTRY], self.journal, apply=True)
        first = self.journal.read_bytes()
        with self.assertRaises((M.RestoreError, FileExistsError)):
            M.restore(self.session.request, [ENTRY], self.journal, apply=True)
        self.assertEqual(self.journal.read_bytes(), first)

    def test_cli_rejects_nonlocal_target_before_authentication(self):
        import subprocess
        completed = subprocess.run([sys.executable, str(SCRIPTS / 'restore-local-menu-i18n.py'),
                                    '--base-url', 'http://supplier:18081', '--user', 'fake',
                                    '--password-file', str(Path(self.temp.name) / 'missing'),
                                    '--journal', str(self.journal)], capture_output=True, text=True)
        self.assertEqual(completed.returncode, 1)
        self.assertFalse(self.journal.exists())
        self.assertEqual(json.loads(completed.stderr)['status'], 'failed')

    def test_redirect_cannot_escape_local_transport(self):
        self.failure = 'redirect'
        self.session.opener = M.build_opener(M.ProxyHandler({}), M.HTTPCookieProcessor(self.session.jar), M.RejectRedirects())
        with self.assertRaises(M.RestoreError):
            M.restore(self.session.request, [ENTRY], self.journal)
        self.assertFalse(self.journal.exists())
        self.assertEqual(len(self.calls), 1)


if __name__ == '__main__': unittest.main()
