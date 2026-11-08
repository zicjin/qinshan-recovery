#!/usr/bin/env python3
"""Keep nine-table admission exact and preserve the existing schema policies."""
import json
from pathlib import Path
import shutil
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'scripts'))
import local_schema_policy as policy
import schema_fixture

NEW_POLICY = 'approved-missing-runtime-and-email-tables-v2'
EMAIL_TABLES = {'T_CORE_EMAIL_CONFIG_RECEIPENT', 'T_CORE_EMAIL_CONFIG_REC_ROLE'}


class LocalSchemaPolicyTest(unittest.TestCase):
    def test_new_policy_pins_nine_tables_and_both_original_email_definitions(self):
        descriptor, predicates = policy.definition(NEW_POLICY)
        self.assertEqual(descriptor['table_count'], 9)
        self.assertEqual(descriptor['column_count'], 125)
        self.assertEqual(set(descriptor['definition_sha256']), schema_fixture.TABLES | EMAIL_TABLES)
        self.assertEqual(len(predicates), 389)
        self.assertEqual(descriptor['additional_fixtures'][0]['fixture_sha256'],
                         '73fab487b8612cc4ba9d0cfe254f7db1816b83b0debab63e71344edfa7d8fbb6')
        self.assertEqual(schema_fixture.TABLES, frozenset(descriptor['definition_sha256']) - EMAIL_TABLES)

    def test_existing_policy_descriptor_and_default_rejection_remain_unchanged(self):
        descriptor, _ = policy.definition(policy.APPROVED)
        self.assertEqual(policy.digest(descriptor),
                         '48233a6db2976b6a2120b1013543e609fa6519a73711f4f74acb5f240b2b3137')
        baseline = {'ORIGINAL'}
        seven = baseline | schema_fixture.TABLES
        nine = seven | EMAIL_TABLES
        policy.check_inventories(baseline, baseline)
        policy.check_inventories(baseline, seven, policy.APPROVED)
        for selected in (policy.EQUAL, policy.APPROVED):
            with self.subTest(policy=selected), self.assertRaises(policy.PolicyError):
                policy.check_inventories(baseline, nine, selected)
        policy.check_inventories(baseline, nine, NEW_POLICY)
        for invalid in (seven, nine | {'UNKNOWN_EXTRA'}, nine - {'ORIGINAL'},
                        nine - {'T_CORE_EMAIL_CONFIG_RECEIPENT'}):
            with self.subTest(tables=invalid), self.assertRaises(policy.PolicyError):
                policy.check_inventories(baseline, invalid, NEW_POLICY)
        with self.assertRaises(policy.PolicyError):
            policy.check_inventories(baseline | EMAIL_TABLES, nine, NEW_POLICY)

    def test_new_policy_rejects_source_fixture_manifest_and_renderer_drift(self):
        with tempfile.TemporaryDirectory() as temporary:
            copied = Path(temporary)
            manifest = json.loads((ROOT / 'api-fixtures/schema/email-config-associations.json').read_text())
            paths = [schema_fixture.BASE + '.json', schema_fixture.BASE + '.sql',
                     *schema_fixture.SOURCE_PATHS, 'scripts/schema_fixture.py',
                     'api-fixtures/schema/email-config-associations.json',
                     'api-fixtures/schema/email-config-associations.sql']
            for name in paths:
                target = copied / name
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / name, target)
            policy.definition(NEW_POLICY, copied)
            for name in (manifest['source']['path'], manifest['fixture']['path'],
                         'api-fixtures/schema/email-config-associations.json', 'scripts/schema_fixture.py'):
                with self.subTest(path=name):
                    target = copied / name
                    raw = target.read_bytes()
                    target.write_bytes(raw + b'\n# changed\n')
                    with self.assertRaises(policy.PolicyError):
                        policy.definition(NEW_POLICY, copied)
                    target.write_bytes(raw)


if __name__ == '__main__':
    unittest.main()
