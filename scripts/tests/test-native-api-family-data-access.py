import json
import subprocess
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REPORT = ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-data-access-20261025.json'
EXPECTED = {
    'native-java-declaration:c11a0ea464ef2354',
    'native-java-declaration:ea0dca711e1f3bf8',
    'native-export:43d05c835a4f4fdf',
}

class DataAccessFamilyTest(unittest.TestCase):
    def test_family_closes_only_direct_data_access_items(self):
        report = json.loads(REPORT.read_text())
        self.assertEqual(report['status'], 'executed-pass')
        self.assertFalse(report['production_access'])
        self.assertFalse(report['source_dirty'])
        self.assertEqual({r['registry_id'] for r in report['results']}, EXPECTED)
        self.assertTrue(all(r['status'] == 'removed-negative-pass' for r in report['results']))
        self.assertTrue(all(r['observed'] for r in report['results']))
        self.assertTrue(all(report['cleanup'].values()))

    def test_project_has_no_direct_supplier_data_access_calls(self):
        result = subprocess.run(
            ['rg', '-n', 'MybatisHelper|DataSourceUtils', 'project/src/main/java', 'project/src/main/resources'],
            cwd=ROOT, text=True, capture_output=True)
        self.assertNotEqual(result.returncode, 0, result.stdout)

if __name__ == '__main__':
    unittest.main()
