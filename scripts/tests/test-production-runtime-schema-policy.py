#!/usr/bin/env python3
"""Reject unrelated schema drift while admitting only the approved v5 delta.

These are offline acquisition/validation fixtures. They never connect to Oracle
or run DDL, DML, application, browser or shared-target builds.
"""
import copy
import hashlib
import importlib.util
import json
from pathlib import Path
import re
import shutil
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import local_schema_policy as policy

V5 = 'approved-runtime-production-alignment-v5'
MANIFEST = 'api-fixtures/schema/approved-production-alignment-v5.json'
spec = importlib.util.spec_from_file_location('v4_fixture_tests', Path(__file__).with_name('test-les-runtime-schema-policy.py'))
previous = importlib.util.module_from_spec(spec)
spec.loader.exec_module(previous)

ADDED = 'T_EMC_EQUIPT_ACQ_FILE'
RUNTIME = 'LIMS_RUNTIME_LOCAL'
BASELINE = 'LIMS_BASELINE_LOCAL'
SOURCE = 'api-fixtures/schema/production-ddl-source-20261109.json'
BEFORE_WIDTHS = {
    ('T_CORE_AT_RECORD_LINE', 'COLUMN'): 60,
    ('T_CORE_AT_RECORD_LINE', 'CREATEDBYNAME'): 60,
    ('T_CORE_AT_RECORD_LINE', 'CREATEDBYID'): 36,
    ('T_CORE_AT_RECORD_LINE', 'CREATEDBYORGID'): 36,
    ('T_CORE_AT_RECORD_LINE', 'CREATEDBYORGNAME'): 270,
    ('T_CORE_AT_RECORD_LINE', 'TARGETID'): 129,
    ('T_CORE_BPMN_COMMENT', 'COMMENT'): 300,
    ('T_EMC_AUDIT_RECORD', 'COMMENTS'): 600,
    ('T_EMC_FOLDER', 'MONITORPOINTNAME'): 300,
}


def metadata_row(column):
    """Observer format from pinned production metadata, independent of v5 code."""
    names = ['TABLE_NAME', 'COLUMN_NAME', 'COLUMN_ID', 'DATA_TYPE', 'DATA_LENGTH',
             'DATA_PRECISION', 'DATA_SCALE', 'NULLABLE', 'CHAR_LENGTH', 'CHAR_USED', 'DEFAULT_LENGTH']
    return [str(column[key]) if column.get(key) is not None else '-' for key in names]


def old_snapshot():
    """Complete representative shared tables with their pre-approved definitions.

    The original source subset supplies unrelated columns; only the declared
    approved migrations are reversed here. No live database metadata is read.
    """
    result = previous.snapshot()
    reviewed = json.loads((ROOT / SOURCE).read_text())
    shared = {table for table, _ in BEFORE_WIDTHS}
    rows = [metadata_row(column) for column in reviewed['columns'] if column['TABLE_NAME'] in shared]
    for row in rows:
        before = BEFORE_WIDTHS.get((row[0], row[1]))
        if before is not None:
            row[4] = row[8] = str(before)
        if row[0] == 'T_CORE_AT_RECORD_LINE' and row[1] in ('ID', 'RECORDID'):
            row[5] = row[6] = '-'
    report = json.loads((ROOT / 'api-fixtures/schema/report-config-category-restoration.json').read_text())
    for column in report['existing_columns']:
        varchar = column['type'] == 'VARCHAR2'
        rows.append(['T_CORE_REPORT_CONFIG', column['name'], str(column['position']), column['type'],
                     str(column['data_length']), '-', '-', column['nullable'],
                     str(column['data_length']) if varchar else '0', 'B' if varchar else '-', '-'])
    for owner in [BASELINE, RUNTIME]:
        schema = result['schemas'][owner]
        schema['column_metadata']['rows'].extend(copy.deepcopy(rows))
        schema['column_metadata']['rows'].sort()
        schema['row_counts']['rows'].extend({'table': table, 'count': 0} for table in sorted(shared | {'T_CORE_REPORT_CONFIG'}))
        schema['row_counts']['rows'].sort(key=lambda row: row['table'])
    previous.existing.seal_snapshot(result)
    return result


