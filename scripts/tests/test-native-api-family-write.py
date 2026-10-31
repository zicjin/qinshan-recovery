#!/usr/bin/env python3
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REGISTRY = ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json'
REPORT = ROOT / '.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-write-20261025.json'


class WriteFamilyTest(unittest.TestCase):
    def load(self, path):
        return json.loads(path.read_text(encoding='utf-8'))

    def test_family_report_is_pass_only_for_registry_items_in_scope(self):
        registry = self.load(REGISTRY)
        report = self.load(REPORT)
        items = [item for item in registry['items']
                 if item.get('scope') in ('external-native', 'sunway-license')
                 and item.get('family') == 'write-transaction-audit']
        self.assertEqual(report['status'], 'executed-pass')
        self.assertEqual(report['family'], 'write-transaction-audit')
        self.assertEqual(report['registry_item_count'], len(items))
        self.assertEqual(len(report['results']), len(items))
        self.assertTrue(report['limitations']['unclassified_native_exports_remain_unknown'])
        self.assertFalse(report['claims_owned_business_write_closure'])
        self.assertGreaterEqual(len(report['candidate_review']), 5)
        self.assertTrue(all(row.get('reason') and row.get('next_family') for row in report['candidate_review']))

    def test_no_unclassified_write_item_is_silently_closed(self):
        registry = self.load(REGISTRY)
        report = self.load(REPORT)
        family_items = {item['id'] for item in registry['items']
                        if item.get('family') == 'write-transaction-audit'}
        result_ids = {result['registry_id'] for result in report['results']}
        self.assertEqual(result_ids, family_items)
        self.assertNotIn('unknown', {result['status'] for result in report['results']})


if __name__ == '__main__':
    unittest.main()
