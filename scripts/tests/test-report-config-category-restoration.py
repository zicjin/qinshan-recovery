#!/usr/bin/env python3
"""Pin the single-column report fixture to original DDL; never connect or apply."""
import hashlib
import json
from pathlib import Path
import re
import unittest

ROOT = Path(__file__).resolve().parents[2]
BASE = ROOT / 'api-fixtures/schema/report-config-category-restoration'


class ReportCategoryRestorationTest(unittest.TestCase):
    def setUp(self):
        self.manifest = json.loads(BASE.with_suffix('.json').read_text())
        self.sql = BASE.with_suffix('.sql').read_text()

    def test_original_source_and_mapper_pins(self):
        for source in self.manifest['sources']:
            path = ROOT / source['path']
            self.assertEqual(source['sha256'], hashlib.sha256(path.read_bytes()).hexdigest())
            lines = path.read_text().splitlines()
            for block in source.get('blocks', []):
                text = '\n'.join(lines[block['line_start'] - 1:block['line_end']])
                self.assertEqual(block['sha256'], hashlib.sha256(text.encode()).hexdigest())
        ddl = ROOT / self.manifest['sources'][0]['path']
        create = '\n'.join(ddl.read_text().splitlines()[2320:2327])
        definition = next(line.strip().rstrip(' ,') for line in create.splitlines()
                          if '"CATEGORY"' in line)
        self.assertEqual('"CATEGORY" VARCHAR2(36 BYTE)', definition)
        self.assertNotRegex(definition, r'(?i)DEFAULT|NOT NULL')
        self.assertEqual({'name': 'CATEGORY', 'type': 'VARCHAR2', 'length': 36,
                          'length_semantics': 'BYTE', 'nullable': 'Y',
                          'default': None, 'comment': '类别'}, self.manifest['added_column'])
        mapper = (ROOT / self.manifest['sources'][1]['path']).read_text()
        self.assertIn('B.CATEGORY', mapper)
        self.assertEqual(2, mapper.count("T.CATEGORY = 'select'"))
        self.assertEqual(self.manifest['stock']['mapper_member_sha256'],
                         self.manifest['sources'][1]['sha256'])

    def test_only_source_column_and_comment_can_change(self):
        self.assertEqual(self.manifest['fixture']['sha256'],
                         hashlib.sha256(BASE.with_suffix('.sql').read_bytes()).hexdigest())
        statements = re.findall(r"EXECUTE IMMEDIATE '((?:[^']|'')*)'", self.sql)
        self.assertEqual([
            'ALTER TABLE "LIMS_RUNTIME_LOCAL"."T_CORE_REPORT_CONFIG" ADD ("CATEGORY" VARCHAR2(36 BYTE))',
            'COMMENT ON COLUMN "LIMS_RUNTIME_LOCAL"."T_CORE_REPORT_CONFIG"."CATEGORY" IS \'\'类别\'\'',
            'SELECT COUNT(*) FROM "LIMS_RUNTIME_LOCAL"."T_CORE_REPORT_CONFIG" WHERE "CATEGORY" IS NOT NULL',
        ], statements)
        self.assertNotRegex(self.sql, r'(?i)\b(?:INSERT\s+INTO|UPDATE\s+\w+|DELETE\s+FROM|MERGE\s+INTO|DROP|TRUNCATE|RENAME)\b')
        self.assertIn('Oracle DDL implicitly commits', self.sql)
        self.assertIn('rows_after<>rows_before', self.sql)
        self.assertIn('New report CATEGORY must remain NULL', self.sql)

    def test_exact_existing_shape_before_ddl_and_equivalent_noop(self):
        self.assertEqual('LIMS_RUNTIME_LOCAL', self.manifest['owner'])
        self.assertEqual('FREEPDB1', self.manifest['container'])
        prefix = self.sql[:self.sql.index("EXECUTE IMMEDIATE 'ALTER TABLE")]
        for guard in ["'CURRENT_SCHEMA'", "'CON_NAME'", "'SESSION_USER'", "'FREEPDB1'",
                      "'LIMS_RUNTIME_LOCAL'", "'SYS'", 'ALL_TABLES', 'ALL_TAB_COLS',
                      'HIDDEN_COLUMN', 'VIRTUAL_COLUMN', 'DEFAULT_LENGTH IS NULL',
                      'Unexpected report config column count', 'verify_columns(FALSE)',
                      'verify_columns(TRUE)', 'RETURN;', 'ALL_COL_COMMENTS', 'ALL_CONS_COLUMNS']:
            self.assertIn(guard, prefix)
        expected = [('ID', 'VARCHAR2', 128, 'N'), ('VERSION', 'NUMBER', 22, 'Y'),
                    ('ORDERNO', 'NUMBER', 22, 'Y'), ('REMARK', 'CLOB', 4000, 'Y'),
                    ('NAME', 'CLOB', 4000, 'Y'), ('CREATEDBYORGNAME', 'VARCHAR2', 270, 'Y'),
                    ('REPORTCONFIGCATEGORY', 'VARCHAR2', 120, 'Y')]
        columns = self.manifest['existing_columns']
        self.assertEqual(expected, [(c['name'], c['type'], c['data_length'], c['nullable'])
                                    for c in columns])
        for position, (name, datatype, length, nullable) in enumerate(expected, 1):
            self.assertIn(f"COLUMN_ID={position} AND COLUMN_NAME='{name}' AND DATA_TYPE='{datatype}'", prefix)
            self.assertIn(f"DATA_LENGTH={length}", prefix)
        self.assertIn("COLUMN_ID=8 AND COLUMN_NAME='CATEGORY'", prefix)
        self.assertIn("DATA_LENGTH=36 AND CHAR_LENGTH=36 AND CHAR_USED='B' AND NULLABLE='Y'", prefix)
        self.assertIn("COMMENTS='类别'", prefix)
        self.assertIn('Partial or incompatible report CATEGORY comment', prefix)
        self.assertNotIn('LIMS_BASELINE_LOCAL', self.sql)
        self.assertNotIn('ALTER SESSION', self.sql)


if __name__ == '__main__':
    unittest.main()
