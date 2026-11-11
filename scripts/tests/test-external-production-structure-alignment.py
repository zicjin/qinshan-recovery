#!/usr/bin/env python3
"""Exercise the existing fixture verifier's public alignment seam offline."""
from pathlib import Path
import copy
import hashlib
import json
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/verify-external-logical-clone-fixture.py'
MANIFEST = ROOT / 'api-fixtures/schema/external-production-structure-alignment-20261112.json'


class ExternalProductionAlignmentTest(unittest.TestCase):
    def run_manifest(self, manifest):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / 'alignment.json'
            path.write_text(json.dumps(manifest), encoding='utf-8')
            return subprocess.run(['python3', str(SCRIPT), '--alignment-manifest', str(path)],
                                  text=True, capture_output=True)

    def test_formal_source_defaults_are_complete_and_portably_verified(self):
        result = self.run_manifest(json.loads(MANIFEST.read_text()))
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"defaults": 33', result.stdout)
        self.assertIn('"columns_checked": 1303', result.stdout)

    def test_missing_or_changed_default_cannot_be_recertified(self):
        manifest = json.loads(MANIFEST.read_text())
        for mutation in ('missing', 'different_expression'):
            with self.subTest(mutation=mutation):
                changed = copy.deepcopy(manifest)
                if mutation == 'missing':
                    changed['defaults'].pop()
                else:
                    changed['defaults'][0]['expression'] = "'1'"
                self.assertNotEqual(self.run_manifest(changed).returncode, 0)

    def test_source_or_target_drift_fails_closed(self):
        manifest = json.loads(MANIFEST.read_text())
        for mutation in ('source', 'target'):
            with self.subTest(mutation=mutation):
                changed = copy.deepcopy(manifest)
                if mutation == 'source':
                    changed['sources'][0]['sha256'] = '0' * 64
                else:
                    changed['target_owner'] = 'PRODUCTION'
                self.assertNotEqual(self.run_manifest(changed).returncode, 0)

    def test_rehashed_sql_cannot_drop_preflight_or_add_business_writes(self):
        manifest = json.loads(MANIFEST.read_text())
        original = (ROOT / manifest['sql']['path']).read_text()
        for modified in (original.replace('  assert_preflight;\n', '', 1), original + '\nDELETE FROM T_CRM_INVOICE;\n'):
            with tempfile.NamedTemporaryFile(dir=ROOT / 'api-fixtures/schema', suffix='.sql', mode='w', encoding='utf-8') as temp:
                temp.write(modified)
                temp.flush()
                changed = copy.deepcopy(manifest)
                changed['sql'] = {'path': str(Path(temp.name).relative_to(ROOT)),
                                  'sha256': hashlib.sha256(modified.encode()).hexdigest()}
                self.assertNotEqual(self.run_manifest(changed).returncode, 0)


if __name__ == '__main__':
    unittest.main()
