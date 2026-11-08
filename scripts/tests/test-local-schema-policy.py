#!/usr/bin/env python3
"""Keep nine-table admission exact and preserve the existing schema policies."""
import copy
import hashlib
import json
from pathlib import Path
import shutil
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import local_schema_policy as policy
import schema_fixture

NEW_POLICY = 'approved-missing-runtime-and-email-tables-v2'
EMAIL_TABLES = {'T_CORE_EMAIL_CONFIG_RECEIPENT', 'T_CORE_EMAIL_CONFIG_REC_ROLE'}
FILE_POLICY = 'approved-runtime-core-file-v3'
FILE_BASE = 'api-fixtures/schema/core-file-restoration'


def column_rows(table, columns):
    rows = []
    for ordinal, col in enumerate(columns, 1):
        varchar = col['type'] == 'VARCHAR2'
        length = col['length'] if varchar else {'NUMBER': 22, 'DATE': 7, 'CLOB': 4000, 'TIMESTAMP(6)': 11}[col['type']]
        rows.append([table, col['name'], str(ordinal), col['type'], str(length),
                     str(col['precision']) if col['precision'] is not None else '-',
                     str(col['scale']) if col['scale'] is not None else '-', col['nullable'],
                     str(col['length']) if varchar else '0', 'B' if varchar else '-', '-'])
    return rows


def seal_snapshot(snapshot):
    for owner, schema in snapshot['schemas'].items():
        rows = schema['column_metadata']['rows']
        schema['column_metadata']['sha256'] = policy.digest(rows)
        snapshot['runtime_contract']['baseline_schema_hash' if owner == 'LIMS_BASELINE_LOCAL'
                                     else 'runtime_schema_hash_before'] = policy.digest(rows)
    from source_binding import runtime_contract_digest
    runtime = snapshot['runtime_contract']
    digest = runtime_contract_digest(runtime)
    snapshot['source_binding'] = {'runtime_contract': runtime, 'runtime_contract_digest': digest,
                                  'runtime_contract_binding': 'rt-' + digest[:16]}


def file_snapshot(selected=FILE_POLICY):
    manifest = json.loads((ROOT / (FILE_BASE + '.json')).read_text())
    descriptor, predicates = policy.definition(selected)
    loaded = schema_fixture.load_fixture(ROOT)
    loaded.update(policy.email_fixture(ROOT)[0])
    loaded['T_CORE_FILE'] = (manifest['target_definition'] if selected == FILE_POLICY
                            else {'columns': manifest['historical_columns']})
    shared = column_rows('ORIGINAL', [{'name': 'ID', 'type': 'NUMBER', 'length': None,
                                      'precision': 10, 'scale': 0, 'nullable': 'N'}])
    baseline = copy.deepcopy(shared) + column_rows('T_CORE_FILE', manifest['historical_columns'])
    runtime = copy.deepcopy(shared) + [row for table, definition in sorted(loaded.items())
                                       for row in column_rows(table, definition['columns'])]
    records = [{**p, 'observed_count': p['expected_count']} for p in predicates]
    supplied = {**descriptor, 'sha256': policy.digest(descriptor),
                'metadata_proof': {'record_count': len(records), 'sha256': policy.digest(records), 'rows': records}}
    query_rows = [[str(i), str(p['expected_count'])] for i, p in enumerate(predicates)]
    snapshot = {'schema_policy': supplied,
                'runtime_contract': {'schema_policy_id': selected,
                                     'schema_policy_sha256': policy.digest(descriptor)},
                'schemas': {owner: {'column_metadata': {'rows': sorted(rows)},
                                    'row_counts': {'rows': [{'table': t, 'count': 0} for t in sorted({r[0] for r in rows})]}}
                            for owner, rows in [('LIMS_BASELINE_LOCAL', baseline), (schema_fixture.OWNER, runtime)]},
                'queries': [{'name': 'approved-schema-metadata',
                             'sql_sha256': hashlib.sha256(policy.query_sql(policy.proof_selects(predicates)).encode()).hexdigest(),
                             'expected_records': len(query_rows), 'parsed_records': len(query_rows),
                             'parsed_rows_sha256': policy.digest(query_rows), 'coverage_complete': True}]}
    seal_snapshot(snapshot)
    return snapshot


