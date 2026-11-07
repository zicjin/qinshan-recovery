#!/usr/bin/env python3
"""Public renderer CLI checks; synthetic IDs only, no database connections."""
import copy
import hashlib
import json
from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
CLI = ROOT / 'scripts/audit_fixture_cleanup.py'
SHA = hashlib.sha256(b'fixture').hexdigest()


def example():
    common = {'createdById': 'fixture-user', 'createdByOrgId': 'fixture-org',
              'createdTime': '2026-11-07 01:02:03'}
    return {'version': 1, 'owner': 'LIMS_RUNTIME_LOCAL', 'table': 'T_EMC_SEAL_RULE',
            'targetId': 901, 'marker': 'owned-strict-fixture',
            'principal': 'fixture-user', 'organization': 'fixture-org',
            'startedAt': '2026-11-07 01:00:00', 'finishedAt': '2026-11-07 01:03:00',
            'baseline': {'recordIds': [1], 'lineIds': [2], 'configLineIds': [3]},
            'records': [dict(common, id=101, targetId='T_EMC_SEAL_RULE$901',
                             auditTrailType='INSERT', auditTrailName='create', commentHash=None)],
            'lines': [dict(common, id=102, recordId=101, tableName='T_EMC_SEAL_RULE',
                           targetId='T_EMC_SEAL_RULE$901', recordType='INSERT', column='NAME',
                           oldValueHash=None, newValueHash=hashlib.sha256(b'owned-strict-fixture').hexdigest())],
            'configLines': [dict(common, id=103, configId='T_EMC_SEAL_RULE',
                                 columnId='NAME', auditTrail='1')],
            'apiRestore': {'path': 'http.json', 'sha256': '', 'businessRemoved': True,
                           'configParentRemoved': True}}


class CleanupCliTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.directory = Path(self.tmp.name)
        self.journal = example()
        def observation(method, path, body, request_body=None, status=200):
            raw = json.dumps(body, ensure_ascii=False)
            return {'method': method, 'path': path, 'status': status,
                    'requestBody': request_body, 'rawBody': raw, 'bodySha256': hashlib.sha256(raw.encode()).hexdigest()}
        transcript = {'version': 1, 'baseUrl': 'http://127.0.0.1:18089',
                      'principal': 'fixture-user', 'organization': 'fixture-org',
                      'marker': 'owned-strict-fixture',
                      'authentication': {'sessionCookieSha256': SHA,
                                         'identity': {'id': 'fixture-user', 'organizationId': 'fixture-org'}},
                      'requests': [
                          observation('GET', '/secure/emc/module/mdm/basemdm/seal-rules/901',
                                      {'id': 901, 'name': 'owned-strict-fixture'}),
                          observation('DELETE', '/secure/emc/module/mdm/basemdm/seal-rules', None, {'b': ['', [{'id': 901}]], 'p': {}}),
                          observation('POST', '/secure/emc/module/mdm/basemdm/seal-rules/queries', {'total': 0, 'rows': []}, {'p': {'n': 1, 's': 10, 'f': {'id_SEQ': 901}}}),
                          observation('DELETE', '/secure/core/module/sys/at-configs', None, {'b': ['', [{'id': 'T_EMC_SEAL_RULE'}]], 'p': {}})]}
        data = json.dumps(transcript).encode()
        (self.directory / 'http.json').write_bytes(data)
        self.journal['apiRestore']['sha256'] = hashlib.sha256(data).hexdigest()

    def test_restore_transcript_requires_pinned_actual_json_and_batch_read(self):
        proof_path = self.directory / 'http.json'
        original = json.loads(proof_path.read_text())
        for mutation in ('login-page', 'not-empty', 'wrong-marker', 'wrong-identity', 'hash-only'):
            with self.subTest(mutation=mutation):
                proof = copy.deepcopy(original)
                row = proof['requests'][2]
                if mutation == 'login-page':
                    row['rawBody'] = '<html>Login</html>'
                elif mutation == 'not-empty':
                    row['rawBody'] = '{"total":1,"rows":[{"id":901}]}'
                elif mutation == 'wrong-marker':
                    proof['requests'][0]['rawBody'] = '{"id":901,"name":"other"}'
                elif mutation == 'wrong-identity':
                    proof['authentication']['identity']['id'] = 'other'
                else:
                    del row['rawBody']
                for request in proof['requests']:
                    if 'rawBody' in request:
                        request['bodySha256'] = hashlib.sha256(request['rawBody'].encode()).hexdigest()
                data = json.dumps(proof).encode()
                proof_path.write_bytes(data)
                self.journal['apiRestore']['sha256'] = hashlib.sha256(data).hexdigest()
                result = self.run_cli()
                self.assertNotEqual(result.returncode, 0)
                self.assertFalse((self.directory / 'cleanup.sql').exists())

    def test_collection_delete_requires_exact_single_business_and_config_ids(self):
        proof_path = self.directory / 'http.json'
        original = json.loads(proof_path.read_text())
        for mutation in ('wrong-business-id', 'wrong-config-id', 'extra-business-id',
                         'legacy-item-path', 'missing-request-body', 'unfiltered-query', 'get-body'):
            with self.subTest(mutation=mutation):
                proof = copy.deepcopy(original)
                rows = proof['requests']
                if mutation == 'wrong-business-id':
                    rows[1]['requestBody']['b'][1][0]['id'] = 999
                elif mutation == 'wrong-config-id':
                    rows[3]['requestBody']['b'][1][0]['id'] = 'T_CORE_USER'
                elif mutation == 'extra-business-id':
                    rows[1]['requestBody']['b'][1].append({'id': 999})
                elif mutation == 'legacy-item-path':
                    rows[1]['path'] += '/901'
                elif mutation == 'missing-request-body':
                    del rows[1]['requestBody']
                elif mutation == 'unfiltered-query':
                    rows[2]['requestBody']['p']['f'] = {}
                else:
                    rows[0]['requestBody'] = {}
                data = json.dumps(proof).encode()
                proof_path.write_bytes(data)
                self.journal['apiRestore']['sha256'] = hashlib.sha256(data).hexdigest()
                result = self.run_cli()
                self.assertNotEqual(result.returncode, 0)
                self.assertFalse((self.directory / 'cleanup.sql').exists())

    def test_refuses_unsafe_or_incomplete_private_journals_without_output(self):
        mutations = {
            'historical-record': lambda j: j['baseline']['recordIds'].append(101),
            'historical-line': lambda j: j['baseline']['lineIds'].append(102),
            'historical-config': lambda j: j['baseline']['configLineIds'].append(103),
            'duplicate-new-line': lambda j: j['lines'].append(copy.deepcopy(j['lines'][0])),
            'wrong-parent': lambda j: j['lines'][0].update(recordId=999),
            'other-target': lambda j: j['lines'][0].update(targetId='T_EMC_SEAL_RULE$999'),
            'other-table': lambda j: j.update(table='T_CORE_USER'),
            'other-owner': lambda j: j.update(owner='LIMS_BASELINE_LOCAL'),
            'other-org': lambda j: j['records'][0].update(createdByOrgId='other'),
            'boolean-version': lambda j: j.update(version=True),
            'missing-config': lambda j: j.update(configLines=[]),
            'old-timestamp': lambda j: j['records'][0].update(createdTime='2026-11-06 01:00:00'),
            'non-numeric-id': lambda j: j['records'][0].update(id='101 OR 1=1'),
            'boolean-id': lambda j: j['records'][0].update(id=True),
            'injected-audit-name': lambda j: j['records'][0].update(auditTrailName="create'; DELETE FROM T_CORE_USER;--"),
            'marker-prefix': lambda j: j.update(marker='ordinary-business'),
            'marker-hash': lambda j: j['lines'][0].update(newValueHash=SHA),
            'missing-proof': lambda j: j['apiRestore'].update(businessRemoved=False),
            'proof-traversal': lambda j: j['apiRestore'].update(path='../http.json'),
            'proof-hash': lambda j: j['apiRestore'].update(sha256=SHA),
            'extra-field': lambda j: j.update(deleteAll=True),
        }
        for label, mutate in mutations.items():
            with self.subTest(label=label):
                j = copy.deepcopy(self.journal)
                mutate(j)
                result = self.run_cli(j)
                self.assertNotEqual(result.returncode, 0)
                self.assertIn('cleanup refused:', result.stderr)
                self.assertFalse((self.directory / 'cleanup.sql').exists())

    def test_missing_baseline_snapshot_is_not_inferred_from_candidate_ids(self):
        del self.journal['baseline']['recordIds']
        self.assertNotEqual(self.run_cli().returncode, 0)
        self.assertFalse((self.directory / 'cleanup.sql').exists())

    def test_duplicate_json_keys_are_rejected_instead_of_last_value_winning(self):
        raw = json.dumps(self.journal)
        raw = raw.replace('"owner": "LIMS_RUNTIME_LOCAL"',
                          '"owner": "LIMS_BASELINE_LOCAL", "owner": "LIMS_RUNTIME_LOCAL"')
        (self.directory / 'journal.json').write_text(raw)
        result = subprocess.run(['python3', str(CLI), '--journal', str(self.directory / 'journal.json'),
                                 '--output', str(self.directory / 'cleanup.sql')], capture_output=True, text=True)
        self.assertNotEqual(result.returncode, 0)
        self.assertFalse((self.directory / 'cleanup.sql').exists())

    def test_never_overwrites_existing_cleanup_output(self):
        output = self.directory / 'cleanup.sql'
        output.write_text('preserve review copy')
        result = self.run_cli()
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(output.read_text(), 'preserve review copy')

    def test_parent_links_and_config_scope_have_independent_count_guards(self):
        result = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stderr)
        sql = (self.directory / 'cleanup.sql').read_text()
        before_delete = sql[:sql.index('DELETE FROM')]
        self.assertIn('WHERE RECORDID=101', before_delete)
        self.assertIn("WHERE CONFIGID='T_EMC_SEAL_RULE'", before_delete)
        self.assertIn("WHERE TARGETID='T_EMC_SEAL_RULE$901'", before_delete)
        # Business absence and parent absence must also be checked after mutation.
        after_delete = sql[sql.rindex('DELETE FROM'):]
        self.assertIn('FROM LIMS_RUNTIME_LOCAL.T_EMC_SEAL_RULE;', after_delete)
        self.assertIn("T_CORE_AT_CONFIG WHERE ID='T_EMC_SEAL_RULE'", after_delete)

    def run_cli(self, journal=None):
        (self.directory / 'journal.json').write_text(json.dumps(journal or self.journal))
        return subprocess.run(['python3', str(CLI), '--journal', str(self.directory / 'journal.json'),
                               '--output', str(self.directory / 'cleanup.sql')], capture_output=True, text=True)

    def test_exact_plan_preflights_before_three_narrow_delete_groups(self):
        result = self.run_cli()
        self.assertEqual(result.returncode, 0, result.stderr)
        sql = (self.directory / 'cleanup.sql').read_text()
        self.assertIn('SET DEFINE OFF', sql)
        self.assertIn('DBMS_CRYPTO.HASH', sql)
        self.assertIn('FOR UPDATE NOWAIT', sql)
        self.assertEqual(sql.count('COMMIT;'), 1)
        self.assertIn('WHEN OTHERS THEN', sql)
        self.assertIn('ROLLBACK;', sql)
        self.assertLess(sql.index('END preflight;'), sql.index('DELETE FROM'))
        self.assertNotIn('LOCK TABLE', sql)
        self.assertNotRegex(sql, r'(?im)^\s*(INSERT INTO|UPDATE |DELETE FROM LIMS_RUNTIME_LOCAL.T_EMC_)')
        self.assertIn('WHERE ID=102', sql)
        self.assertIn('WHERE ID=101', sql)
        self.assertIn('WHERE ID=103', sql)
        self.assertNotRegex(sql, r'DELETE FROM [^;]+ WHERE ID=(?:1|2|3) AND')


if __name__ == '__main__':
    unittest.main()
