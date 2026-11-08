#!/usr/bin/env python3
"""Admit only the two already-restored original LES tables in a new policy."""
import copy
import hashlib
import importlib.util
import json
from pathlib import Path
import sys
import unittest

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import local_schema_policy as policy

spec = importlib.util.spec_from_file_location('existing_schema_policy_tests',
    Path(__file__).with_name('test-local-schema-policy.py'))
existing = importlib.util.module_from_spec(spec)
spec.loader.exec_module(existing)
LES_POLICY = 'approved-runtime-les-data-source-v4'


def snapshot():
    result = existing.file_snapshot()
    loaded, _ = policy.les_fixture(ROOT)
    schema = result['schemas']['LIMS_RUNTIME_LOCAL']
    for table, definition in sorted(loaded.items()):
        schema['column_metadata']['rows'].extend(existing.column_rows(table, definition['columns']))
        schema['row_counts']['rows'].append({'table': table, 'count': 0})
    schema['column_metadata']['rows'].sort()
    schema['row_counts']['rows'].sort(key=lambda row: row['table'])
    descriptor, predicates = policy.definition(LES_POLICY)
    records = [{**p, 'observed_count': p['expected_count']} for p in predicates]
    result['schema_policy'] = {**descriptor, 'sha256': policy.digest(descriptor),
        'metadata_proof': {'record_count': len(records), 'sha256': policy.digest(records), 'rows': records}}
    rows = [[str(i), str(p['expected_count'])] for i, p in enumerate(predicates)]
    result['queries'] = [{'name': 'approved-schema-metadata',
        'sql_sha256': hashlib.sha256(policy.query_sql(policy.proof_selects(predicates)).encode()).hexdigest(),
        'expected_records': len(rows), 'parsed_records': len(rows),
        'parsed_rows_sha256': policy.digest(rows), 'coverage_complete': True}]
    result['runtime_contract'].update(schema_policy_id=LES_POLICY,
                                      schema_policy_sha256=policy.digest(descriptor))
    existing.seal_snapshot(result)
    return result


class LesRuntimePolicyTest(unittest.TestCase):
    def test_exact_two_table_contract_and_existing_policy_unchanged(self):
        result = snapshot()
        descriptor = policy.validate(result, LES_POLICY)
        self.assertEqual((descriptor['table_count'], descriptor['column_count']), (11, 152))
        self.assertEqual(len([r for r in result['schemas']['LIMS_RUNTIME_LOCAL']['column_metadata']['rows']
                              if r[0].startswith('T_CORE_LES_DS')]), 27)
        self.assertEqual(policy.digest(policy.definition(policy.APPROVED_FILE)[0]),
                         'aefa8e10afc4bb814090a529ce535d2eb06eb93f596d3746aac52f8c12980e58')
        for old in (policy.EQUAL, policy.APPROVED, policy.APPROVED_EMAIL, policy.APPROVED_FILE):
            with self.subTest(policy=old), self.assertRaises(policy.PolicyError):
                policy.validate(result, old)

    def test_no_missing_extra_or_baseline_les_table(self):
        for mutation in ('missing', 'extra', 'baseline'):
            result = snapshot()
            owner = 'LIMS_BASELINE_LOCAL' if mutation == 'baseline' else 'LIMS_RUNTIME_LOCAL'
            rows = result['schemas'][owner]['row_counts']['rows']
            if mutation == 'missing':
                rows[:] = [r for r in rows if r['table'] != 'T_CORE_LES_DS_FIELD']
            else:
                rows.append({'table': 'T_CORE_LES_DS' if mutation == 'baseline' else 'UNAPPROVED', 'count': 0})
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.validate(result, LES_POLICY)

    def test_column_and_unrelated_baseline_drift_stay_rejected(self):
        for table, column in [('T_CORE_LES_DS', 'LESDSNAME'), ('T_CORE_LES_DS_FIELD', 'ORDERNO'),
                              ('ORIGINAL', 'ID')]:
            result = snapshot()
            rows = result['schemas']['LIMS_RUNTIME_LOCAL']['column_metadata']['rows']
            row = next(r for r in rows if r[0] == table and r[1] == column)
            row[7] = 'N' if row[7] == 'Y' else 'Y'
            existing.seal_snapshot(result)
            with self.subTest(table=table), self.assertRaises(policy.PolicyError):
                policy.validate(result, LES_POLICY)

    def test_les_metadata_and_query_coverage_tampering_rejected(self):
        for mutation in ('predicate', 'coverage'):
            result = snapshot()
            if mutation == 'predicate':
                proof = result['schema_policy']['metadata_proof']
                next(r for r in proof['rows'] if r['table'] == 'T_CORE_LES_DS')['observed_count'] += 1
                proof['sha256'] = policy.digest(proof['rows'])
            else:
                result['queries'][0]['parsed_records'] -= 1
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.validate(result, LES_POLICY)

    def test_les_manifest_fixture_or_source_drift_rejected(self):
        import tempfile
        import shutil
        with tempfile.TemporaryDirectory() as temporary:
            copied = Path(temporary)
            manifest = json.loads((ROOT / 'api-fixtures/schema/les-data-source-restoration.json').read_text())
            paths = ['api-fixtures/schema/les-data-source-restoration.json',
                     manifest['fixture']['path'], 'scripts/schema_fixture.py',
                     *[s['path'] for s in manifest['sources']]]
            for name in paths:
                target = copied / name
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / name, target)
            policy.les_fixture(copied)
            for name in paths:
                target = copied / name
                raw = target.read_bytes()
                target.write_bytes(raw + b'\n')
                with self.subTest(path=name), self.assertRaises((policy.PolicyError, ValueError)):
                    policy.les_fixture(copied)
                target.write_bytes(raw)


if __name__ == '__main__':
    unittest.main()
