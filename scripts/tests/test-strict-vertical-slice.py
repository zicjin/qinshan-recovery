#!/usr/bin/env python3
"""Public evidence-verifier contract using a real disposable Git checkout."""
import copy
from dataclasses import replace
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'tools'))
sys.path.insert(0, str(ROOT / 'scripts'))
from vertical_slices.matrix import MatrixCase, RunnerRegistration, VERIFIER_PATHS, validate_evidence
from source_binding import capture_binding


class StrictEvidenceTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix='strict-matrix-contract-')
        self.repo = Path(self.tmp.name)
        self.root = self.repo / 'lims-recovery'
        (self.root / 'module/src').mkdir(parents=True)
        (self.root / 'evidence').mkdir()
        self.source = self.root / 'module/src/example.txt'
        self.source.write_text('known source\n')
        for path in VERIFIER_PATHS:
            target = self.root / path
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(b'known verifier\n')
        self.git('init', '-q')
        self.git('add', '.')
        self.commit()
        self.binding = capture_binding(self.repo)
        self.case = MatrixCase('report', 'GET /report', 'manager', 'org-a',
                               'isolated', 'synthetic', 'allowed', 'report-export',
                               'oracle-container-and-network', 'evidence/report.json')
        self.registry = {'report': RunnerRegistration('report', 'module/src/example.txt',
                                                       ('module/src',), 'strict')}
        self.payload = {
            'status': 'executed-pass', 'production_access': False,
            'source_commit': self.git('rev-parse', 'HEAD'), 'source_dirty': False,
            'repository_head_at_capture': self.git('rev-parse', 'HEAD'),
            'implementation_binding': self.binding['binding_id'],
            'implementation_tree_digest': self.binding['implementation_tree_digest'],
            'implementation_dirty': self.binding['implementation_dirty'],
            'implementation_scope': self.binding['implementation_scope'],
            'runner': 'module/src/example.txt',
            'role': 'manager', 'organization': 'org-a', 'mode': 'isolated', 'fixture': 'synthetic',
            'verifier_sha256': {path: hashlib.sha256(b'known verifier\n').hexdigest() for path in VERIFIER_PATHS},
            'source_coverage': {'roots': ['module/src'], 'files': {
                'module/src/example.txt': hashlib.sha256(b'known source\n').hexdigest()},
                'diff': {'added': [], 'deleted': []}},
            'http_cases': [{'route': 'GET /report', 'role': 'manager', 'organization': 'org-a',
                            'mode': 'isolated', 'status': 200, 'expected_status': 200}],
            'audit': {'events': [{'action': 'report-export', 'actorId': 'e-1',
                                  'resource': '7001', 'outcome': 'ALLOWED'}],
                      'expected': [{'action': 'report-export', 'actorId': 'e-1',
                                    'resource': '7001', 'outcome': 'ALLOWED'}]},
            'cleanup': {key: True for key in ('browser_session_closed', 'temporary_files_removed',
                                              'container_removed', 'anonymous_volumes_removed', 'network_removed')},
        }

    def tearDown(self):
        self.tmp.cleanup()

    def git(self, *arguments):
        return subprocess.check_output(['git', *arguments], cwd=self.repo, text=True,
                                       stderr=subprocess.DEVNULL).strip()

    def commit(self):
        self.git('-c', 'user.name=Contract', '-c', 'user.email=contract@example.invalid',
                 'commit', '-qm', 'fixture')

    def verify(self, payload=None, case=None):
        (self.root / 'evidence/report.json').write_text(json.dumps(self.payload if payload is None else payload))
        return validate_evidence([case or self.case], self.root, self.registry, self.repo)

    def test_unchanged_nested_source_remains_valid_after_evidence_only_commit(self):
        self.assertEqual(self.verify()['pass'], 1)
        (self.repo / 'note.md').write_text('unrelated evidence note')
        self.git('add', 'note.md')
        self.commit()
        payload = copy.deepcopy(self.payload)
        self.assertEqual(self.verify(payload)['pass'], 1)

    def test_source_scope_exception_cannot_hide_runner_source_change(self):
        self.assertEqual(self.verify()['pass'], 1)
        self.source.write_text('changed source\n')
        self.git('add', 'lims-recovery/module/src/example.txt')
        self.commit()
        payload = copy.deepcopy(self.payload)
        payload['source_commit_exception'] = {
            'reason': 'later commit was claimed to be outside the runner source scope'
        }
        with self.assertRaises(ValueError):
            self.verify(payload)

    def test_wrong_organization_or_result_is_rejected(self):
        for key, value in [('organization', 'org-b'), ('role', 'other'), ('mode', 'default'),
                           ('status', 500), ('expected_status', 201)]:
            with self.subTest(key=key):
                payload = copy.deepcopy(self.payload)
                payload['http_cases'][0][key] = value
                with self.assertRaises(ValueError):
                    self.verify(payload)

    def test_fixture_metadata_and_declared_audit_must_match(self):
        for key, value in [('fixture', 'unrelated'), ('role', 'other'),
                           ('organization', 'org-b'), ('mode', 'default')]:
            with self.subTest(key=key):
                payload = copy.deepcopy(self.payload)
                payload[key] = value
                with self.assertRaises(ValueError):
                    self.verify(payload)
        payload = copy.deepcopy(self.payload)
        payload['audit']['expected'] = [{'action': 'report-export', 'actorId': 'e-2',
                                          'resource': '7001', 'outcome': 'ALLOWED'}]
        with self.assertRaises(ValueError):
            self.verify(payload)

    def test_missing_provenance_audit_cleanup_or_http_is_rejected(self):
        for key in ('source_commit', 'source_dirty', 'verifier_sha256', 'source_coverage', 'audit', 'cleanup', 'http_cases'):
            with self.subTest(key=key):
                payload = copy.deepcopy(self.payload)
                payload.pop(key)
                with self.assertRaises(ValueError):
                    self.verify(payload)
        for mutation in ('dirty', 'no-audit-event', 'empty-audit-expectation', 'empty-source', 'empty-verifier', 'bad-verifier', 'bad-hash', 'cleanup-failed'):
            with self.subTest(mutation=mutation):
                payload = copy.deepcopy(self.payload)
                if mutation == 'dirty': payload['source_dirty'] = True
                if mutation == 'no-audit-event': payload['audit']['events'] = []
                if mutation == 'empty-audit-expectation': payload['audit']['expected'] = []
                if mutation == 'empty-verifier': payload['verifier_sha256'] = {}
                if mutation == 'bad-verifier': payload['verifier_sha256'][VERIFIER_PATHS[0]] = '0' * 64
                if mutation == 'empty-source': payload['source_coverage']['files'] = {}
                if mutation == 'bad-hash': payload['source_coverage']['files']['module/src/example.txt'] = '0' * 64
                if mutation == 'cleanup-failed': payload['cleanup']['network_removed'] = False
                with self.assertRaises(ValueError):
                    self.verify(payload)

    def test_untracked_source_file_cannot_hide_behind_clean_report_flag(self):
        (self.root / 'module/src/added.txt').write_text('new behavior')
        with self.assertRaises(ValueError):
            self.verify()

    def test_current_source_edits_additions_and_deletions_require_replay(self):
        self.source.write_text('changed behavior')
        with self.assertRaises(ValueError):
            self.verify()
        self.git('add', '.')
        self.commit()
        with self.assertRaises(ValueError):
            self.verify()
        self.source.write_text('known source\n')
        (self.root / 'module/src/added.txt').write_text('added behavior')
        self.git('add', '.')
        self.commit()
        with self.assertRaises(ValueError):
            self.verify()
        self.source.unlink()
        self.git('add', '.')
        self.commit()
        with self.assertRaises(ValueError):
            self.verify()

    def test_custom_evidence_path_verifies_that_file(self):
        # An absolute output path selects the new report, not the matrix's old report.
        custom = self.root / 'evidence/custom.json'
        custom.write_text(json.dumps(self.payload))
        self.assertEqual(validate_evidence([replace(self.case, evidence=str(custom))],
                                          self.root, self.registry)['pass'], 1)

    def test_forbidden_contract_requires_denied_audit(self):
        case = replace(self.case, expected_result='forbidden', audit='role.write')
        payload = copy.deepcopy(self.payload)
        payload['http_cases'] = [{'route': 'GET /report', 'role': 'manager',
                                  'organization': 'org-a', 'mode': 'isolated',
                                  'status': 403, 'expected_status': 403}]
        payload['audit'] = {
            'events': [{'action': 'role.write', 'actorId': 'e-1',
                        'resource': 'role.write', 'outcome': 'DENIED'}],
            'expected': [{'action': 'role.write', 'actorId': 'e-1',
                          'resource': 'role.write', 'outcome': 'DENIED'}],
        }
        self.assertEqual(self.verify(payload, case)['pass'], 1)
        payload['audit']['events'][0]['outcome'] = 'ALLOWED'
        with self.assertRaises(ValueError):
            self.verify(payload, case)

    def test_batch_case_audit_expectation_overrides_shared_top_level_expectation(self):
        case = replace(self.case, case_id='batch-forbidden', expected_result='forbidden', audit='role.write')
        payload = copy.deepcopy(self.payload)
        payload['http_cases'] = [{'route': 'GET /report', 'role': 'manager',
                                  'organization': 'org-a', 'mode': 'isolated',
                                  'status': 403, 'expected_status': 403}]
        payload['audit'] = {
            'events': [{'action': 'role.write', 'actorId': 'e-1',
                        'resource': 'role.write', 'outcome': 'DENIED'}],
            # A shared report may carry an expectation for another case.
            'expected': [{'action': 'other-case', 'actorId': 'e-1',
                          'resource': 'other-case', 'outcome': 'ALLOWED'}],
        }
        payload['batch_case_results'] = [{
            'case_id': 'batch-forbidden', 'role': 'manager', 'organization': 'org-a',
            'mode': 'isolated', 'fixture': 'synthetic', 'fixture_restore': True,
            'audit_observed': True,
            'audit_expected': [{'action': 'role.write', 'actorId': 'e-1',
                                'resource': 'role.write', 'outcome': 'DENIED'}],
        }]
        registry = {'batch-forbidden': self.registry['report']}
        (self.root / 'evidence/report.json').write_text(json.dumps(payload))
        self.assertEqual(validate_evidence([case], self.root, registry, self.repo)['pass'], 1)
        payload['batch_case_results'][0]['audit_expected'][0]['outcome'] = 'ALLOWED'
        (self.root / 'evidence/report.json').write_text(json.dumps(payload))
        with self.assertRaises(ValueError):
            validate_evidence([case], self.root, registry, self.repo)


if __name__ == '__main__':
    unittest.main()
