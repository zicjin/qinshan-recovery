#!/usr/bin/env python3
"""Public combined-plan CLI contract; offline temporary checkout only."""
from pathlib import Path
import hashlib
import json
import shutil
import subprocess
import sys
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/verify-combined-oracle-installation.py'


class CombinedOracleInstallationTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix='combined-oracle-installation-')
        self.checkout = Path(self.tmp.name)
        (self.checkout / 'scripts').mkdir()
        for name in ('verify-combined-oracle-installation.py', 'verify-core-schema-package.py'):
            source = ROOT / 'scripts' / name
            shutil.copy2(source, self.checkout / 'scripts' / name)
        shutil.copytree(ROOT / 'deployment/oracle', self.checkout / 'deployment/oracle')
        self.plan_path = self.checkout / 'deployment/oracle/installation-manifest.json'

    def tearDown(self):
        self.tmp.cleanup()

    def plan(self):
        return json.loads(self.plan_path.read_text())

    def save(self, plan):
        self.plan_path.write_text(json.dumps(plan) + '\n')

    def pin_local(self, plan, name):
        raw = (self.plan_path.parent / name).read_bytes()
        for entry in [*plan['files'], *plan['phases']]:
            if entry['path'] == name:
                entry.update(sha256=hashlib.sha256(raw).hexdigest(), bytes=len(raw))
        self.save(plan)

    def replace_package_names(self, package, old, new):
        directory = self.plan_path.parent / (package + '-schema')
        manifest_path = directory / 'manifest.json'
        manifest = json.loads(manifest_path.read_text().replace(old, new))
        for entry in manifest['files']:
            path = directory / entry['path']
            path.write_text(path.read_text().replace(old, new))
            raw = path.read_bytes()
            entry.update(sha256=hashlib.sha256(raw).hexdigest(), bytes=len(raw))
        for obj in manifest['objects']:
            sql = (directory / obj['path']).read_text()
            start = sql.index('-- BEGIN OBJECT ' + obj['object_type'] + ' ' + obj['name'] + '\n')
            body = sql[start:].split('\n', 1)[1].split('-- END OBJECT ', 1)[0].strip() + '\n'
            obj['rendered_sha256'] = hashlib.sha256(body.encode()).hexdigest()
        registry = directory / manifest['registry']['path']
        manifest['registry']['sha256'] = hashlib.sha256(registry.read_bytes()).hexdigest()
        manifest_path.write_text(json.dumps(manifest) + '\n')
        plan = self.plan()
        for entry in plan['packages']:
            if entry['id'] == package:
                entry['sha256'] = hashlib.sha256(manifest_path.read_bytes()).hexdigest()
        files = {entry['path']: entry for entry in manifest['files']}
        for phase in plan['phases']:
            if phase.get('package') == package:
                child = files[phase['path'].split('/', 1)[1]]
                phase.update(sha256=child['sha256'], bytes=child['bytes'])
        self.save(plan)

    def assert_rejected(self):
        result = self.run_plan()
        self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
        report = json.loads(result.stdout)
        self.assertFalse(report['plan_valid'])
        self.assertFalse(report['deployment_ready'])
        return report

    def run_plan(self, check=True):
        command = [sys.executable, str(self.checkout / 'scripts' / SCRIPT.name)]
        return subprocess.run(command + (['--check-plan'] if check else []),
                              cwd=self.checkout, text=True, capture_output=True)

    def test_clean_clone_validates_selected_union_but_default_blocks_installation(self):
        result = self.run_plan()
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        report = json.loads(result.stdout)
        self.assertTrue(report['plan_valid'])
        self.assertFalse(report['deployment_ready'])
        self.assertEqual(report['tables'], 749)
        self.assertEqual(report['package_table_counts'], {'external': 10, 'core': 739})
        self.assertFalse(report['complete_external_domains'])
        self.assertFalse((self.checkout / 'private').exists())
        self.assertFalse((self.checkout / '.scratch').exists())
        blocked = self.run_plan(check=False)
        self.assertEqual(blocked.returncode, 2, blocked.stdout + blocked.stderr)
        self.assertFalse(json.loads(blocked.stdout)['deployment_ready'])

    def test_conflicting_create_and_named_constraint_are_rejected_after_repinning(self):
        # These remain valid standalone packages; the conflict exists only in
        # their shared Oracle namespace and must be caught at this boundary.
        self.replace_package_names('external', 'T_CRM_CONTRACT', 'BKITEM')
        result = subprocess.run([sys.executable, str(self.checkout / 'scripts/verify-core-schema-package.py'),
                                 '--manifest', str(self.plan_path.parent / 'external-schema/manifest.json'),
                                 '--check-package'], capture_output=True, text=True)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn('collision', self.assert_rejected()['reason'])

    def test_named_constraint_collision_is_rejected_after_repinning(self):
        self.replace_package_names('external', 'T_CRM_CONTRACT_CHECK', 'PK_CORE_ABOUT_SYSTEM')
        self.assertIn('constraint', self.assert_rejected()['reason'])

    def test_index_namespace_collision_is_rejected_after_repinning(self):
        self.replace_package_names('external', 'T_CRM_CONTRACT_PK', 'IDX_EMC_FOLDER_FOLDERNO')
        self.assertIn('collision', self.assert_rejected()['reason'])

    def test_implicit_constraint_backing_index_collision_is_rejected(self):
        self.replace_package_names('external', 'T_CRM_CONTRACT_SIGNUSERID_IDX', 'PK_CORE_ABOUT_SYSTEM')
        self.assertIn('collision', self.assert_rejected()['reason'])

    def test_reordered_duplicated_omitted_and_traversing_phases_are_rejected(self):
        original = self.plan()
        for change in ('order', 'duplicate', 'bootstrap-verifier', 'external-verifier', 'traversal'):
            with self.subTest(change=change):
                plan = json.loads(json.dumps(original))
                phases = plan['phases']
                if change == 'order':
                    phases[1], phases[2] = phases[2], phases[1]
                elif change == 'duplicate':
                    phases.insert(2, dict(phases[1]))
                elif change == 'traversal':
                    phases[1]['path'] = '../external-schema/20-tables.sql'
                else:
                    path = ('core-schema/80-bootstrap-verify.sql' if change == 'bootstrap-verifier'
                            else 'external-schema/90-verify.sql')
                    plan['phases'] = [entry for entry in phases if entry['path'] != path]
                self.save(plan)
                self.assert_rejected()
        self.save(original)

    def test_package_manifest_and_child_hash_drift_are_rejected(self):
        path = self.plan_path.parent / 'external-schema/manifest.json'
        original = path.read_bytes()
        path.write_bytes(original + b'\n')
        self.assertIn('manifest hash', self.assert_rejected()['reason'])
        path.write_bytes(original)
        child = self.plan_path.parent / 'external-schema/20-tables.sql'
        child.write_bytes(child.read_bytes() + b'-- altered\n')
        self.assertIn('file hash', self.assert_rejected()['reason'])

    def test_partial_guard_must_precede_any_child_or_ddl_even_after_repinning(self):
        path = self.plan_path.parent / 'install.sql'
        original = path.read_text()
        for change in ('early-include', 'early-ddl', 'conditional', 'extra-tail'):
            with self.subTest(change=change):
                if change == 'early-include':
                    changed = '@@00-preflight.sql\n' + original.replace('@@00-preflight.sql\n', '')
                elif change == 'early-ddl':
                    changed = 'CREATE TABLE UNREVIEWED(ID NUMBER);\n' + original
                elif change == 'conditional':
                    changed = original.replace('  RAISE_APPLICATION_ERROR', '  IF 1=0 THEN RAISE_APPLICATION_ERROR').replace('END;\n/', 'END IF; END;\n/')
                else:
                    changed = original + 'CREATE TABLE UNREVIEWED(ID NUMBER);\n'
                path.write_text(changed)
                self.pin_local(self.plan(), 'install.sql')
                self.assert_rejected()
        path.write_text(original)
        self.pin_local(self.plan(), 'install.sql')

    def test_sql_include_order_and_verifier_coverage_cannot_be_changed_by_repinning(self):
        path = self.plan_path.parent / 'install.sql'
        original = path.read_text()
        for change in ('order', 'missing-verifier'):
            with self.subTest(change=change):
                if change == 'order':
                    changed = original.replace('@@external-schema/20-tables.sql\n@@external-schema/30-indexes.sql',
                                               '@@external-schema/30-indexes.sql\n@@external-schema/20-tables.sql')
                else:
                    changed = original.replace('@@core-schema/80-bootstrap-verify.sql\n', '')
                path.write_text(changed)
                self.pin_local(self.plan(), 'install.sql')
                self.assert_rejected()
        path.write_text(original)
        self.pin_local(self.plan(), 'install.sql')

    def test_union_sql_same_count_substitution_cannot_pass_with_a_fresh_local_hash(self):
        path = self.plan_path.parent / '99-verify.sql'
        path.write_text(path.read_text().replace("'TABLE|BKITEM'", "'TABLE|OTHER_NAME'"))
        self.pin_local(self.plan(), '99-verify.sql')
        self.assertIn('union verifier', self.assert_rejected()['reason'])

    def test_combined_plan_rejects_private_parent_and_symlink_inputs(self):
        original = self.plan()
        for value in ('../core-schema/manifest.json', 'private/manifest.json', '/manifest.json'):
            with self.subTest(value=value):
                plan = json.loads(json.dumps(original))
                plan['packages'][1]['path'] = value
                self.save(plan)
                self.assert_rejected()
        self.save(original)
        path = self.plan_path.parent / '99-verify.sql'
        target = self.checkout / 'replacement.sql'
        path.rename(target)
        path.symlink_to(target)
        self.assertIn('symbolic', self.assert_rejected()['reason'])


if __name__ == '__main__':
    unittest.main()
