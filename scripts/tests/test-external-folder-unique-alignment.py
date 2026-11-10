#!/usr/bin/env python3
"""Check the finite source-bound folder constraint migration, without Oracle."""
from pathlib import Path
import hashlib
import json
import re
import unittest

ROOT = Path(__file__).resolve().parents[2]
BASE = ROOT / 'api-fixtures/schema/external-folder-unique-alignment-20261111'


class FolderUniqueAlignmentTest(unittest.TestCase):
    def test_exact_source_statements_and_fixture_are_hash_bound(self):
        manifest = json.loads(BASE.with_suffix('.json').read_text())
        sql = BASE.with_suffix('.sql').read_bytes()
        self.assertEqual(manifest['sql']['sha256'], hashlib.sha256(sql).hexdigest())
        for source in manifest['sources']:
            raw = (ROOT / source['path']).read_bytes()
            self.assertEqual(source['sha256'], hashlib.sha256(raw).hexdigest())
            self.assertIn(source['statement'], raw.decode())
            self.assertEqual(source['statement_sha256'], hashlib.sha256(source['statement'].encode()).hexdigest())
        self.assertEqual(manifest['source_constraint']['validated'], 'NOT VALIDATED')
        self.assertEqual(manifest['source_index']['uniqueness'], 'NONUNIQUE')
        self.assertFalse(manifest['business_rows_written'])

    def test_only_original_index_and_novalidate_ddl_and_exact_local_target(self):
        sql = BASE.with_suffix('.sql').read_text()
        commands = re.findall(r"EXECUTE IMMEDIATE\s+'((?:[^']|'')*)'", sql, re.I)
        self.assertEqual(commands, ['CREATE INDEX "IDX_T_LIMS_FOLDERS_FOLDERNO" ON "T_LIMS_FOLDERS" ("FOLDERNO")',
                                   'ALTER TABLE "T_LIMS_FOLDERS" ADD CONSTRAINT "UKT_LIMS_FOLDERS_FOLDERNO" UNIQUE ("FOLDERNO") USING INDEX "IDX_T_LIMS_FOLDERS_FOLDERNO" ENABLE NOVALIDATE'])
        self.assertNotRegex(sql, r'(?i)ENABLE\s+VALIDATE\b')
        self.assertNotRegex(sql, r'(?im)^\s*(?:INSERT|UPDATE|DELETE|MERGE|DROP|TRUNCATE|GRANT)\b')
        self.assertIn("SYS_CONTEXT('USERENV','SESSION_USER') <> 'LIMS_RUNTIME_LOCAL'", sql)
        self.assertIn("SYS_CONTEXT('USERENV','CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'", sql)
        self.assertIn("SYS_CONTEXT('USERENV','CON_NAME') <> 'FREEPDB1'", sql)
        self.assertIn('WHENEVER SQLERROR EXIT FAILURE ROLLBACK', sql)
        self.assertIn('WHENEVER OSERROR EXIT FAILURE ROLLBACK', sql)
        self.assertIn('SET DEFINE OFF', sql)

    def test_conflict_and_repeat_paths_require_exact_definition(self):
        sql = BASE.with_suffix('.sql').read_text()
        self.assertEqual(sql.count('assert_constraint;'), 2)
        self.assertRegex(sql, r'IF existing_count = 0 THEN[\s\S]*ELSE\s+assert_constraint;')
        self.assertLess(sql.index('Another folder unique constraint already exists'), sql.index('EXECUTE IMMEDIATE'))
        self.assertIn('IF index_count = 0 THEN', sql)
        self.assertIn('IF existing_count = 0 THEN', sql)
        for predicate in ("CONSTRAINT_TYPE='U'", "STATUS='ENABLED'", "VALIDATED='NOT VALIDATED'",
                          "DEFERRABLE='NOT DEFERRABLE'", "DEFERRED='IMMEDIATE'", "INDEX_OWNER=USER",
                          "INDEX_NAME='IDX_T_LIMS_FOLDERS_FOLDERNO'", "GENERATED='USER NAME'",
                          "COLUMN_NAME='FOLDERNO' AND POSITION=1", "RELY IS NULL", "INVALID IS NULL"):
            self.assertIn(predicate, sql)
        self.assertIn("INDEX_TYPE='NORMAL' AND UNIQUENESS='NONUNIQUE' AND STATUS='VALID'", sql)
        self.assertIn("COLUMN_NAME='FOLDERNO' AND COLUMN_POSITION=1 AND DESCEND='ASC'", sql)
        self.assertIn("DATA_TYPE='VARCHAR2' AND DATA_LENGTH=30", sql)
        self.assertIn("CHAR_USED='B' AND CHAR_LENGTH=30", sql)
        self.assertIn('IF n <> 1 THEN', sql)
        self.assertIn('IF n <> 0 THEN', sql)
        self.assertIn('IF n <> rows_before THEN', sql)
        self.assertIn('IF n <> duplicate_groups_before THEN', sql)


if __name__ == '__main__':
    unittest.main()