class LocalSchemaPolicyTest(unittest.TestCase):
    def test_file_policy_accepts_only_pinned_baseline10_runtime21_and_nine_added_tables(self):
        snapshot = file_snapshot()
        descriptor = policy.validate(snapshot, FILE_POLICY)
        self.assertEqual(descriptor['id'], FILE_POLICY)
        self.assertEqual(descriptor['table_count'], 9)
        self.assertEqual(descriptor['altered_tables'], ['T_CORE_FILE'])
        self.assertEqual(len([r for r in snapshot['schemas'][schema_fixture.OWNER]['column_metadata']['rows']
                              if r[0] == 'T_CORE_FILE']), 21)
        self.assertEqual(policy.digest(policy.definition(NEW_POLICY)[0]),
                         '65084d7a081a8087e6f2ad4dd9a36eeed33014932540586e4d76445586ffdffd')
        for selected in (policy.EQUAL, policy.APPROVED, NEW_POLICY):
            with self.subTest(policy=selected), self.assertRaises(policy.PolicyError):
                policy.validate(snapshot, selected)

    def test_old_v2_still_accepts_its_exact_contract_and_rejects_restored_core_file(self):
        snapshot = file_snapshot(NEW_POLICY)
        policy.validate(snapshot, NEW_POLICY)
        manifest = json.loads((ROOT / (FILE_BASE + '.json')).read_text())
        metadata = snapshot['schemas'][schema_fixture.OWNER]['column_metadata']
        metadata['rows'] = [r for r in metadata['rows'] if r[0] != 'T_CORE_FILE']
        metadata['rows'].extend(column_rows('T_CORE_FILE', manifest['restored_columns']))
        metadata['rows'].sort()
        seal_snapshot(snapshot)
        with self.assertRaisesRegex(policy.PolicyError, 'shared column metadata mismatch'):
            policy.validate(snapshot, NEW_POLICY)

    def test_file_policy_rejects_pinned_manifest_fixture_and_original_source_drift(self):
        with tempfile.TemporaryDirectory() as temporary:
            copied = Path(temporary)
            manifest = json.loads((ROOT / (FILE_BASE + '.json')).read_text())
            paths = {schema_fixture.BASE + '.json', schema_fixture.BASE + '.sql',
                     *schema_fixture.SOURCE_PATHS, 'scripts/schema_fixture.py',
                     policy.EMAIL_BASE + '.json', policy.EMAIL_BASE + '.sql',
                     FILE_BASE + '.json', FILE_BASE + '.sql',
                     *[s['path'] for s in manifest['sources']]}
            for name in paths:
                target = copied / name
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / name, target)
            policy.definition(FILE_POLICY, copied)
            for name in (FILE_BASE + '.json', FILE_BASE + '.sql',
                         *[s['path'] for s in manifest['sources']]):
                with self.subTest(path=name):
                    target = copied / name
                    raw = target.read_bytes()
                    target.write_bytes(raw + b'\n-- source drift\n')
                    with self.assertRaises(policy.PolicyError):
                        policy.definition(FILE_POLICY, copied)
                    target.write_bytes(raw)

    def test_file_policy_rejects_target_column_drift_and_baseline_change(self):
        original = file_snapshot()
        for selected in ('type', 'length', 'nullable', 'order', 'baseline', 'unrelated-shared'):
            snapshot = copy.deepcopy(original)
            owner = 'LIMS_BASELINE_LOCAL' if selected == 'baseline' else schema_fixture.OWNER
            rows = snapshot['schemas'][owner]['column_metadata']['rows']
            row = next(r for r in rows if r[0] == ('ORIGINAL' if selected == 'unrelated-shared' else 'T_CORE_FILE')
                       and r[1] == ('ID' if selected == 'unrelated-shared' else 'TARGETID'))
            if selected == 'type': row[3] = 'CHAR'
            elif selected == 'nullable': row[7] = 'N'
            elif selected == 'order': row[2] = '3'
            else: row[4] = row[8] = '59'
            seal_snapshot(snapshot)
            with self.subTest(drift=selected), self.assertRaises(policy.PolicyError):
                policy.validate(snapshot, FILE_POLICY)

    def test_file_policy_rejects_default_pk_index_comments_fk_and_trigger_proof_drift(self):
        original = file_snapshot()
        selectors = {
            'default': lambda r: r['view'] == 'ALL_TAB_COLS' and 'DEFAULT_LENGTH IS NULL' in r['predicate'],
            'pk': lambda r: r['view'] == 'ALL_CONSTRAINTS' and "CONSTRAINT_TYPE='P'" in r['predicate'],
            'index': lambda r: r['view'] == 'ALL_INDEXES',
            'comments': lambda r: r['view'] == 'ALL_COL_COMMENTS',
            'outgoing-fk': lambda r: r['view'] == 'ALL_CONSTRAINTS' and r['predicate'].endswith("TABLE_NAME='T_CORE_FILE'"),
            'incoming-fk': lambda r: r['view'] == 'ALL_CONSTRAINTS' and "CONSTRAINT_TYPE='R'" in r['predicate'],
            'trigger': lambda r: r['view'] == 'ALL_TRIGGERS',
        }
        for selected, matches in selectors.items():
            snapshot = copy.deepcopy(original)
            proof = snapshot['schema_policy']['metadata_proof']
            row = next(r for r in proof['rows'] if r['table'] == 'T_CORE_FILE' and matches(r))
            row['observed_count'] += 1
            proof['sha256'] = policy.digest(proof['rows'])
            with self.subTest(drift=selected), self.assertRaises(policy.PolicyError):
                policy.validate(snapshot, FILE_POLICY)

    def test_new_policy_pins_nine_tables_and_both_original_email_definitions(self):
        descriptor, predicates = policy.definition(NEW_POLICY)
        self.assertEqual(descriptor['table_count'], 9)
        self.assertEqual(descriptor['column_count'], 125)
        self.assertEqual(set(descriptor['definition_sha256']), schema_fixture.TABLES | EMAIL_TABLES)
        self.assertEqual(len(predicates), 389)
        self.assertEqual(descriptor['additional_fixtures'][0]['fixture_sha256'],
                         '73fab487b8612cc4ba9d0cfe254f7db1816b83b0debab63e71344edfa7d8fbb6')
        self.assertEqual(schema_fixture.TABLES, frozenset(descriptor['definition_sha256']) - EMAIL_TABLES)

    def test_existing_policy_descriptor_and_default_rejection_remain_unchanged(self):
        descriptor, _ = policy.definition(policy.APPROVED)
        self.assertEqual(policy.digest(descriptor),
                         '48233a6db2976b6a2120b1013543e609fa6519a73711f4f74acb5f240b2b3137')
        baseline = {'ORIGINAL'}
        seven = baseline | schema_fixture.TABLES
        nine = seven | EMAIL_TABLES
        policy.check_inventories(baseline, baseline)
        policy.check_inventories(baseline, seven, policy.APPROVED)
        for selected in (policy.EQUAL, policy.APPROVED):
            with self.subTest(policy=selected), self.assertRaises(policy.PolicyError):
                policy.check_inventories(baseline, nine, selected)
        policy.check_inventories(baseline, nine, NEW_POLICY)
        for invalid in (seven, nine | {'UNKNOWN_EXTRA'}, nine - {'ORIGINAL'},
                        nine - {'T_CORE_EMAIL_CONFIG_RECEIPENT'}):
            with self.subTest(tables=invalid), self.assertRaises(policy.PolicyError):
                policy.check_inventories(baseline, invalid, NEW_POLICY)
        with self.assertRaises(policy.PolicyError):
            policy.check_inventories(baseline | EMAIL_TABLES, nine, NEW_POLICY)

    def test_new_policy_rejects_source_fixture_manifest_and_renderer_drift(self):
        with tempfile.TemporaryDirectory() as temporary:
            copied = Path(temporary)
            manifest = json.loads((ROOT / 'api-fixtures/schema/email-config-associations.json').read_text())
            paths = [schema_fixture.BASE + '.json', schema_fixture.BASE + '.sql',
                     *schema_fixture.SOURCE_PATHS, 'scripts/schema_fixture.py',
                     'api-fixtures/schema/email-config-associations.json',
                     'api-fixtures/schema/email-config-associations.sql']
            for name in paths:
                target = copied / name
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / name, target)
            policy.definition(NEW_POLICY, copied)
            for name in (manifest['source']['path'], manifest['fixture']['path'],
                         'api-fixtures/schema/email-config-associations.json', 'scripts/schema_fixture.py'):
                with self.subTest(path=name):
                    target = copied / name
                    raw = target.read_bytes()
                    target.write_bytes(raw + b'\n# changed\n')
                    with self.assertRaises(policy.PolicyError):
                        policy.definition(NEW_POLICY, copied)
                    target.write_bytes(raw)


if __name__ == '__main__':
    unittest.main()
