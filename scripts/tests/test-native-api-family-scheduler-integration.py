import json
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[2]
REGISTRY=json.loads((ROOT/'.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json').read_text())
REPORT=json.loads((ROOT/'.scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-scheduler-integration-20261025.json').read_text())
PATTERNS=('taskExecutor','taskScheduler','configureTasks','restTemplate','redisTemplate','redisCacheWriter','cacheManager')


class SchedulerIntegrationFamilyTest(unittest.TestCase):
    def test_family_report_closes_only_direct_symbols(self):
        expected={item['id']:item for item in REGISTRY['items']
                  if item['scope']=='external-native'
                  and item['kind']=='native-export'
                  and any(pattern in item['key'] for pattern in PATTERNS)}
        self.assertEqual(REPORT['status'],'executed-pass')
        self.assertEqual(REPORT['required_item_count'],len(expected))
        self.assertEqual(len(REPORT['results']),len(expected))
        seen=set()
        for result in REPORT['results']:
            item_id=result['registry_id']
            self.assertIn(item_id,expected)
            self.assertNotIn(item_id,seen)
            seen.add(item_id)
            self.assertEqual(result['status'],'owned-pass')
            self.assertIn(result['execution'],('http-contract','java-contract'))
            self.assertTrue(result['evidence'])
            self.assertTrue(result['observed'])
        self.assertEqual(seen,set(expected))

    def test_cleanup_and_scope_metadata(self):
        self.assertFalse(REPORT['production_access'])
        self.assertFalse(REPORT['source_dirty'])
        self.assertTrue(REPORT['audit']['credentials_excluded'])
        self.assertTrue(all(value is True for value in REPORT['cleanup'].values()))
        unknown_exports=[item for item in REGISTRY['items']
                         if item['scope']=='external-native'
                         and item['kind']=='native-export'
                         and item['id'] not in {r['registry_id'] for r in REPORT['results']}]
        self.assertTrue(unknown_exports)


if __name__=='__main__':
    unittest.main()
