#!/usr/bin/env python3
"""Schema fixture boundary tests; no database or runtime processes required."""
import copy
import hashlib
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

MODULE = Path(__file__).resolve().parents[1] / 'schema_fixture.py'
spec = importlib.util.spec_from_file_location('schema_fixture', MODULE)
fixture = importlib.util.module_from_spec(spec)
spec.loader.exec_module(fixture)


class SchemaFixtureTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.ddl = 'CREATE TABLE T_EMC_INVOICE (ID NUMBER NOT NULL, NAME VARCHAR2(30));'
        source = self.root / 'project/sql/oracle/oracle_sql_emc.txt'
        source.parent.mkdir(parents=True)
        source.write_text(self.ddl + '\n', encoding='utf-8')
        target = self.root / 'api-fixtures/schema'
        target.mkdir(parents=True)
        (target / 'missing-runtime-tables.sql').write_text(self.ddl + '\n', encoding='utf-8')
        sha = lambda value: hashlib.sha256(value).hexdigest()
        self.manifest = {
            'version': 1, 'owner': 'LIMS_RUNTIME_LOCAL',
            'sources': [{'path': 'project/sql/oracle/oracle_sql_emc.txt',
                         'sha256': sha(source.read_bytes())}],
            'fixture_sha256': sha((self.ddl + '\n').encode()),
            'statements': [{'source': 'project/sql/oracle/oracle_sql_emc.txt',
                            'line_start': 1, 'line_end': 1,
                            'source_sha256': sha(self.ddl.encode()),
                            'sha256': sha(self.ddl.encode()),
                            'transformations': []}],
        }
        self.save_manifest()

    def tearDown(self):
        self.temp.cleanup()

    def save_manifest(self):
        (self.root / 'api-fixtures/schema/missing-runtime-tables.json').write_text(
            json.dumps(self.manifest), encoding='utf-8')

    def test_verified_missing_table_plan_and_render_are_schema_only(self):
        loaded = fixture.load_fixture(self.root)
        plan = fixture.plan(loaded, 'LIMS_RUNTIME_LOCAL', {'T_EMC_INVOICE': None})
        self.assertEqual(plan['create'], ['T_EMC_INVOICE'])
        self.assertEqual(plan['skip'], [])
        sql = fixture.render(loaded, 'LIMS_RUNTIME_LOCAL')
        self.assertIn("SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')", sql)
        self.assertIn('LIMS_RUNTIME_LOCAL', sql)
        self.assertIn('WHENEVER SQLERROR EXIT FAILURE ROLLBACK', sql)
        self.assertLess(sql.index('ALL_TAB_COLUMNS'), sql.index('EXECUTE IMMEDIATE'))
        self.assertNotRegex(sql, r'(?i)\b(?:INSERT|UPDATE|DELETE|MERGE|DROP|TRUNCATE)\b')

    def test_wrong_owner_is_rejected_before_plan_or_render(self):
        loaded = fixture.load_fixture(self.root)
        for owner in ('SYS', 'LIMS_BASELINE_LOCAL', 'LIMS_RUNTIME_SSS2', 'lims_runtime_local'):
            with self.subTest(owner=owner):
                with self.assertRaises(fixture.FixtureError):
                    fixture.plan(loaded, owner)
                with self.assertRaises(fixture.FixtureError):
                    fixture.render(loaded, owner)

    def test_equivalent_existing_table_is_skipped_without_mutation(self):
        loaded = fixture.load_fixture(self.root)
        expected = {
            'columns': [
                {'name': 'ID', 'type': 'NUMBER', 'length': None, 'precision': None,
                 'scale': None, 'nullable': 'N'},
                {'name': 'NAME', 'type': 'VARCHAR2', 'length': 30, 'precision': None,
                 'scale': None, 'nullable': 'Y'},
            ], 'primary_key': None, 'indexes': [], 'comments': {},
        }
        metadata = {'T_EMC_INVOICE': expected}
        unchanged = copy.deepcopy(metadata)
        for _ in range(2):
            result = fixture.plan(loaded, 'LIMS_RUNTIME_LOCAL', metadata)
            self.assertEqual(result['create'], [])
            self.assertEqual(result['skip'], ['T_EMC_INVOICE'])
        self.assertEqual(metadata, unchanged)
        for actual in ({}, {'columns': []}, {**expected, 'unknown': True}):
            with self.subTest(actual=actual):
                with self.assertRaises(fixture.FixtureError):
                    fixture.plan(loaded, 'LIMS_RUNTIME_LOCAL', {'T_EMC_INVOICE': actual})
        with self.assertRaises(fixture.FixtureError):
            fixture.plan(loaded, 'LIMS_RUNTIME_LOCAL', {})

    def test_whole_source_hash_binds_unselected_lines(self):
        source = self.root / 'project/sql/oracle/oracle_sql_emc.txt'
        source.write_text(self.ddl + '\n-- changed unrelated source\n')
        with self.assertRaisesRegex(fixture.FixtureError, 'source file hash'):
            fixture.load_fixture(self.root)

    def test_rehashed_fixture_cannot_add_unbound_statement(self):
        target = self.root / 'api-fixtures/schema/missing-runtime-tables.sql'
        target.write_text(self.ddl + '\nALTER TABLE T_EMC_INVOICE ADD EXTRA NUMBER;\n')
        self.manifest['fixture_sha256'] = hashlib.sha256(target.read_bytes()).hexdigest()
        self.save_manifest()
        with self.assertRaisesRegex(fixture.FixtureError, 'differs from verified'):
            fixture.load_fixture(self.root)

    def test_bound_business_dml_still_cannot_be_rendered(self):
        ddl = 'INSERT INTO T_EMC_INVOICE (ID) VALUES (1);'
        source = self.root / 'project/sql/oracle/oracle_sql_emc.txt'
        source.write_text(ddl + '\n')
        target = self.root / 'api-fixtures/schema/missing-runtime-tables.sql'
        target.write_text(ddl + '\n')
        self.manifest['sources'][0]['sha256'] = hashlib.sha256(source.read_bytes()).hexdigest()
        self.manifest['fixture_sha256'] = hashlib.sha256(target.read_bytes()).hexdigest()
        self.manifest['statements'][0]['source_sha256'] = hashlib.sha256(ddl.encode()).hexdigest()
        self.manifest['statements'][0]['sha256'] = hashlib.sha256(ddl.encode()).hexdigest()
        self.save_manifest()
        with self.assertRaisesRegex(fixture.FixtureError, 'only approved'):
            fixture.load_fixture(self.root)

    def test_actual_fixture_is_complete_and_contains_no_guessed_columns(self):
        loaded = fixture.load_fixture(Path(__file__).resolve().parents[2])
        self.assertEqual(set(loaded), {
            'T_EMC_INVOICE', 'T_EMC_RETURNED', 'T_EMC_QC_ITEM', 'T_EMC_SEAL_RULE',
            'T_EMC_SEAL_RULE_DETAILS', 'T_CORE_EMAIL_CONFIG', 'T_CORE_EMAIL_TEMPLATE'})
        invoice = loaded['T_EMC_INVOICE']
        self.assertEqual(invoice['columns'][-1]['name'], 'INVOICEPROJNAME')
        self.assertNotIn('COPYFLAG', [c['name'] for c in invoice['columns']])
        self.assertEqual(sum(len(d['statements']) for d in loaded.values()), 128)
        sql = fixture.render(loaded, 'LIMS_RUNTIME_LOCAL')
        self.assertIn('ALL_TABLES', sql)
        self.assertIn("SYS_CONTEXT('USERENV', 'SESSION_USER')", sql)
        self.assertIn("CONSTRAINT_NAME='PK_EMC_INVOICE'", sql[:sql.index('EXECUTE IMMEDIATE')])
        self.assertIn("OBJECT_NAME='IDX_CEC_TEMPLATEID'", sql[:sql.index('EXECUTE IMMEDIATE')])
        self.assertIn('ALL_CONS_COLUMNS', sql)
        self.assertIn('ALL_IND_COLUMNS', sql)
        self.assertIn("NLS_LENGTH_SEMANTICS must be BYTE", sql)


if __name__ == '__main__':
    unittest.main()