def install_proof(result, selected):
    descriptor, predicates = policy.definition(selected)
    # The observer's finite SQL-count result transcript. Physical mutation tests
    # reseal column hashes so a rejection cannot merely be a stale digest.
    query_rows = [[str(i), str(p['expected_count'])] for i, p in enumerate(predicates)]
    proof = [{**p, 'observed_count': p['expected_count']} for p in predicates]
    result['schema_policy'] = {**descriptor, 'sha256': policy.digest(descriptor),
        'metadata_proof': {'record_count': len(proof), 'sha256': policy.digest(proof), 'rows': proof}}
    result['queries'] = [{'name': 'approved-schema-metadata',
        'sql_sha256': hashlib.sha256(policy.query_sql(policy.proof_selects(predicates)).encode()).hexdigest(),
        'expected_records': len(query_rows), 'parsed_records': len(query_rows),
        'parsed_rows_sha256': policy.digest(query_rows), 'coverage_complete': True}]
    result['runtime_contract'].update(schema_policy_id=selected, schema_policy_sha256=policy.digest(descriptor))
    previous.existing.seal_snapshot(result)
    return result


def snapshot():
    """Apply the authorized SQL effects without calling production_fixture()."""
    result = old_snapshot()
    runtime = result['schemas'][RUNTIME]
    rows = runtime['column_metadata']['rows']
    reviewed = json.loads((ROOT / SOURCE).read_text())
    for row in rows:
        if (row[0], row[1]) in BEFORE_WIDTHS:
            target = next(column for column in reviewed['columns'] if column['TABLE_NAME'] == row[0] and column['COLUMN_NAME'] == row[1])
            row[4] = row[8] = target['DATA_LENGTH']
    file_widths = {'TARGETID': 64, 'NAME': 3000, 'MD5NAME': 32, 'BIZCATEGORY': 32,
                   'EXT': 128, 'CREATEDBYID': 32, 'CREATEDBYNAME': 64, 'CREATEDBYORGID': 32}
    file_rows = sorted((row for row in rows if row[0] == 'T_CORE_FILE'), key=lambda row: int(row[2]))
    remark = next(row for row in file_rows if row[1] == 'REMARK')
    file_rows.remove(remark)
    for row in file_rows:
        if row[1] in file_widths:
            row[4] = row[8] = str(file_widths[row[1]])
        if row[1] in ('TIMES', 'SIZE', 'VERSION'):
            row[5] = row[6] = '-'
    remark[3:7] = ['CLOB', '4000', '-', '-']
    remark[8:10] = ['0', '-']
    file_rows.append(remark)
    for position, row in enumerate(file_rows, 1):
        row[2] = str(position)
    rows.extend(metadata_row(column) for column in reviewed['columns'] if column['TABLE_NAME'] == ADDED)
    rows.append(['T_CORE_REPORT_CONFIG', 'CATEGORY', '8', 'VARCHAR2', '36', '-', '-', 'Y', '36', 'B', '-'])
    rows.sort()
    runtime['row_counts']['rows'].append({'table': ADDED, 'count': 0})
    runtime['row_counts']['rows'].sort(key=lambda row: row['table'])
    return install_proof(result, V5)


def row_in(result, owner, table, column):
    return next(row for row in result['schemas'][owner]['column_metadata']['rows'] if row[:2] == [table, column])


def copy_pinned_inputs(root):
    """Copy only actual pinned source inputs into an isolated fixture checkout."""
    names = {'scripts/schema_fixture.py', *previous.existing.schema_fixture.SOURCE_PATHS}
    for directory in [ROOT / 'api-fixtures/schema']:
        for source in directory.glob('*'):
            if source.is_file():
                names.add(str(source.relative_to(ROOT)))
                if source.suffix == '.json':
                    def collect(value):
                        if isinstance(value, dict):
                            for item in value.values():
                                collect(item)
                        elif isinstance(value, list):
                            for item in value:
                                collect(item)
                        elif (isinstance(value, str)
                              and value.startswith(('api-fixtures/schema/', 'project/', 'reconstituted/', 'scripts/'))
                              and (ROOT / value).is_file()):
                            # Provenance can name private capture receipts that
                            # happen to exist in a developer checkout. They are
                            # not installation inputs and must not be discovered
                            # or copied into this source-only fixture.
                            names.add(value)
                    collect(json.loads(source.read_text()))
    for name in names:
        target = root / name
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(ROOT / name, target)
    return names


