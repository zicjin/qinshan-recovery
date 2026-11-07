#!/usr/bin/env python3
"""Menu repair public seam over real HTTP; never accesses a live app or DB."""
import copy
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
spec = importlib.util.spec_from_file_location('repair_menu', SCRIPTS / 'repair-local-menu-hrefs.py')
M = importlib.util.module_from_spec(spec)
spec.loader.exec_module(M)
ROOT = '/secure/core/module/sys/menus'
OLD = '/secure/emc/dingan/module/mdm/file-modifications/page/audit-list?bpmn_statusCode=fileAudit'
NEW = '/secure/emc/module/mdm/file-modifications/page/audit-list?bpmn_statusCode=fileAudit'
HTML = '<script>' + ' ' * 220 + 'Gikam.requirejs(["/static/emc/module/mdm/file-modifications/emc-file-modification-audit-list.js"]); emcFileModification.auditPage.init({});</script>'


class RepairTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.journal = Path(self.temp.name) / 'journal.jsonl'
        self.calls = []
        self.menu = {'id': 9602, 'href': OLD, 'i18nCode': 'CORE.MENU.FILE', 'ext$': {},
                     'icon': 'icon-file', 'menuName': 'File', 'menuInitName': None,
                     'parentId': 3, 'orderNo': 7}
        self.data = {9602: self.menu}
        self.failure = None
        self.fragments = {}
        owner = self
        class Handler(BaseHTTPRequestHandler):
            def log_message(self, *args): pass
            def do_GET(self): self.serve()
            def do_POST(self): self.serve()
            def do_PUT(self): self.serve()
            def serve(self):
                body = self.rfile.read(int(self.headers.get('Content-Length', 0)))
                body = json.loads(body) if body else None
                owner.calls.append((self.command, self.path, body))
                status, content_type, response = owner.api(self.command, self.path, body)
                self.send_response(status)
                self.send_header('Content-Type', content_type)
                if status == 302:
                    self.send_header('Location', 'http://supplier.invalid:18081/private')
                self.end_headers()
                self.wfile.write(response.encode() if isinstance(response, str) else json.dumps(response).encode())
        self.server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        self.thread = threading.Thread(target=self.server.serve_forever, daemon=True)
        self.thread.start()
        self.session = Session('http://127.0.0.1:' + str(self.server.server_port), 2)
        self.session.opener = M.build_opener(M.ProxyHandler({}), M.HTTPCookieProcessor(self.session.jar), M.RejectRedirects())
        self.request = M.request_with_fragments(self.session)

    def tearDown(self):
        self.server.shutdown()
        self.server.server_close()
        self.thread.join()
        self.temp.cleanup()

    def api(self, method, path, body):
        if path == ROOT + '/queries/raw':
            all_rows = list(self.data.values())
            n, size = body['p']['n'], body['p']['s']
            if self.failure == 'duplicate-inventory': all_rows += all_rows
            if self.failure == 'incomplete-inventory':
                return 200, 'application/json', {'rows': [], 'total': 1}
            return 200, 'application/json', {'rows': all_rows[(n-1)*size:n*size], 'total': len(all_rows)}
        if method == 'GET' and path.startswith(ROOT + '/'):
            row = copy.deepcopy(self.data[int(path.rsplit('/', 1)[1])])
            if self.failure == 'identity': row['id'] = 999
            if self.failure == 'href-drift': row['href'] = '/concurrent-change'
            return 200, 'application/json', row
        if method == 'PUT':
            self.assertTrue(path.startswith(ROOT + '/'))
            records = [json.loads(line) for line in self.journal.read_text().splitlines()]
            self.assertIn(records[-1]['event'], ('update-intent', 'rollback-intent'))
            item = body['b'][1][0]
            self.assertEqual(set(item), {'id', 'href'})
            self.assertEqual(body, {'b': ['', [item]], 'p': {}})
            self.assertEqual(str(item['id']), path.rsplit('/', 1)[1])
            self.data[item['id']]['href'] = item['href']
            if self.failure == 'nonhref': self.data[item['id']]['icon'] = 'changed'
            if self.failure == 'put-after-write': return 500, 'application/json', {'error': 'secret must not leak'}
            return 200, 'application/json', None
        if method == 'GET':
            if self.failure == 'redirect': return 302, 'text/html', ''
            if self.failure == 'error-json': return 200, 'application/json', {'error': 'failure'}
            if self.failure == 'empty': return 200, 'text/html', ''
            if self.failure == 'login': return 200, 'text/html', '<html><form>login</form></html>'
            if self.failure == 'wrong-init': return 200, 'text/html', '<script>coreOther.editPage.init();</script>'
            if self.failure == '500': return 500, 'text/html', HTML
            return 200, 'text/html', self.fragments.get(path, HTML)
        self.fail('unexpected request')

    def test_apply_changes_only_href_preserves_query_and_private_journal(self):
        before = copy.deepcopy(self.menu)
        result = M.repair(self.request, self.journal, apply=True)
        expected = dict(before, href=NEW)
        self.assertEqual(self.menu, expected)
        self.assertEqual(result['updated'], 1)
        self.assertEqual(self.journal.stat().st_mode & 0o777, 0o600)
        records = [json.loads(line) for line in self.journal.read_text().splitlines()]
        intent = next(r for r in records if r['event'] == 'update-intent')
        self.assertEqual((intent['before_href'], intent['after_href']), (OLD, NEW))
        self.assertNotIn('CORE.MENU.FILE', self.journal.read_text())

    def test_rollback_restores_only_owned_href_and_is_repeatable(self):
        original = copy.deepcopy(self.menu)
        M.repair(self.request, self.journal, apply=True)
        before = self.journal.read_bytes()
        preview = M.rollback(self.request, self.journal)
        self.assertEqual(preview['pending'], 1)
        self.assertEqual(self.journal.read_bytes(), before)
        self.assertEqual(self.menu['href'], NEW)
        result = M.rollback(self.request, self.journal, apply=True)
        self.assertTrue(result['rollback_complete'])
        self.assertEqual(self.menu, original)
        self.assertTrue(self.journal.read_bytes().startswith(before))
        self.assertTrue(M.rollback(self.request, self.journal, apply=True)['rollback_complete'])

    def test_cli_refuses_unsafe_endpoint_before_auth_or_journal(self):
        import subprocess
        for url in ('http://127.0.0.1:18081', 'https://127.0.0.1:18087',
                    'http://supplier:18087', 'http://user:pass@127.0.0.1:18087',
                    'http://127.0.0.1:18087/secure', 'http://127.0.0.1:18087?x=1'):
            completed = subprocess.run([sys.executable, str(SCRIPTS / 'repair-local-menu-hrefs.py'),
                '--base-url', url, '--user', 'fake', '--password-file', '/missing',
                '--journal', str(self.journal)], capture_output=True, text=True)
            self.assertEqual(completed.returncode, 1)
            self.assertEqual(json.loads(completed.stderr)['status'], 'failed')
        self.assertFalse(self.journal.exists())
        self.assertFalse(self.calls)

    def test_error_pages_empty_html_wrong_initializer_and_redirect_never_allow_put(self):
        for failure in ('error-json', 'empty', 'login', 'wrong-init', '500', 'redirect'):
            with self.subTest(failure=failure):
                self.failure = failure
                self.calls.clear()
                with self.assertRaises(Exception):
                    M.repair(self.request, self.journal, apply=True)
                self.assertEqual(self.menu['href'], OLD)
                self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_partial_write_failure_stays_recoverable_and_never_claims_success(self):
        self.failure = 'put-after-write'
        with self.assertRaises(M.RestoreError):
            M.repair(self.request, self.journal, apply=True)
        self.assertEqual(self.menu['href'], NEW)
        self.assertEqual(json.loads(self.journal.read_text().splitlines()[-1])['event'], 'failed')
        self.assertNotIn('secret must not leak', self.journal.read_text())
        self.failure = None
        self.assertTrue(M.rollback(self.request, self.journal, apply=True)['rollback_complete'])
        self.assertEqual(self.menu['href'], OLD)

    def test_nonhref_mutation_fails_verification_and_rollback_refuses_overwrite(self):
        self.failure = 'nonhref'
        with self.assertRaises(M.RestoreError):
            M.repair(self.request, self.journal, apply=True)
        self.calls.clear()
        self.failure = None
        with self.assertRaises(M.RestoreError):
            M.rollback(self.request, self.journal, apply=True)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_identity_drift_fails_before_any_write(self):
        self.failure = 'identity'
        with self.assertRaises(M.RestoreError):
            M.repair(self.request, self.journal, apply=True)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_rollback_refuses_unexpected_href_identity_and_tampered_journal(self):
        M.repair(self.request, self.journal, apply=True)
        self.menu['href'] = '/somebody-elses-href'
        self.calls.clear()
        with self.assertRaises(M.RestoreError):
            M.rollback(self.request, self.journal, apply=True)
        self.menu['href'] = NEW
        self.failure = 'identity'
        with self.assertRaises(M.RestoreError):
            M.rollback(self.request, self.journal, apply=True)
        self.failure = None
        self.journal.write_text(self.journal.read_text().replace('9602', '9603', 1))
        with self.assertRaises(M.RestoreError):
            M.rollback(self.request, self.journal, apply=True)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_preexisting_target_and_unapproved_paths_remain_unchanged(self):
        self.menu['href'] = NEW
        self.data[9603] = dict(self.menu, id=9603, href='/secure/emc/dingan/project-run-list')
        self.data[9604] = dict(self.menu, id=9604, href='/secure/emc/dingan/module/mdm/file-modifications/page/choose-list')
        result = M.repair(self.request, self.journal, apply=True)
        self.assertEqual((result['planned'], result['updated']), (0, 0))
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_unsafe_query_variants_never_write_or_persist_sensitive_values(self):
        for query in ('bpmn_statusCode=unapproved', 'token=private-secret', 'bpmn_statusCode=x&bpmn_statusCode=y',
                      'bpmn_statusCode=%66ileAudit', 'bpmn_statusCode=x#fragment',
                      'bpmn_statusCode=x&password=private-secret'):
            with self.subTest(query=query):
                self.menu['href'] = OLD.split('?')[0] + '?' + query
                with self.assertRaises(M.RestoreError):
                    M.repair(self.request, self.journal, apply=True)
                self.assertFalse(self.journal.exists())
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_inventory_paginates_all_406_menu_rows_without_skipping_target(self):
        self.data = {i: dict(self.menu, id=i, href='') for i in range(1, 407)}
        self.data[406]['href'] = OLD
        result = M.repair(self.request, self.journal)
        self.assertEqual((result['inventory'], result['planned']), (406, 1))
        pages = [body['p']['n'] for method, path, body in self.calls if path == ROOT + '/queries/raw']
        self.assertEqual(pages, [1, 2, 3, 4, 5])

    def test_existing_or_untrusted_journal_cannot_overwrite(self):
        self.journal.write_text('private existing data')
        self.journal.chmod(0o600)
        with self.assertRaises(FileExistsError):
            M.repair(self.request, self.journal, apply=True)
        self.assertEqual(self.journal.read_text(), 'private existing data')
        with self.assertRaises(Exception):
            M.rollback(self.request, self.journal, apply=True)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))

    def test_all_exact_source_backed_menu_families_and_queries(self):
        # Expected targets and initializers are literals from controller/template sources.
        cases = [
            ('/secure/emc/dingan/module/mdm/file-modifications/page/edit-list',
             '/secure/emc/module/mdm/file-modifications/page/edit-list', 'emcFileModification.editPage.init'),
            ('/secure/emc/dingan/module/mdm/file-modifications/page/search-list',
             '/secure/emc/module/mdm/file-modifications/page/search-list', 'emcFileModification.searchPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-checks/page/edit-list',
             '/secure/emc/module/mdm/equipt/equip-checks/page/edit-list', 'emcEquipCheck.editPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-checks/page/audit-list?bpmn_statusCode=equiptCheck',
             '/secure/emc/module/mdm/equipt/equip-checks/page/audit-list?bpmn_statusCode=equiptCheck', 'emcEquipCheck.auditPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-checks/page/search-list',
             '/secure/emc/module/mdm/equipt/equip-checks/page/search-list', 'emcEquipCheck.searchPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-checks/page/appearance-list',
             '/secure/emc/module/mdm/equipt/equip-checks/page/appearance-list', 'emcEquipAppearance.editPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-checks/page/check-list',
             '/secure/emc/module/mdm/equipt/equip-checks/page/check-list', 'emcEquipAppearance.choosePage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-verifications/page/edit-list',
             '/secure/emc/module/mdm/equipt/equip-verifications/page/edit-list', 'emcEquipVerification.editPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-verifications/page/audit-list?bpmn_statusCode=equiptVerification',
             '/secure/emc/module/mdm/equipt/equip-verifications/page/audit-list?bpmn_statusCode=equiptVerification', 'emcEquipVerification.auditPage.init'),
            ('/secure/emc/dingan/module/mdm/equipt/equip-verifications/page/search-list',
             '/secure/emc/module/mdm/equipt/equip-verifications/page/search-list', 'emcEquipVerification.searchPage.init'),
            ('/secure/emc/dingan/module/mdm/basemdm/mtl-use-records/page/edit-list',
             '/secure/emc/module/mdm/basemdm/mtl-use-records/page/edit-list', 'emcMtlUseRecord.editPage.init'),
            ('/secure/core/module/item/les-dses/page/edit-list',
             '/secure/core/les/les-dses/page/edit-list', 'coreLesDs.editPage.init'),
            ('/secure/core/module/item/les-templates/page/edit-list',
             '/secure/core/les/les-templates/page/edit-list', 'coreLesTemplate.editListPage.init'),
            ('/secure/core/module/item/les-templates/page/audit-list',
             '/secure/core/les/les-templates/page/audit-list', 'coreLesTemplate.auditListPage.init'),
            ('/secure/core/audit/audit-logs/page/search-list',
             '/secure/core/module/sys/audit/audit-logs/page/search-list', 'coreAuditLog.searchPage.init'),
            ('/secure/emc/dingan/module/mdm/file-modifications/page/audit-list?bpmn_statusCode=fileConfirm',
             '/secure/emc/module/mdm/file-modifications/page/audit-list?bpmn_statusCode=fileConfirm', 'emcFileModification.auditPage.init')]
        self.data = {i: dict(self.menu, id=i, href=old) for i, (old, _, _) in enumerate(cases, 1)}
        for _, target, init in cases:
            self.fragments[target] = '<script>' + init + '({});</script>'
        result = M.repair(self.request, self.journal, apply=True)
        self.assertEqual(result['updated'], 16)
        self.assertEqual([r['href'] for r in self.data.values()], [target for _, target, _ in cases])
        self.assertTrue(M.rollback(self.request, self.journal, apply=True)['rollback_complete'])
        self.assertEqual([r['href'] for r in self.data.values()], [old for old, _, _ in cases])

    def test_incomplete_duplicate_inventory_or_href_drift_never_writes(self):
        for failure in ('incomplete-inventory', 'duplicate-inventory', 'href-drift'):
            self.failure = failure
            with self.subTest(failure=failure), self.assertRaises(M.RestoreError):
                M.repair(self.request, self.journal, apply=True)
            self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))
            self.assertFalse(self.journal.exists())

    def test_dry_run_inventories_exact_query_and_never_changes_metadata(self):
        before = copy.deepcopy(self.menu)
        result = M.repair(self.request, self.journal)
        self.assertEqual(result['planned'], 1)
        self.assertEqual(result['updated'], 0)
        self.assertEqual(self.menu, before)
        self.assertFalse(any(method == 'PUT' for method, _, _ in self.calls))
        self.assertFalse(result['strict_evidence'])


if __name__ == '__main__':
    unittest.main()
