#!/usr/bin/env python3
"""Package the real public Oracle inputs with a tiny temporary application JAR."""
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[2]


class InstallationBundleTest(unittest.TestCase):
    def test_source_only_package_ships_independent_guarded_installation(self):
        with tempfile.TemporaryDirectory(prefix='installation-bundle-') as temporary:
            root = Path(temporary)
            source = root / 'application.jar'
            mappers = [
                'com/sunwayworld/emc/module/bp/project/project/mapper/conf/pf-project-oracle-mapper.xml',
                'com/sunwayworld/emc/module/mdm/contract/mapper/conf/emc-contract-oracle-mapper.xml',
            ]
            contract = {'schema': 'external-schema-build-contract-v1', 'mode': 'original-db-link',
                        'profile': None, 'profile_sha256': None, 'manifest': None, 'manifest_sha256': None,
                        'renderer': None, 'renderer_sha256': None,
                        'mappers': [{'path': name, 'sha256': hashlib.sha256(b'<mapper/>').hexdigest()} for name in mappers]}
            raw = json.dumps(contract).encode()
            with zipfile.ZipFile(source, 'w') as archive:
                archive.writestr('example/Main.class', b'fixture application')
                for name in mappers:
                    archive.writestr(name, b'<mapper/>')
                archive.writestr('META-INF/lims-external-schema.json', raw)
                archive.writestr('META-INF/lims-external-schema.properties',
                                 'mode=original-db-link\ncontract_sha256=' + hashlib.sha256(raw).hexdigest() + '\n')
            dependencies = root / 'dependencies'
            dependencies.mkdir()
            output = root / 'distribution'
            environment = dict(os.environ, OWNED_PUBLIC_DEPENDENCY_DIR=str(dependencies),
                               OWNED_ADDITIONAL_DEPENDENCY_DIRS='', OWNED_THIRD_PARTY_DEPENDENCY_PROVENANCE='',
                               OWNED_DISTRIBUTION_MODE='source-only-reconstituted')
            result = subprocess.run([sys.executable, str(ROOT / 'owned-distribution/package.py'), str(output), str(source)],
                                    env=environment, capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
            installation = output / 'installation'
            self.assertTrue((installation / 'README.md').is_file())
            manifest = json.loads((output / 'manifest.json').read_text())
            self.assertFalse(manifest['installation']['deployment_ready'])
            self.assertEqual(manifest['installation']['status'], 'partial')
            workflow_manifest = json.loads((installation / 'deployment/oracle/workflow/manifest.json').read_text())
            workflow_definitions = json.loads((installation / 'deployment/oracle/workflow/definitions.json').read_text())
            self.assertEqual(workflow_manifest['schema'], 'owned-workflow-package-v1')
            self.assertEqual(workflow_definitions['schema'], 'owned-workflow-definitions-v1')
            self.assertEqual(manifest['installation']['workflow']['counts'], workflow_manifest['counts'])
            self.assertFalse(manifest['installation']['workflow']['deployment_ready'])
            self.assertEqual(workflow_manifest['definitions']['sha256'],
                             hashlib.sha256((installation / 'deployment/oracle/workflow/definitions.json').read_bytes()).hexdigest())
            self.assertEqual(sorted(path.name for path in (installation / 'deployment/oracle/workflow').iterdir()),
                             ['README.md', 'definitions.json', 'manifest.json'])
            self.assertEqual(manifest['installation']['import_templates']['assets'], 1)
            self.assertEqual(manifest['installation']['import_templates']['blocked'], 1)
            asset_script = installation / 'scripts/install-import-template-assets.py'
            asset_check = subprocess.run([sys.executable, str(asset_script), '--check-package'],
                                         cwd=installation, capture_output=True, text=True)
            self.assertEqual(asset_check.returncode, 0, asset_check.stdout + asset_check.stderr)
            self.assertFalse(json.loads(asset_check.stdout)['deployment_ready'])
            for name in ('core-schema', 'external-schema'):
                relative = 'installation/deployment/oracle/' + name + '/manifest.json'
                self.assertEqual(manifest['files'][relative], hashlib.sha256((output / relative).read_bytes()).hexdigest())
            for path, expected in manifest['files'].items():
                self.assertEqual(hashlib.sha256((output / path).read_bytes()).hexdigest(), expected)
            self.assertFalse((installation / 'deployment/oracle/README.md').exists())
            self.assertFalse((installation / 'deployment/oracle/core-schema/README.md').exists())
            self.assertFalse((installation / 'private').exists())
            self.assertFalse((installation / 'project').exists())
            self.assertFalse((installation / 'api-fixtures').exists())
            before = {path.relative_to(output).as_posix(): hashlib.sha256(path.read_bytes()).hexdigest()
                      for path in output.rglob('*') if path.is_file()}
            stale = installation / 'unlisted-old-file.sql'
            stale.write_text('stale installation output\n')
            repackaged = subprocess.run(
                [sys.executable, str(ROOT / 'owned-distribution/package.py'), str(output), str(source)],
                env=environment, capture_output=True, text=True)
            self.assertEqual(repackaged.returncode, 0, repackaged.stdout + repackaged.stderr)
            self.assertFalse(stale.exists())
            self.assertEqual(before, {path.relative_to(output).as_posix(): hashlib.sha256(path.read_bytes()).hexdigest()
                                     for path in output.rglob('*') if path.is_file()})
            isolated = root / 'relocated-installation'
            shutil.copytree(installation, isolated)
            isolated_before = {path.relative_to(isolated).as_posix(): hashlib.sha256(path.read_bytes()).hexdigest()
                               for path in isolated.rglob('*') if path.is_file()}
            script = isolated / 'scripts/verify-combined-oracle-installation.py'
            for arguments, expected_exit in ((['--check-plan'], 0), ([], 2)):
                result = subprocess.run([sys.executable, str(script), *arguments], cwd=isolated,
                                        capture_output=True, text=True)
                self.assertEqual(result.returncode, expected_exit, result.stdout + result.stderr)
                report = json.loads(result.stdout)
                self.assertTrue(report['plan_valid'])
                self.assertFalse(report['deployment_ready'])
                self.assertEqual(isolated_before, {path.relative_to(isolated).as_posix(): hashlib.sha256(path.read_bytes()).hexdigest()
                                                   for path in isolated.rglob('*') if path.is_file()})
            child = isolated / 'deployment/oracle/99-verify.sql'
            child.write_bytes(child.read_bytes() + b'-- changed\n')
            result = subprocess.run([sys.executable, str(script), '--check-plan'], cwd=isolated,
                                    capture_output=True, text=True)
            self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
            self.assertFalse(json.loads(result.stdout)['plan_valid'])
            shutil.rmtree(installation)
            outside = root / 'outside-installation'
            outside.mkdir()
            sentinel = outside / 'existing.txt'
            sentinel.write_bytes(b'preserve outside bytes')
            installation.symlink_to(outside, target_is_directory=True)
            rejected = subprocess.run(
                [sys.executable, str(ROOT / 'owned-distribution/package.py'), str(output), str(source)],
                env=environment, capture_output=True, text=True)
            self.assertNotEqual(rejected.returncode, 0, rejected.stdout + rejected.stderr)
            self.assertIn('installation bundle integrity failed', rejected.stderr)
            self.assertTrue(installation.is_symlink())
            self.assertEqual(sentinel.read_bytes(), b'preserve outside bytes')
            self.assertEqual([path.name for path in outside.iterdir()], ['existing.txt'])

    def test_workflow_layout_pin_and_counts_are_checked_without_private_sources(self):
        spec = importlib.util.spec_from_file_location('installation_packager', ROOT / 'owned-distribution/package.py')
        packager = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(packager)
        with tempfile.TemporaryDirectory(prefix='workflow-bundle-boundary-') as temporary:
            checkout = Path(temporary) / 'source'
            shutil.copytree(ROOT / 'deployment/oracle', checkout / 'deployment/oracle')
            (checkout / 'scripts').mkdir()
            for name in ('verify-core-schema-package.py', 'verify-combined-oracle-installation.py',
                         'install-import-template-assets.py'):
                shutil.copy2(ROOT / 'scripts' / name, checkout / 'scripts' / name)
            (checkout / 'owned-distribution').mkdir()
            shutil.copy2(ROOT / 'owned-distribution/installation-README.md', checkout / 'owned-distribution/installation-README.md')
            packager.REPO_ROOT = checkout
            output = Path(temporary) / 'distribution'
            result = packager.package_installation(output)
            self.assertFalse(result['workflow']['deployment_ready'])
            self.assertFalse((checkout / 'private').exists())
            self.assertFalse((checkout / 'project').exists())
            asset_manifest_path = checkout / 'deployment/oracle/import-templates/manifest.json'
            asset_original = asset_manifest_path.read_bytes()
            changed = json.loads(asset_original)
            changed['assets'][0]['relative_path'] = changed['assets'][0]['relative_path'].replace('.xlsx', '-changed.xlsx')
            asset_manifest_path.write_text(json.dumps(changed))
            try:
                # Internally valid file metadata must still match the SQL bootstrap pin.
                with self.assertRaisesRegex(SystemExit, 'installation bundle integrity failed'):
                    packager.package_installation(output)
            finally:
                asset_manifest_path.write_bytes(asset_original)
            path = checkout / 'deployment/oracle/workflow/manifest.json'
            original = path.read_bytes()
            for change in ('schema', 'ready', 'path', 'hash', 'bytes', 'count', 'extra-count', 'reference-list', 'reference-extra'):
                with self.subTest(change=change):
                    manifest = json.loads(original)
                    if change == 'schema':manifest['schema'] = 'unknown'
                    elif change == 'ready':manifest['deployment_ready'] = True
                    elif change == 'path':manifest['definitions']['path'] = '../private/definitions.json'
                    elif change == 'hash':manifest['definitions']['sha256'] = '0' * 64
                    elif change == 'bytes':manifest['definitions']['bytes'] += 1
                    elif change == 'count':manifest['counts']['definitions'] += 1
                    elif change == 'extra-count':manifest['counts']['extra'] = 0
                    elif change == 'reference-list':manifest['definitions'] = []
                    else:manifest['definitions']['unreviewed'] = None
                    path.write_text(json.dumps(manifest))
                    try:
                        with self.assertRaisesRegex(SystemExit, 'installation bundle integrity failed'):
                            packager.package_installation(output)
                    finally:
                        path.write_bytes(original)
            definitions = checkout / 'deployment/oracle/workflow/definitions.json'
            definitions_original = definitions.read_bytes()
            for invalid in ([], dict(json.loads(definitions_original), unexpected=[])):
                with self.subTest(definitions_shape=type(invalid).__name__):
                    definitions.write_text(json.dumps(invalid))
                    try:
                        with self.assertRaisesRegex(SystemExit, 'installation bundle integrity failed'):
                            packager.package_installation(output)
                    finally:
                        definitions.write_bytes(definitions_original)
            saved = definitions.read_bytes()
            target = Path(temporary) / 'outside-definitions.json'
            target.write_bytes(saved)
            definitions.unlink()
            definitions.symlink_to(target)
            with self.assertRaisesRegex(SystemExit, 'installation bundle integrity failed'):
                packager.package_installation(output)
            self.assertEqual(target.read_bytes(), saved)


if __name__ == '__main__':
    unittest.main()
