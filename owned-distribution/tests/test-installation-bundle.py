#!/usr/bin/env python3
"""Package the real public Oracle inputs with a tiny temporary application JAR."""
import hashlib
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


if __name__ == '__main__':
    unittest.main()
