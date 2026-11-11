#!/usr/bin/env python3
"""Verify the finite NOT NULL migration through the existing public CLI."""
from pathlib import Path
import copy
import hashlib
import json
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/verify-external-logical-clone-fixture.py'
MANIFEST = ROOT / 'api-fixtures/schema/external-not-null-alignment-20261112.json'


class NotNullAlignmentTest(unittest.TestCase):
    def run_manifest(self, manifest):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / 'manifest.json'
            path.write_text(json.dumps(manifest), encoding='utf-8')
            return subprocess.run(['python3', str(SCRIPT), '--notnull-alignment-manifest', str(path)],
                                  capture_output=True, text=True)

    def test_all_thirteen_source_not_null_constraints_are_verified_without_private_input(self):
        result = subprocess.run(['python3', str(SCRIPT), '--notnull-alignment-manifest', str(MANIFEST)],
                                capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"not_null_constraints": 13', result.stdout)
        self.assertIn('"strict_evidence": false', result.stdout)


    def test_omitted_or_changed_source_constraint_cannot_be_recertified(self):
        original = json.loads(MANIFEST.read_text())
        for mutation in ('omit', 'column', 'temporary_name', 'target', 'source_hash', 'strict'):
            with self.subTest(mutation=mutation):
                changed = copy.deepcopy(original)
                if mutation == 'omit':
                    changed['not_null_constraints'].pop()
                elif mutation == 'column':
                    changed['not_null_constraints'][0]['column'] = 'CREATEDBYID'
                elif mutation == 'temporary_name':
                    changed['not_null_constraints'][0]['temporary_constraint'] = 'ARBITRARY'
                elif mutation == 'target':
                    changed['target_owner'] = 'PRODUCTION'
                elif mutation == 'strict':
                    changed['strict_evidence'] = True
                else:
                    changed['sources'][0]['sha256'] = '0' * 64
                self.assertNotEqual(self.run_manifest(changed).returncode, 0)

    def test_rehashed_sql_with_missing_guard_or_early_drop_or_business_write_is_rejected(self):
        original = json.loads(MANIFEST.read_text())
        sql = (ROOT / original['sql']['path']).read_text()
        for modified in (sql.replace('  assert_preflight;\n', '', 1),
                         'ALTER TABLE "T_CRM_INVOICE" DROP CONSTRAINT "SYS_C0016504";\n' + sql,
                         sql + '\nDELETE FROM T_CRM_INVOICE;\n'):
            with tempfile.NamedTemporaryFile(dir=ROOT / 'api-fixtures/schema', suffix='.sql', mode='w', encoding='utf-8') as temp:
                temp.write(modified)
                temp.flush()
                changed = copy.deepcopy(original)
                changed['sql'] = {'path': str(Path(temp.name).relative_to(ROOT)),
                                  'sha256': hashlib.sha256(modified.encode()).hexdigest()}
                self.assertNotEqual(self.run_manifest(changed).returncode, 0)

    def test_exact_unnamed_add_check_from_generated_dictionary_name_is_supported(self):
        # Oracle GET_DDL omits the name of a GENERATED NAME ordinary CHECK.
        # Exercise the public verifier against the finite SQL shape correction.
        original = json.loads(MANIFEST.read_text())
        sql = (ROOT / original['sql']['path']).read_text()
        named = "    ELSIF compact=base||'ADD'||named_clause||'CHECK(\"'||p_column||'\"ISNOTNULL)ENABLE'"
        unnamed = "        OR compact=base||'ADDCHECK(\"'||p_column||'\"ISNOTNULL)ENABLE'"
        corrected = sql.replace(named + ' THEN', named + '\n' + unnamed + ' THEN')
        with tempfile.NamedTemporaryFile(dir=ROOT / 'api-fixtures/schema', suffix='.sql', mode='w', encoding='utf-8') as temp:
            temp.write(corrected)
            temp.flush()
            changed = copy.deepcopy(original)
            changed['sql'] = {'path': str(Path(temp.name).relative_to(ROOT)),
                              'sha256': hashlib.sha256(corrected.encode()).hexdigest()}
            result = self.run_manifest(changed)
            self.assertEqual(result.returncode, 0, result.stderr)


if __name__ == '__main__':
    unittest.main()
