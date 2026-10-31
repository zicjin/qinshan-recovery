import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

class SecurityTenantFamilyCoverageTest(unittest.TestCase):
    def test_remaining_security_tenant_items_have_independent_results(self):
        registry = json.loads((ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json').read_text())
        identity = json.loads((ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-bootstrap-identity-20261025.json').read_text())
        report = json.loads((ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-security-tenant-20261025.json').read_text())
        identity_ids = {item['registry_id'] for item in identity['results']}
        expected = {item['id'] for item in registry['items']
                    if item.get('scope') == 'external-native'
                    and item.get('family') in {'security-identity', 'tenant-organization'}
                    and item['id'] not in identity_ids}
        results = {item['registry_id']: item for item in report['results']}
        self.assertEqual(set(results), expected)
        self.assertTrue(expected)
        self.assertTrue(all(item['status'] in {'owned-pass', 'removed-negative-pass'} for item in results.values()))
        self.assertTrue(all(item.get('evidence') and item.get('observed') for item in results.values()))
        self.assertTrue(all(report['cleanup'].values()))
        self.assertEqual(report['behavioral_evidence']['permission_matrix'],
                         '.scratch/sunway-supplier-exit-api-first/archive/historical/permission-role-organization-20261025-current.json')

    def test_permission_batch_covers_roles_orgs_and_modes(self):
        report = json.loads((ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-security-tenant-20261025.json').read_text())
        observed = report['results'][0]['observed']
        self.assertEqual(observed['permission_batch_cases'], 184)
        self.assertEqual(set(observed['roles']), {'reader', 'manager', 'admin', 'role-admin'})
        self.assertEqual(set(observed['organizations']), {'org-a', 'org-b'})
        self.assertEqual(set(observed['modes']), {'default', 'isolated'})

if __name__ == '__main__':
    unittest.main()
