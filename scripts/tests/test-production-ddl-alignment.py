#!/usr/bin/env python3
"""Verify the reviewed production DDL subset, not production connectivity."""
from pathlib import Path
import hashlib
import json
import re
import unittest

ROOT = Path(__file__).resolve().parents[2]
DIRECTORY = ROOT / 'api-fixtures/schema'


class ProductionAlignmentTest(unittest.TestCase):
    def test_reviewed_sources_and_statements_remain_bound(self):
        source_path = DIRECTORY / 'production-ddl-source-20261109.json'
        source = json.loads(source_path.read_text())
        columns = {(r['TABLE_NAME'], r['COLUMN_NAME']): r for r in source['columns']}
        for name in ('production-ddl-alignment-20261109',
                     'production-ddl-field-alignment-20261109'):
            manifest = json.loads((DIRECTORY / (name + '.json')).read_text())
            sql = (ROOT / manifest['sql']['path']).read_bytes()
            self.assertEqual(manifest['sql']['sha256'], hashlib.sha256(sql).hexdigest())
            self.assertEqual(manifest['reviewed_source']['sha256'],
                             hashlib.sha256(source_path.read_bytes()).hexdigest())
            text = sql.decode()
            self.assertIn("CURRENT_SCHEMA')<>'LIMS_RUNTIME_LOCAL'", text)
            self.assertIn("CON_NAME')<>'FREEPDB1'", text)
            self.assertIn('WHENEVER SQLERROR EXIT FAILURE ROLLBACK', text)
            self.assertNotRegex(text, r'(?im)^\s*(INSERT|UPDATE|DELETE|TRUNCATE)\b')
            self.assertNotIn('LIMS_BASELINE_LOCAL', text)
            self.assertNotIn('DATABASE LINK', text)
            for table, column, width in re.findall(
                    r'ALTER TABLE "LIMS_RUNTIME_LOCAL"\."([^"]+)" MODIFY '
                    r'\("([^"]+)" VARCHAR2\((\d+) BYTE\)\)', text):
                actual = columns[table, column]
                self.assertEqual('VARCHAR2', actual['DATA_TYPE'])
                self.assertEqual('B', actual['CHAR_USED'])
                self.assertEqual(width, actual['DATA_LENGTH'])
            if 'field-alignment' in name:
                self.assertIn('File metadata must be empty', text)
                self.assertIn('Remark constraint dependency', text)
                self.assertIn('Remark index dependency', text)
                function = '\n'.join(line.rstrip() for line in
                    source['function_ddl'][0]['ddl'].replace(
                        '"SOURCE_SCHEMA".', '"LIMS_RUNTIME_LOCAL".').splitlines()).strip()
                self.assertIn(function, text)
            else:
                for item in source['table_ddl'] + source['index_ddl']:
                    expected = '\n'.join(line.rstrip() for line in
                        item['ddl'].replace('"SOURCE_SCHEMA".',
                                            '"LIMS_RUNTIME_LOCAL".').splitlines()).strip()
                    self.assertIn(expected, text)


if __name__ == '__main__':
    unittest.main()