class ProductionRuntimePolicyTest(unittest.TestCase):
    def test_exact_opt_in_v5_does_not_relabel_the_old_v4_contract(self):
        descriptor, predicates = policy.definition(V5)
        self.assertEqual(descriptor['id'], V5)
        self.assertEqual(descriptor['table_count'], 12)
        self.assertIn('T_EMC_EQUIPT_ACQ_FILE', descriptor['definition_sha256'])
        self.assertTrue(predicates)
        old = previous.snapshot()
        policy.validate(old, previous.LES_POLICY)
        self.assertNotEqual(policy.digest(descriptor), policy.digest(policy.definition(previous.LES_POLICY)[0]))

    def test_current_finite_v5_passes_but_old_v4_still_rejects(self):
        current = snapshot()
        policy.validate(current, V5)
        legacy = old_snapshot()
        policy.validate(legacy, previous.LES_POLICY)
        for selected in (policy.EQUAL, policy.APPROVED, policy.APPROVED_EMAIL, policy.APPROVED_FILE, previous.LES_POLICY):
            with self.subTest(policy=selected), self.assertRaises(policy.PolicyError):
                policy.validate(install_proof(copy.deepcopy(current), selected), selected)
        with self.assertRaises(policy.PolicyError):
            policy.validate(install_proof(legacy, V5), V5)

    def test_all_old_descriptor_hashes_are_unchanged(self):
        expected = {
            policy.EQUAL: '8566153dd4a3bb2aec3410ee2592a5b218e651b5ef62e30b727c5b7e403fc75a',
            policy.APPROVED: '48233a6db2976b6a2120b1013543e609fa6519a73711f4f74acb5f240b2b3137',
            policy.APPROVED_EMAIL: '65084d7a081a8087e6f2ad4dd9a36eeed33014932540586e4d76445586ffdffd',
            policy.APPROVED_FILE: 'aefa8e10afc4bb814090a529ce535d2eb06eb93f596d3746aac52f8c12980e58',
            previous.LES_POLICY: 'c6af89101645d95c767bdd68bc331700f34b2b2eee1ee8b38a28ba8f95f0e92a',
        }
        for selected, expected_hash in expected.items():
            with self.subTest(policy=selected):
                self.assertEqual(policy.digest(policy.definition(selected)[0]), expected_hash)

    def test_extra_missing_or_baseline_added_tables_are_not_admitted(self):
        for mutation in ('extra', 'missing-old', 'missing-new', 'baseline-added', 'missing-shared'):
            result = snapshot()
            owner = BASELINE if mutation == 'baseline-added' else RUNTIME
            inventory = result['schemas'][owner]['row_counts']['rows']
            if mutation.startswith('missing'):
                table = {'missing-old': 'T_CORE_LES_DS', 'missing-new': ADDED, 'missing-shared': 'ORIGINAL'}[mutation]
                inventory[:] = [row for row in inventory if row['table'] != table]
            else:
                inventory.append({'table': ADDED if mutation == 'baseline-added' else 'UNAPPROVED', 'count': 0})
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.validate(result, V5)

    def test_column_type_length_precision_ordinal_default_and_null_are_exact(self):
        for table, column in [(ADDED, 'SOURCEFILENAME'), ('T_CORE_FILE', 'NAME'),
                              ('T_CORE_AT_RECORD_LINE', 'TARGETID'), ('T_CORE_REPORT_CONFIG', 'CATEGORY')]:
            for index, value in [(2, '99'), (3, 'CHAR'), (4, '42'), (5, '12'), (6, '3'), (7, 'N'), (8, '41'), (9, 'C'), (10, '1')]:
                result = snapshot()
                row_in(result, RUNTIME, table, column)[index] = value
                previous.existing.seal_snapshot(result)
                with self.subTest(table=table, column=column, position=index), self.assertRaises(policy.PolicyError):
                    policy.validate(result, V5)
        for mutation in ('missing', 'extra'):
            result = snapshot()
            rows = result['schemas'][RUNTIME]['column_metadata']['rows']
            if mutation == 'missing':
                rows[:] = [row for row in rows if row[:2] != [ADDED, 'CONTENT']]
            else:
                rows.append([ADDED, 'UNAPPROVED', '10', 'NUMBER', '22', '-', '-', 'Y', '0', '-', '-'])
            previous.existing.seal_snapshot(result)
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.validate(result, V5)
        for column, index, value in [('ID', 4, '8'), ('ID', 8, '1'), ('ID', 9, 'B'),
                                     ('CONTENT', 4, '42'), ('CREATEDTIME', 4, '8')]:
            result = snapshot()
            row_in(result, RUNTIME, ADDED, column)[index] = value
            previous.existing.seal_snapshot(result)
            with self.subTest(column=column, position=index), self.assertRaises(policy.PolicyError):
                policy.validate(result, V5)

    def test_baseline_and_unrelated_shared_drift_do_not_become_approved(self):
        for owner, table, column in [(BASELINE, 'T_CORE_AT_RECORD_LINE', 'TARGETID'),
                                     (BASELINE, 'T_CORE_FILE', 'TARGETID'),
                                     (RUNTIME, 'ORIGINAL', 'ID'),
                                     (RUNTIME, 'T_CORE_AT_RECORD_LINE', 'OLDVALUE')]:
            result = snapshot()
            changed = row_in(result, owner, table, column)
            changed[7] = 'N' if changed[7] == 'Y' else 'Y'
            previous.existing.seal_snapshot(result)
            with self.subTest(owner=owner, table=table), self.assertRaises(policy.PolicyError):
                policy.validate(result, V5)

    def test_acquisition_and_query_coverage_cannot_be_forged_by_rehashing(self):
        for mutation in ('missing', 'duplicate', 'sql', 'parsed', 'expected', 'coverage', 'rows-hash', 'bool-count'):
            result = snapshot()
            query = result['queries'][0]
            if mutation == 'missing': result['queries'] = []
            elif mutation == 'duplicate': result['queries'].append(copy.deepcopy(query))
            elif mutation == 'sql': query['sql_sha256'] = '0' * 64
            elif mutation == 'parsed': query['parsed_records'] -= 1
            elif mutation == 'expected': query['expected_records'] -= 1
            elif mutation == 'coverage': query['coverage_complete'] = False
            elif mutation == 'rows-hash': query['parsed_rows_sha256'] = '0' * 64
            else:
                proof = result['schema_policy']['metadata_proof']
                row = next(row for row in proof['rows'] if row['observed_count'] == 1)
                row['observed_count'] = True
                proof['sha256'] = policy.digest(proof['rows'])
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.validate(result, V5)

    def test_real_observer_transcript_rejects_missing_extra_reordered_and_wrong_counts(self):
        _, predicates = policy.definition(V5)
        expected = [[str(i), str(row['expected_count'])] for i, row in enumerate(predicates)]
        calls = []
        def acquire(name, sql, tag, width):
            calls.append((name, sql, tag, width))
            return copy.deepcopy(expected)
        observed = policy.observe(V5, acquire)
        self.assertEqual(observed['metadata_proof']['record_count'], len(expected))
        self.assertEqual([(call[0], call[2], call[3]) for call in calls], [('approved-schema-metadata', 'FIXTURE', 2)])
        for mutation in ('missing', 'extra', 'reordered', 'wrong-count'):
            rows = copy.deepcopy(expected)
            if mutation == 'missing': rows.pop()
            elif mutation == 'extra': rows.append([str(len(rows)), '0'])
            elif mutation == 'reordered': rows[0], rows[1] = rows[1], rows[0]
            else: rows[0][1] = str(int(rows[0][1]) + 1)
            with self.subTest(mutation=mutation), self.assertRaises(policy.PolicyError):
                policy.observe(V5, lambda *args: rows)

    def test_observed_pk_index_order_and_function_body_mismatches_are_rejected(self):
        selectors = {
            'acquisition-pk': lambda row: row['table'] == ADDED and row['view'] == 'ALL_CONSTRAINTS' and "CONSTRAINT_TYPE='P'" in row['predicate'],
            'acquisition-pk-column': lambda row: row['table'] == ADDED and row['view'] == 'ALL_CONS_COLUMNS' and "COLUMN_NAME='ID'" in row['predicate'],
            'acquisition-unexpected-constraint': lambda row: row['table'] == ADDED and row['view'] == 'ALL_CONSTRAINTS' and 'CONSTRAINT_TYPE' not in row['predicate'],
            'acquisition-default': lambda row: row['table'] == ADDED and row['view'] == 'ALL_TAB_COLS' and "COLUMN_NAME='ID'" in row['predicate'] and 'DEFAULT_LENGTH IS NULL' in row['predicate'],
            'index-name-status': lambda row: row['view'] == 'ALL_INDEXES' and "INDEX_NAME='IDX_CORE_FILE_TARGET_SCOPE_ID'" in row['predicate'],
            'index-order': lambda row: row['view'] == 'ALL_IND_COLUMNS' and "INDEX_NAME='IDX_CORE_FILE_TARGET_BIZ_ID'" in row['predicate'] and "COLUMN_NAME='BIZCATEGORY'" in row['predicate'] and 'COLUMN_POSITION=2' in row['predicate'],
            'index-extra-column': lambda row: row['view'] == 'ALL_IND_COLUMNS' and row['predicate'] == "INDEX_OWNER='LIMS_RUNTIME_LOCAL' AND INDEX_NAME='IDX_CORE_FILE_TARGET_ID'",
            'function-invalid': lambda row: row['table'] == 'STRINGTOSCIENCE' and row['view'] == 'ALL_OBJECTS',
            'function-body': lambda row: row['table'] == 'STRINGTOSCIENCE' and row['view'] == 'ALL_SOURCE' and ' AND LINE=5 ' in row['predicate'],
            'function-extra-source-line': lambda row: row['table'] == 'STRINGTOSCIENCE' and row['view'] == 'ALL_SOURCE' and ' AND LINE=' not in row['predicate'],
        }
        original = snapshot()
        for name, select in selectors.items():
            result = copy.deepcopy(original)
            proof = result['schema_policy']['metadata_proof']
            selected = [row for row in proof['rows'] if select(row)]
            self.assertTrue(selected, 'Required acquisition contract was not generated: ' + name)
            selected[0]['observed_count'] += 1
            proof['sha256'] = policy.digest(proof['rows'])
            with self.subTest(mismatch=name), self.assertRaises(policy.PolicyError):
                policy.validate(result, V5)

    def test_function_transcript_pins_exact_reviewed_body_and_index_column_order(self):
        _, predicates = policy.definition(V5)
        observed = [row for row in predicates if row['table'] == 'STRINGTOSCIENCE' and row['view'] == 'ALL_SOURCE' and ' AND LINE=' in row['predicate']]
        self.assertEqual(len(observed), 44)
        sql = (ROOT / 'api-fixtures/schema/production-ddl-field-alignment-20261109.sql').read_text()
        function = sql[sql.index('CREATE OR REPLACE EDITIONABLE FUNCTION'):sql.index('\n/\nEXIT;', sql.index('CREATE OR REPLACE EDITIONABLE FUNCTION'))]
        function = function.replace('CREATE OR REPLACE EDITIONABLE ', '', 1).replace('"LIMS_RUNTIME_LOCAL".', '', 1)
        expected = [line.rstrip().encode('utf-8').hex().upper() for line in function.splitlines()]
        self.assertEqual(len(expected), 44)
        for number, hexadecimal in enumerate(expected, 1):
            row = next(row for row in observed if ' AND LINE=' + str(number) + ' AND ' in row['predicate'])
            self.assertEqual(row['expected_count'], 1)
            self.assertIn("='" + hexadecimal + "'" if hexadecimal else ' IS NULL', row['predicate'])
        index_rows = [row for row in predicates if row['view'] == 'ALL_IND_COLUMNS' and "INDEX_NAME='IDX_CORE_FILE_TARGET_BIZ_ID'" in row['predicate'] and 'COLUMN_POSITION=' in row['predicate']]
        self.assertEqual(len(index_rows), 3)
        for position, column in enumerate(['TARGETID', 'BIZCATEGORY', 'ID'], 1):
            self.assertTrue(any("COLUMN_NAME='" + column + "' AND COLUMN_POSITION=" + str(position) + " AND DESCEND='ASC'" in row['predicate'] for row in index_rows))

    def test_source_manifest_sql_function_and_pins_are_immutable_inputs(self):
        pinned = [MANIFEST, SOURCE,
                  'api-fixtures/schema/production-ddl-alignment-20261109.json',
                  'api-fixtures/schema/production-ddl-alignment-20261109.sql',
                  'api-fixtures/schema/production-ddl-field-alignment-20261109.json',
                  'api-fixtures/schema/production-ddl-field-alignment-20261109.sql',
                  'api-fixtures/schema/report-config-category-restoration.json',
                  'api-fixtures/schema/report-config-category-restoration.sql']
        with tempfile.TemporaryDirectory(prefix='schema-v5-source-') as temporary:
            copied = Path(temporary)
            copy_pinned_inputs(copied)
            policy.definition(V5, copied)
            for name in pinned:
                target = copied / name
                original = target.read_bytes()
                target.write_bytes(original + b'\n-- unapproved source mutation\n')
                with self.subTest(path=name), self.assertRaises(policy.PolicyError):
                    policy.definition(V5, copied)
                target.write_bytes(original)


if __name__ == '__main__':
    unittest.main()
