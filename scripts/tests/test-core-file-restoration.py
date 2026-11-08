#!/usr/bin/env python3
"""Source-pinned, single-table migration checks; never connect to Oracle."""
import hashlib
import json
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[2]
BASE = ROOT / 'api-fixtures/schema/core-file-restoration'


class CoreFileRestorationTest(unittest.TestCase):
    def test_source_pins_and_in_place_column_contract(self):
        manifest = json.loads(BASE.with_suffix('.json').read_text())
        for source in manifest['sources']:
            path = ROOT / source['path']
            self.assertEqual(source['sha256'], hashlib.sha256(path.read_bytes()).hexdigest())
            lines = path.read_text().splitlines()
            for block in source['blocks']:
                text = '\n'.join(lines[block['line_start'] - 1:block['line_end']])
                self.assertEqual(block['sha256'], hashlib.sha256(text.encode()).hexdigest())
        old = manifest['old_columns']
        restored = manifest['restored_columns']
        self.assertEqual(10, len(old))
        self.assertEqual(21, len(restored))
        self.assertEqual([row['name'] for row in old], [row['name'] for row in restored[:10]])
        self.assertEqual({'TIMES', 'VALIDTIMEFROM', 'VALIDTIMETO', 'CREATEDBYID', 'CREATEDBYNAME',
                          'PERMANENT', 'REMARK', 'CREATEDBYORGID', 'CREATEDBYORGNAME', 'SIZE', 'VERSION'},
                         {row['name'] for row in restored[10:]})
        self.assertEqual('PK_OWNED_CORE_FILE', manifest['retained_primary_key'])
        self.assertEqual('FREEPDB1', manifest['container'])
        self.assertEqual('LIMS_RUNTIME_LOCAL', manifest['owner'])
        source = ROOT / manifest['sources'][0]['path']
        create = '\n'.join(source.read_text().splitlines()[1399:1422])
        original = {}
        for name, datatype, length, nullable in re.findall(
                r'"([A-Z][A-Z0-9_]*)"\s+(NUMBER|VARCHAR2|DATE)(?:\((\d+)(?: BYTE)?\))?(\s+NOT NULL)?', create):
            original[name] = {'name': name, 'type': datatype,
                              'length': int(length) if datatype == 'VARCHAR2' else None,
                              'precision': int(length) if datatype == 'NUMBER' and length else None,
                              'scale': 0 if datatype == 'NUMBER' and length else None,
                              'nullable': 'N' if nullable else 'Y'}
        self.assertEqual(original, {row['name']: row for row in restored})
        self.assertEqual(restored, manifest['target_definition']['columns'])
        self.assertEqual([row['name'] for row in restored], manifest['physical_order'])
        self.assertEqual(old, manifest['historical_columns'])

    def test_every_original_comment_including_md5_name_is_restored(self):
        manifest = json.loads(BASE.with_suffix('.json').read_text())
        source = ROOT / manifest['sources'][0]['path']
        block = '\n'.join(source.read_text().splitlines()[1399:1442])
        expected = {}
        for line in block.splitlines():
            match = re.fullmatch(r'COMMENT ON COLUMN "T_CORE_FILE"\."([A-Z][A-Z0-9_]*)" IS \'(.*)\';', line)
            if match:
                expected[match[1]] = match[2]
            match = re.fullmatch(r'COMMENT ON TABLE "T_CORE_FILE" IS \'(.*)\';', line)
            if match:
                expected[''] = match[1]
        self.assertEqual(expected, manifest['comments'])
        self.assertEqual('MD5加密的文件名称（主键+$+文件名称后加密）', manifest['comments']['MD5NAME'])
        self.assertEqual(expected, manifest['target_definition']['comments'])
        sql = BASE.with_suffix('.sql').read_text()
        for name, comment in expected.items():
            literal = "''" + comment.replace("'", "''''") + "''"
            self.assertIn(literal, sql)

    def test_sql_rejects_unknown_before_ddl_and_verifies_exact_noop(self):
        sql = BASE.with_suffix('.sql').read_text()
        manifest = json.loads(BASE.with_suffix('.json').read_text())
        self.assertEqual(manifest['fixture_sha256'], hashlib.sha256(sql.encode()).hexdigest())
        first = sql.index("EXECUTE IMMEDIATE 'ALTER TABLE")
        prefix = sql[:first]
        for guard in ['CURRENT_SCHEMA', 'SESSION_USER', 'CON_NAME', 'FREEPDB1', 'NLS_LENGTH_SEMANTICS',
                      'ALL_TAB_COLS', 'ALL_CONSTRAINTS', 'ALL_CONS_COLUMNS', 'ALL_INDEXES',
                      'ALL_IND_COLUMNS', 'ALL_TRIGGERS', 'ALL_TAB_COMMENTS', 'ALL_COL_COMMENTS',
                      'Incoming foreign key', 'Unexpected column count', 'Nonempty T_CORE_FILE',
                      'verify_structure(FALSE)', 'verify_structure(TRUE)', 'RETURN;']:
            self.assertIn(guard, prefix)
        self.assertIn('verify_structure(TRUE)', sql[first:])
        self.assertIn('Oracle DDL implicitly commits', sql)
        self.assertNotRegex(sql, r'(?i)\b(?:INSERT\s+INTO|UPDATE\s+\w+|DELETE\s+FROM|MERGE\s+INTO|DROP|TRUNCATE|RENAME)\b')
        self.assertNotIn('LIMS_BASELINE_LOCAL', sql)
        ddl = re.findall(r"EXECUTE IMMEDIATE '((?:[^']|'')*)'", sql)
        self.assertTrue(ddl)
        for statement in ddl:
            self.assertRegex(statement, r'^(?:ALTER TABLE "LIMS_RUNTIME_LOCAL"\."T_CORE_FILE" (?:ADD|MODIFY)|COMMENT ON (?:COLUMN|TABLE) "LIMS_RUNTIME_LOCAL"\."T_CORE_FILE"|CREATE INDEX "LIMS_RUNTIME_LOCAL"\."IDX_T_CORE_FILE_TARGETID" ON "LIMS_RUNTIME_LOCAL"\."T_CORE_FILE")')
        self.assertEqual(1, sum(' ADD (' in statement for statement in ddl))
        self.assertEqual(1, sum(' MODIFY (' in statement for statement in ddl))


if __name__ == '__main__':
    unittest.main()
