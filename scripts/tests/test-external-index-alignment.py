#!/usr/bin/env python3
"""Public offline contract of the finite backing-index migration."""
from pathlib import Path
import copy
import hashlib
import json
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/verify-external-index-alignment.py'
MANIFEST = ROOT / 'api-fixtures/schema/external-index-alignment-20261112.json'

class BackingIndexAlignmentTest(unittest.TestCase):
    def run_plan(self, plan=None):
        with tempfile.TemporaryDirectory() as temp:
            args = ['python3', str(SCRIPT)]
            if plan is not None:
                path = Path(temp) / 'plan.json'
                path.write_text(json.dumps(plan))
                args += ['--manifest', str(path)]
            return subprocess.run(args, text=True, capture_output=True)

    def test_formal_four_backing_indexes_are_accepted(self):
        result = self.run_plan()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"indexes": 4', result.stdout)

    def test_changed_mapping_target_source_and_write_scope_reject(self):
        plan = json.loads(MANIFEST.read_text())
        for mutation in ('mapping', 'missing', 'owner', 'container', 'source', 'dml'):
            with self.subTest(mutation=mutation):
                changed = copy.deepcopy(plan)
                if mutation == 'mapping': changed['mappings'][0]['target_index'] = 'OTHER'
                elif mutation == 'missing': changed['mappings'].pop()
                elif mutation == 'owner': changed['target_owner'] = 'OTHER'
                elif mutation == 'container': changed['target_container'] = 'CDB$ROOT'
                elif mutation == 'source': changed['sources'][0]['sha256'] = '0'*64
                else: changed['business_rows_written'] = True
                result = self.run_plan(changed)
                self.assertNotEqual(result.returncode, 0)

    def test_rehashed_sql_cannot_remove_fk_or_target_binding_guard(self):
        plan = json.loads(MANIFEST.read_text())
        sql = ROOT / plan['sql']['path']
        original = sql.read_bytes()
        for old, new in (('IF n <> 0 THEN reject(\'Incoming foreign key conflicts:',
                          'IF n < 0 THEN reject(\'Incoming foreign key conflicts:'),
                         ("OR constraint_index<>p_new OR constraint_index_owner<>USER THEN",
                          'OR 1=0 THEN')):
            with self.subTest(guard=old):
                changed = copy.deepcopy(plan)
                mutated = original.decode().replace(old, new)
                self.assertNotEqual(mutated, original.decode())
                try:
                    sql.write_text(mutated)
                    changed['sql']['sha256'] = hashlib.sha256(mutated.encode()).hexdigest()
                    result = self.run_plan(changed)
                    self.assertNotEqual(result.returncode, 0)
                    self.assertIn('migration SQL contract conflicts', result.stderr)
                finally:
                    sql.write_bytes(original)

if __name__ == '__main__': unittest.main()
