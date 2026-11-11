#!/usr/bin/env python3
"""Public offline verification of the finite empty-table scale correction."""
from pathlib import Path
import copy
import hashlib
import json
import subprocess
import tempfile
import unittest
ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/verify-external-logical-clone-fixture.py'
MANIFEST = ROOT / 'api-fixtures/schema/external-clearance-integer-alignment-20261112.json'

class EmptyClearanceIntegerAlignmentTest(unittest.TestCase):
    def run_plan(self, plan):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / 'plan.json'
            path.write_text(json.dumps(plan))
            return subprocess.run(['python3', str(SCRIPT), '--integer-alignment-manifest', str(path)], text=True, capture_output=True)

    def test_exact_two_source_integer_columns_pass(self):
        result = self.run_plan(json.loads(MANIFEST.read_text()))
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"columns": 2', result.stdout)

    def test_missing_changed_column_and_wrong_owner_reject(self):
        plan = json.loads(MANIFEST.read_text())
        for mutation in ('missing', 'scale', 'target', 'source'):
            with self.subTest(mutation=mutation):
                changed = copy.deepcopy(plan)
                if mutation == 'missing': changed['columns'].pop()
                elif mutation == 'scale': changed['columns'][0]['scale'] = None
                elif mutation == 'target': changed['target_owner'] = 'OTHER'
                else: changed['sources'][0]['sha256'] = '0'*64
                self.assertNotEqual(self.run_plan(changed).returncode, 0)

    def test_rehashed_sql_cannot_remove_empty_guard(self):
        plan = json.loads(MANIFEST.read_text())
        raw = (ROOT / plan['sql']['path']).read_text().replace('IF rows_now <> 0 THEN', 'IF rows_now < 0 THEN')
        with tempfile.NamedTemporaryFile(dir=ROOT / 'api-fixtures/schema', suffix='.sql', mode='w') as temp:
            temp.write(raw); temp.flush()
            plan['sql'] = {'path': str(Path(temp.name).relative_to(ROOT)), 'sha256': hashlib.sha256(raw.encode()).hexdigest()}
            self.assertNotEqual(self.run_plan(plan).returncode, 0)

if __name__ == '__main__': unittest.main()
