#!/usr/bin/env python3
"""Golden write-proof dispositions through runner and promotion public seams."""
import copy
import datetime as dt
import hashlib
import importlib.util
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
import sys
import tempfile
import threading
import subprocess
import unittest
from unittest import mock

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import spring_api_contract_runner as runner
from evidence_output import strict_case_contract
from http_write_proof import validate_case_proof, register_pending_report, transcript


class Handler(BaseHTTPRequestHandler):
    value = None
    audits = []
    broken_restore = False
    corrupt_failure = False
    wrong_audit = None
    naive_timestamp = False
    event_value = 'I'
    identity_problem = None
    account_reads = 0
    public_key = b''
    source_shape = False
    empty_login = False

    def do_PUT(self):
        body = json.loads(self.rfile.read(int(self.headers.get('Content-Length', 0))))
        old = type(self).value
        type(self).value = body['name']
        type(self).audits.append({'id': 81, 'targetId': 'T_EMC_SEAL_RULE$71', 'auditTrailType': 'UPDATE',
            'createdById': 'manager', 'createdByOrgId': 'org-1', 'createdTime': dt.datetime.now(dt.timezone.utc).isoformat(),
            'lines': [{'id': 91, 'column': 'NAME', 'oldValue': old, 'newValue': body['name']}]})
        self.send_response(200)
        self.end_headers()

    def account(self):
        cls = type(self)
        identity = {'id': 'manager', 'orgId': 'org-1', 'loginToken': 'private-login-token',
                    'ext$': {'loginorgid': 'org-1', 'roles': json.dumps([{'roleId': 'manager', 'userId': 'manager', 'secret': 'private-role-extra'}])},
                    'email': 'private-account-extra'}
        if cls.identity_problem == 'missing-org': identity['ext$']['loginorgid'] = ''
        if cls.identity_problem == 'labels': identity['ext$']['roles'] = '["manager"]'
        if cls.identity_problem == 'assignment': identity['ext$']['roles'] = '[{"roleId":"manager","userId":"other"}]'
        if cls.account_reads and cls.identity_problem == 'account-principal': identity['id'] = 'other'
        if cls.account_reads and cls.identity_problem == 'account-org': identity['ext$']['loginorgid'] = 'other'
        if cls.identity_problem == 'principal': identity['id'] = 'other'
        if cls.identity_problem == 'organization': identity['ext$']['loginorgid'] = 'other'
        if cls.identity_problem == 'missing-role' or (cls.identity_problem == 'revoked' and cls.account_reads > 2): identity['ext$']['roles'] = '[]'
        return identity

    def respond(self, value, status=200):
        data = json.dumps(value).encode()
        self.send_response(status)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        self.wfile.write(data)

    def do_GET(self):
        if self.path == '/open/security/public-key':
            self.send_response(200)
            self.end_headers()
            self.wfile.write(self.public_key)
            return
        if self.path == '/core/module/sys/logins/account':
            type(self).account_reads += 1
            if self.identity_problem in ('fake-login', 'unauthorized'):
                self.respond({'error': 'unauthenticated'}, 401)
            elif self.identity_problem == 'changed-cookie':
                self.send_response(200)
                self.send_header('Set-Cookie', 'SESSION=different-private-session; Path=/; HttpOnly')
                self.end_headers()
                self.wfile.write(json.dumps(self.account()).encode())
            else:
                self.respond(self.account() if self.headers.get('Cookie') == 'SESSION=private-session-value' else {'error': 'unauthenticated'})
            return
        if self.path == '/state/queries' and self.source_shape:
            self.respond({'rows': [] if self.value is None else [{'id': 71, 'name': self.value, 'createdTime': 'dynamic-server-time', 'version': 7}]})
            return
        if self.path == '/lines/queries':
            self.respond({'rows': [{**line, 'record': row['id']} for row in self.audits for line in row['lines']]})
        else:
            self.respond({'rows': self.audits} if self.path == '/audit/queries' else {'rows': [] if self.value is None else [{'id': 'entity-7', 'name': self.value}]})

    def do_POST(self):
        length = int(self.headers.get('Content-Length', 0))
        raw = self.rfile.read(length)
        if self.path == '/core/module/sys/login':
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            if self.identity_problem != 'no-cookie':
                cookie = '' if self.identity_problem == 'empty-cookie' else 'private-session-value'
                self.send_header('Set-Cookie', 'SESSION=' + cookie + '; Path=/; HttpOnly')
            self.end_headers()
            if not self.empty_login:
                self.wfile.write(json.dumps({'ok': True} if self.identity_problem == 'fake-login' else self.account()).encode())
            return
        body = json.loads(raw or b'{}')
        if self.path == '/source-setup':
            type(self).value = 'before'
            self.respond(71)
            return
        if self.path == '/restore':
            if not self.broken_restore:
                type(self).value = None
            self.respond({'ok': True})
        elif body.get('reject'):
            if self.corrupt_failure:
                type(self).value = 'corrupted'
            self.respond({'code': 'too-long'}, 422)
        else:
            type(self).value = 'created'
            row = {'id': 'audit-11', 'entity': 'entity-7', 'event': self.event_value, 'principal': 'manager',
                   'role': 'manager', 'org': 'org-1', 'at': dt.datetime.now(dt.timezone.utc).isoformat(),
                   'lines': [{'id': 'line-12', 'field': 'name', 'old': None, 'new': 'created'}]}
            if self.naive_timestamp:
                row['at'] = dt.datetime.now(dt.timezone.utc).strftime('%Y-%m-%d %H:%M:%S')
            if self.wrong_audit:
                row[self.wrong_audit] = 'wrong'
            type(self).audits.append(row)
            self.respond({'id': 'entity-7'}, 201)

    def log_message(self, *_): pass


class ProofTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        with tempfile.TemporaryDirectory() as keydir:
            key = Path(keydir) / 'key.pem'
            subprocess.run(['openssl', 'genpkey', '-algorithm', 'RSA', '-pkeyopt', 'rsa_keygen_bits:2048', '-out', str(key)], check=True, capture_output=True)
            der = subprocess.run(['openssl', 'pkey', '-in', str(key), '-pubout', '-outform', 'DER'], check=True, capture_output=True).stdout
            import base64
            Handler.public_key = base64.b64encode(der)
        cls.server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.base = 'http://127.0.0.1:%d' % cls.server.server_address[1]

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()
        cls.thread.join()
        cls.server.server_close()

    def setUp(self):
        Handler.value, Handler.audits = None, []
        Handler.broken_restore = Handler.corrupt_failure = False
        Handler.wrong_audit = None
        Handler.naive_timestamp = False
        Handler.event_value = 'I'
        Handler.identity_problem, Handler.account_reads = None, 0
        Handler.source_shape = False
        Handler.empty_login = False
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)

    def document(self, rejected=False):
        read = {'method': 'GET', 'path': '/state/queries', 'operation': 'read', 'expected_status': 200}
        audit = {'method': 'GET', 'path': '/audit/queries', 'operation': 'read', 'expected_status': 200}
        return {'cases': [{'id': 'write', 'method': 'POST', 'path': '/entity', 'operation': 'create',
            'family_fingerprint': 'fam-test', 'body': {'id': 'entity-7', 'reject': rejected},
            'expected_status': 422 if rejected else 201,
            'fixture': {'mode': 'api', 'restore_requests': [{'method': 'POST', 'path': '/restore'}]},
            'identity_request': {'method': 'GET', 'operation': 'read', 'path': '/core/module/sys/logins/account',
                                 'projection': {'adapter': 'sunway-account-v1', 'selectors': {'principal': 'id', 'account_organization': 'orgId', 'organization': 'ext$.loginorgid', 'roles': 'ext$.roles'}}},
            'audit_requests': {'before': audit, 'after': audit},
            'audit': {'expected': {'entity': 'entity-7', 'event': 'I', 'principal': 'manager', 'role': 'manager', 'organization': 'org-1'},
                      'entity_binding': {'source': 'request', 'path': 'body.id'},
                      'fields': {'entity': 'entity', 'event': 'event', 'principal': 'principal', 'role': 'role', 'organization': 'org', 'record_id': 'id', 'timestamp': 'at', 'lines': 'lines'},
                      'line_fields': {'line_id': 'id', 'field': 'field', 'old': 'old', 'new': 'new'},
                      'expected_records': 0 if rejected else 1, 'expected_lines': 1,
                      'changes': [{'field': 'name', 'old': None, 'new': 'created'}]},
            'state_proof': {'records_path': 'rows', 'entity_field': 'id', 'before_request': read, 'after_request': read, 'restored_request': read,
                            'expected': {'rows': []}, 'expected_after': {'rows': [] if rejected else [{'id': 'entity-7', 'name': 'created'}]}},
            'rollback': {'kind': 'rejected-write' if rejected else 'compensation', 'failure_status': 422, 'failure_values': {'code': 'too-long'}}}]}

    def run_document(self, document):
        artifact = self.root / 'app.jar'
        artifact.write_bytes(b'owned-source-only-test')
        metadata = {key: key + '-test' for key in ('application_binding', 'harness_binding', 'runtime_contract_binding', 'implementation_binding')}
        metadata.update({'source_dirty': False, 'implementation_dirty': False, 'source_commit': 'test'})
        contract = {'runtime_identity': 'test-runtime'}
        with mock.patch.object(runner, 'source_metadata', return_value=metadata), mock.patch.object(runner, 'validate_file', return_value=contract), mock.patch.object(runner, 'read_password_file', return_value='test-only'):
            return runner.run_cases(document, base_url=self.base, mode='strict', role='manager', organization='org-1',
                clone_identity='test-runtime', artifacts=[artifact], output=self.root / 'pending.json', timeout=2,
                runtime_report=self.root / 'runtime.json', defer_cleanup=True, auth_user='manager', password_file=self.root / 'test-password')

    def check_pending_proof(self, report):
        return validate_case_proof(report, report['cases'][0], require_registered=False)

    def test_empty_login_body_authenticates_only_through_current_account(self):
        Handler.empty_login = True
        report = self.run_document(self.document())
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))
        self.assertTrue(self.check_pending_proof(report)[0])
        auth = report['authentication']
        self.assertTrue(auth['account_validated'])
        self.assertEqual(auth['body_sha256'], 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855')
        self.assertEqual(auth['account_body_sha256'], auth['projection_metadata']['raw_body_sha256'])
        account = auth['account_observation']
        self.assertEqual(account['request']['path'], '/core/module/sys/logins/account')
        self.assertEqual(account['request']['auth'], auth['session_context'])
        self.assertEqual(auth['response_fingerprint'], account['response_fingerprint'])
        self.assertNotEqual(auth['login_response_fingerprint'], auth['response_fingerprint'])
        self.assertNotIn('body_base64', account)
        self.assertEqual(Handler.account_reads, 3)

    def test_server_identity_allows_roleless_audit_without_persisting_secrets(self):
        document = self.document()
        document['cases'][0]['audit']['fields'].pop('role')
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))
        self.assertTrue(self.check_pending_proof(report)[0])
        persisted = json.dumps(report) + Path(report['cases'][0]['http_write_proof']['path']).read_text()
        for secret in ('private-login-token', 'private-session-value', 'private-role-extra', 'private-account-extra', 'loginToken'):
            self.assertNotIn(secret, persisted)
        self.assertEqual(Handler.account_reads, 3)

    def test_http_200_login_without_account_identity_is_not_authenticated(self):
        Handler.identity_problem = 'fake-login'
        report = self.run_document(self.document())
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('valid authenticated identity', report['reason'])
        self.assertFalse(report['authentication']['authenticated'])
        self.assertEqual(Handler.account_reads, 1)
        self.assertIsNone(Handler.value)

    def test_empty_login_requires_account_identity_and_unchanged_nonempty_cookie(self):
        Handler.empty_login = True
        for problem in ('unauthorized', 'principal', 'organization', 'missing-role', 'changed-cookie', 'no-cookie', 'empty-cookie'):
            with self.subTest(problem=problem):
                Handler.identity_problem, Handler.account_reads = problem, 0
                report = self.run_document(self.document())
                self.assertEqual(report['status'], 'executed-fail', report)
                self.assertFalse(report['authentication']['authenticated'])
                self.assertIsNone(Handler.value)
                self.assertEqual(Handler.audits, [])

    def test_role_revocation_and_wrong_principal_or_organization_fail_closed_and_restore(self):
        for problem in ('missing-role', 'revoked', 'principal', 'organization', 'account-principal', 'account-org', 'assignment', 'missing-org', 'labels'):
            with self.subTest(problem=problem):
                Handler.identity_problem, Handler.account_reads = problem, 0
                Handler.value, Handler.audits = None, []
                report = self.run_document(self.document())
                self.assertEqual(report['status'], 'executed-fail', report)
                self.assertIsNone(Handler.value)
                self.assertTrue('identity' in report['reason'])
                if problem in ('account-principal', 'account-org'):
                    self.assertEqual(Handler.audits, [])

    def test_identity_projection_and_session_metadata_tampering_is_rejected(self):
        report = self.run_document(self.document())
        self.assertTrue(self.check_pending_proof(report)[0], report.get('reason'))
        ref = report['cases'][0]['http_write_proof']
        path = Path(ref['path'])
        original = path.read_bytes()
        mutations = {
            'projection_hash': lambda p: p['observations']['identity_after']['projection_metadata'].update(projection_sha256='0' * 64),
            'adapter': lambda p: p['observations']['identity_before']['projection_metadata'].update(adapter='untrusted'),
            'raw_hash': lambda p: p['observations']['identity_after']['projection_metadata'].update(raw_body_sha256='not-sha'),
            'missing_before': lambda p: p['observations'].pop('identity_before'),
            'missing_after': lambda p: p['observations'].pop('identity_after'),
            'role': lambda p: p['observations']['identity_after']['response_projection'].update(roles=[]),
            'privacy': lambda p: p['observations']['identity_after']['response_projection'].update(loginToken='must-reject'),
            'session': lambda p: p['observations']['identity_before']['request'].update(auth={'cookies': {'SESSION': '0' * 64}}),
        }
        for name, mutation in mutations.items():
            with self.subTest(name=name):
                payload = json.loads(original)
                mutation(payload)
                if name == 'session':
                    from evidence_fingerprint import request_fingerprint
                    item = payload['observations']['identity_before']
                    item['request_fingerprint'] = request_fingerprint(item['request'])
                raw = json.dumps(payload).encode()
                path.write_bytes(raw)
                ref['sha256'] = hashlib.sha256(raw).hexdigest()
                self.assertFalse(self.check_pending_proof(report)[0])
        path.write_bytes(original)
        ref['sha256'] = hashlib.sha256(original).hexdigest()
        report['authentication']['identity']['principal'] = 'other'
        self.assertFalse(self.check_pending_proof(report)[0])

    def test_identity_source_selectors_cannot_extract_private_account_fields(self):
        document = self.document()
        document['cases'][0]['identity_request']['projection']['selectors']['principal'] = 'loginToken'
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('allowlist adapter/selectors', report['reason'])
        self.assertIsNone(Handler.value)

    def test_source_shaped_update_numeric_capture_prefixed_audit_empty_put_and_projection(self):
        Handler.source_shape = True
        document = self.document()
        case = document['cases'][0]
        case.update(method='PUT', path='/seal-rule', operation='update', expected_status=200,
                    body={'id': '${fixture.created}', 'name': 'after'})
        case['fixture'] = {'mode': 'api',
            'setup_requests': [{'id': 'created', 'method': 'POST', 'path': '/source-setup', 'capture_json': True}],
            'restore_requests': [{'method': 'PUT', 'path': '/seal-rule', 'body': {'id': '${fixture.created}', 'name': 'before'}}]}
        case['audit'].update(entity_binding={'source': 'request', 'path': 'body.id',
            'format': {'prefix': 'T_EMC_SEAL_RULE$', 'convert': 'string'}},
            expected={'entity': 'T_EMC_SEAL_RULE$${fixture.created}', 'event': 'U', 'principal': 'manager', 'role': 'manager', 'organization': 'org-1'},
            fields={'entity': 'targetId', 'event': 'auditTrailType', 'principal': 'createdById', 'organization': 'createdByOrgId',
                    'record_id': 'id', 'timestamp': 'createdTime', 'lines': 'lines'},
            event_values={'I': 'INSERT', 'U': 'UPDATE', 'D': 'DELETE'},
            line_fields={'line_id': 'id', 'field': 'column', 'old': 'oldValue', 'new': 'newValue'},
            changes=[{'field': 'NAME', 'state_field': 'name', 'value_type': 'string', 'old': 'before', 'new': 'after'}])
        case['state_proof'].update(projection_fields=['id', 'name'],
            expected={'rows': [{'id': '${fixture.created}', 'name': 'before'}]},
            expected_after={'rows': [{'id': '${fixture.created}', 'name': 'after'}]})
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))
        self.assertTrue(self.check_pending_proof(report)[0])
        self.assertEqual(Handler.value, 'before')

    def test_version_one_write_reports_are_never_upgraded(self):
        report = self.run_document(self.document())
        report['http_write_proof_version'] = 1
        valid, reason = self.check_pending_proof(report)
        self.assertFalse(valid)
        self.assertIn('version missing or incompatible', reason)

    def test_positive_write_has_executed_audit_and_compensation_proof(self):
        report = self.run_document(self.document())
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))
        self.assertEqual(report['cases'][0]['rollback']['kind'], 'compensation')
        self.assertFalse(strict_case_contract(report)[0])
        valid, reason = self.check_pending_proof(report)
        self.assertTrue(valid, reason)

    def test_separate_record_and_line_http_queries_bind_parent_record(self):
        document = self.document()
        case = document['cases'][0]
        case['audit_requests']['lines'] = {'method': 'GET', 'path': '/lines/queries', 'operation': 'read'}
        case['audit']['line_records_path'] = 'rows'
        case['audit']['line_fields']['record_id'] = 'record'
        case['audit']['fields'].pop('lines')
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))
        self.assertTrue(self.check_pending_proof(report)[0])

    def test_private_transcript_and_public_report_do_not_publish_response_rows(self):
        report = self.run_document(self.document())
        case = report['cases'][0]
        proof = Path(case['http_write_proof']['path'])
        self.assertEqual(proof.stat().st_mode & 0o777, 0o600)
        self.assertNotIn('body_base64', json.dumps(report))
        self.assertNotIn('audit-11', json.dumps(report))
        self.assertNotIn('line-12', json.dumps(report))

    def test_sensitive_fixture_fields_are_refused_and_business_license_is_allowed(self):
        for key in ('Access_Token', 'loginToken', 'Password'):
            with self.subTest(key=key):
                Handler.value, Handler.audits = None, []
                document = self.document()
                document['cases'][0]['body']['nested'] = {key: 'sensitive-placeholder'}
                report = self.run_document(document)
                self.assertEqual(report['status'], 'executed-fail')
                self.assertIn('sensitive', report['reason'])
        Handler.value, Handler.audits = None, []
        document = self.document()
        document['cases'][0]['body']['business_license'] = 'fixture-entitlement'
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))

    def test_sensitive_header_and_query_values_are_refused_before_transcript_persistence(self):
        now = dt.datetime.now(dt.timezone.utc).isoformat()
        base = {'method': 'GET', 'path': '/state/queries', 'auth': {'cookies': {}}}
        for header in ('X-API-Key', 'X-Auth-Token', 'Proxy-Authorization', 'X-CSRF-Token'):
            with self.subTest(header=header), self.assertRaisesRegex(ValueError, 'sensitive'):
                transcript({**base, 'headers': {header: 'private-placeholder'}}, 200, {}, b'{}', now, now)
        for field in ('path', 'path_template'):
            with self.subTest(field=field), self.assertRaisesRegex(ValueError, 'sensitive'):
                transcript({**base, field: '/state/queries?access_token=private-placeholder'}, 200, {}, b'{}', now, now)

    def test_naive_database_seconds_require_explicit_timezone_and_precision(self):
        Handler.naive_timestamp = True
        report = self.run_document(self.document())
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('declared database timezone', report['reason'])
        Handler.value, Handler.audits = None, []
        document = self.document()
        document['cases'][0]['audit']['timestamp_policy'] = {'timezone': 'UTC', 'precision': 'seconds'}
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))

    def test_declared_source_event_enum_maps_to_semantic_operation(self):
        Handler.event_value = 'INSERT'
        document = self.document()
        document['cases'][0]['audit']['event_values'] = {'I': 'INSERT', 'U': 'UPDATE', 'D': 'DELETE'}
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))

    def test_negative_write_has_unchanged_state_and_no_success_audit(self):
        report = self.run_document(self.document(True))
        self.assertEqual(report['status'], 'executed-pass-pending-cleanup', report.get('reason'))
        self.assertEqual(report['cases'][0]['rollback']['kind'], 'rejected-write')
        self.assertTrue(self.check_pending_proof(report)[0])

    def test_restore_200_with_remaining_row_fails(self):
        Handler.broken_restore = True
        report = self.run_document(self.document())
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('state proof readback', report['reason'])

    def test_rejected_update_that_changed_state_fails(self):
        Handler.corrupt_failure = True
        report = self.run_document(self.document(True))
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('state proof readback', report['reason'])

    def test_wrong_audit_semantics_fail(self):
        for field in ('entity', 'event', 'principal', 'role', 'org'):
            with self.subTest(field=field):
                Handler.value, Handler.audits, Handler.wrong_audit = None, [], field
                report = self.run_document(self.document())
                self.assertEqual(report['status'], 'executed-fail')
                self.assertIn('audit proof', report['reason'])

    def test_old_new_or_count_mismatch_fails(self):
        for mutate in ('old', 'expected_lines'):
            with self.subTest(mutate=mutate):
                Handler.value, Handler.audits = None, []
                document = self.document()
                audit = document['cases'][0]['audit']
                if mutate == 'old': audit['changes'][0]['old'] = 'unexpected'
                else: audit['expected_lines'] = 2
                report = self.run_document(document)
                self.assertEqual(report['status'], 'executed-fail')
                self.assertIn('audit proof', report['reason'])

    def test_promotion_rechecks_artifact_context_fingerprints_and_time(self):
        report = self.run_document(self.document())
        self.assertTrue(self.check_pending_proof(report)[0], report.get('reason'))
        ref = report['cases'][0]['http_write_proof']
        path = Path(ref['path'])
        original = path.read_bytes()
        # Golden classification corpus: all mutations must remain invalid even
        # if a caller rewrites public status labels or the outer artifact hash.
        mutations = {
            'batch': lambda p: p['context'].update(batch_id='prior-batch'),
            'case': lambda p: p['context'].update(case_id='other-case'),
            'artifact': lambda p: p['context'].update(artifact_sha256={'x': '0' * 64}),
            'runtime': lambda p: p['context'].update(clone_identity='other-runtime'),
            'application': lambda p: p['context'].update(application_binding='other-app'),
            'time': lambda p: p['observations']['audit_after'].update(finished_at='2000-01-01T00:00:00+00:00'),
            'fingerprint': lambda p: p['observations']['audit_after'].update(response_fingerprint='res-fake'),
            'declaration': lambda p: p['observations'].pop('audit_after'),
            'restore_observation': lambda p: p['observations'].pop('restore_0'),
            'entity_binding': lambda p: p['audit']['expected'].update(entity='other-entity'),
            'old_new': lambda p: p['audit']['changes'][0].update(old='other-old'),
        }
        for name, mutate in mutations.items():
            with self.subTest(name=name):
                payload = json.loads(original)
                mutate(payload)
                raw = json.dumps(payload).encode()
                path.write_bytes(raw)
                ref['sha256'] = hashlib.sha256(raw).hexdigest()
                valid, reason = self.check_pending_proof(report)
                self.assertFalse(valid, reason)
        path.write_bytes(original)
        ref['sha256'] = '0' * 64
        self.assertFalse(self.check_pending_proof(report)[0])
        path.unlink()
        self.assertFalse(strict_case_contract(report)[0])

    def test_preexisting_audit_record_cannot_be_claimed_as_new(self):
        Handler.audits = [{'id': 'old-audit', 'entity': 'entity-7', 'event': 'I', 'principal': 'manager',
                          'role': 'manager', 'org': 'org-1', 'at': '2000-01-01T00:00:00+00:00', 'lines': []}]
        report = self.run_document(self.document())
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('before absence', report['reason'])

    def test_negative_write_needs_explicit_failure_semantics(self):
        document = self.document(True)
        document['cases'][0]['rollback'].pop('failure_values')
        report = self.run_document(document)
        self.assertEqual(report['status'], 'executed-fail')
        self.assertIn('failure status and semantics', report['reason'])

    def test_explicit_store_export_is_portable_and_can_satisfy_strict_case_gate(self):
        report = self.run_document(self.document())
        store = self.root / 'controlled-store'
        store.mkdir()
        with mock.patch.dict(runner.os.environ, {'SUNWAY_HTTP_PROOF_STORE': str(store)}):
            exported = register_pending_report(report)
            self.assertNotIn('path', exported['cases'][0]['http_write_proof'])
            Path(report['cases'][0]['http_write_proof']['path']).unlink()
            valid, reason = strict_case_contract(exported)
            self.assertTrue(valid, reason)
            self.assertEqual(exported['status'], 'executed-pass-pending-cleanup')
            ref = exported['cases'][0]['http_write_proof']
            object_path = store / 'sha256' / ref['sha256']
            object_path.write_bytes(b'tampered')
            self.assertFalse(strict_case_contract(exported)[0])

    def test_store_rejects_missing_registration_traversal_symlink_and_retention(self):
        report = self.run_document(self.document())
        store = self.root / 'controlled-store'
        store.mkdir()
        with mock.patch.dict(runner.os.environ, {'SUNWAY_HTTP_PROOF_STORE': str(store)}):
            exported = register_pending_report(report)
            ref = exported['cases'][0]['http_write_proof']
            registry = store / 'evidence-index.tsv'
            original = registry.read_text()
            registry.write_text(original.replace('permanent', '30d'))
            self.assertFalse(strict_case_contract(exported)[0])
            registry.write_text(original.splitlines()[0] + '\n')
            self.assertFalse(strict_case_contract(exported)[0])
            registry.write_text(original)
            ref['artifact_ref'] = '../../escape'
            self.assertFalse(strict_case_contract(exported)[0])
            ref['artifact_ref'] = 'sha256:' + ref['sha256']
            obj = store / 'sha256' / ref['sha256']
            other = self.root / 'outside'
            obj.rename(other)
            obj.symlink_to(other)
            self.assertFalse(strict_case_contract(exported)[0])

    def test_observation_rejects_read_or_create_and_missing_read_declaration(self):
        for path, operation in (('/property-tmpls/folders/0/property-data/queries', 'read'), ('/state/queries', 'write')):
            document = self.document()
            document['cases'][0]['state_proof']['before_request'] = {'method': 'GET', 'path': path, 'operation': operation}
            report = self.run_document(document)
            self.assertEqual(report['status'], 'executed-fail')
            self.assertIn('HTTP proof observation', report['reason'])

    def test_promotion_rejects_unverified_authentication(self):
        report = self.run_document(self.document())
        report['authentication']['authenticated'] = False
        self.assertFalse(self.check_pending_proof(report)[0])

    def test_controlled_storage_label_without_resolver_cannot_promote(self):
        report = self.run_document(self.document())
        report['cases'][0]['http_write_proof'].update(storage='artifact', artifact_ref='artifact://claimed')
        valid, reason = strict_case_contract(report)
        self.assertFalse(valid)
        self.assertIn('not registered', reason)

    def test_write_proof_cannot_reuse_read_only_golden_revalidation(self):
        report = self.run_document(self.document())
        report['harness_semantics_unchanged'] = True
        report['cases'][0]['validation_mode'] = 'revalidated-by-fingerprint'
        self.assertFalse(strict_case_contract(report)[0])

if __name__ == '__main__': unittest.main()
