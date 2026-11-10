#!/usr/bin/env python3
"""Exercise the package determinism gate with an immutable distribution."""
import importlib.util
import json
import os
from pathlib import Path
import tempfile
import unittest
import zipfile
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location('package_gate', Path(__file__).with_name('test-package.py'))
gate = importlib.util.module_from_spec(spec)
spec.loader.exec_module(gate)


class PackageIsolationTest(unittest.TestCase):
    def write_snapshot_source(self, root):
        for name in ('project', 'reconstituted', 'scripts', 'docs', 'build-support',
                     'owned-distribution', 'api-fixtures/schema'):
            directory = root / name
            directory.mkdir(parents=True)
            (directory / 'source.txt').write_text('source input\n')
        schema = root / 'api-fixtures/schema'
        (schema / 'profile.json').write_text('{"mode":"local-view"}\n')
        (schema / 'external-schema-clone-manifest.json').write_text('{"domains": []}\n')
        (root / 'project/target').mkdir()
        (root / 'project/target/generated.txt').write_text('must not copy\n')
        (root / 'api-fixtures/private-fixture.json').write_text('not a schema input\n')

    def test_profiled_snapshot_uses_its_own_in_repository_schema_inputs(self):
        with tempfile.TemporaryDirectory(prefix='profiled-package-isolation-') as temporary:
            root = Path(temporary)
            source = root / 'repository'
            snapshot = root / 'snapshot'
            self.write_snapshot_source(source)
            profile = source / 'api-fixtures/schema/profile.json'
            manifest = source / 'api-fixtures/schema/external-schema-clone-manifest.json'
            environment = gate.prepare_build_snapshot(source, snapshot, {
                'EXTERNAL_SCHEMA_PROFILE': str(profile),
                'EXTERNAL_SCHEMA_MANIFEST': str(manifest),
            })
            for key, original in (('EXTERNAL_SCHEMA_PROFILE', profile),
                                  ('EXTERNAL_SCHEMA_MANIFEST', manifest)):
                copied = Path(environment[key])
                self.assertEqual(copied, snapshot / original.relative_to(source))
                self.assertEqual(copied.read_bytes(), original.read_bytes())
            self.assertEqual(environment['LIMS_PROJECT_ROOT'], str(snapshot / 'project'))
            self.assertEqual(environment['OWNED_DISTRIBUTION_REUSE'], '0')
            self.assertTrue((snapshot / 'api-fixtures/schema/source.txt').is_file())
            self.assertFalse((snapshot / 'api-fixtures/private-fixture.json').exists())
            self.assertFalse((snapshot / 'project/target').exists())
            profile.write_text('{"mode":"direct-schema"}\n')
            self.assertEqual(json.loads(Path(environment['EXTERNAL_SCHEMA_PROFILE']).read_text())['mode'], 'local-view')

    def test_profile_only_uses_snapshot_default_manifest_and_copies_selected_private_file(self):
        with tempfile.TemporaryDirectory(prefix='profile-package-default-') as temporary:
            root = Path(temporary)
            source = root / 'repository'
            snapshot = root / 'snapshot'
            self.write_snapshot_source(source)
            profile = source / 'private/profile.json'
            profile.parent.mkdir()
            profile.write_text('{"mode":"local-view"}\n')
            (profile.parent / 'unrelated-secret.txt').write_text('not selected\n')
            environment = gate.prepare_build_snapshot(source, snapshot, {
                'EXTERNAL_SCHEMA_PROFILE': str(profile),
            })
            self.assertNotIn('EXTERNAL_SCHEMA_MANIFEST', environment)
            self.assertEqual(environment['EXTERNAL_SCHEMA_PROFILE'], str(snapshot / 'private/profile.json'))
            self.assertEqual(Path(environment['EXTERNAL_SCHEMA_PROFILE']).read_bytes(), profile.read_bytes())
            self.assertFalse((snapshot / 'private/unrelated-secret.txt').exists())
            self.assertTrue((snapshot / 'api-fixtures/schema/external-schema-clone-manifest.json').is_file())

    def test_explicit_external_inputs_remain_external_and_missing_input_fails(self):
        with tempfile.TemporaryDirectory(prefix='external-package-inputs-') as temporary:
            root = Path(temporary)
            source = root / 'repository'
            self.write_snapshot_source(source)
            profile = root / 'profile.json'
            manifest = root / 'manifest.json'
            profile.write_text('{"mode":"local-view"}\n')
            manifest.write_text('{"domains": []}\n')
            environment = gate.prepare_build_snapshot(source, root / 'snapshot', {
                'EXTERNAL_SCHEMA_PROFILE': str(profile),
                'EXTERNAL_SCHEMA_MANIFEST': str(manifest),
            })
            self.assertEqual(environment['EXTERNAL_SCHEMA_PROFILE'], str(profile.resolve()))
            self.assertEqual(environment['EXTERNAL_SCHEMA_MANIFEST'], str(manifest.resolve()))
            self.assertFalse((root / 'snapshot/profile.json').exists())
            with self.assertRaisesRegex(AssertionError, 'EXTERNAL_SCHEMA_PROFILE.*missing'):
                gate.prepare_build_snapshot(source, root / 'missing-snapshot', {
                    'EXTERNAL_SCHEMA_PROFILE': str(root / 'missing.json'),
                })

    def test_repackage_leaves_readonly_distribution_and_link_unchanged(self):
        with tempfile.TemporaryDirectory(prefix='package-isolation-test-') as temporary:
            root = Path(temporary)
            source = root / 'application.jar'
            with zipfile.ZipFile(source, 'w') as archive:
                archive.writestr('example/Main.class', b'fixture application')
            dependencies = root / 'public-deps'
            dependencies.mkdir()
            environment = dict(os.environ, OWNED_PUBLIC_DEPENDENCY_DIR=str(dependencies))
            environment.pop('OWNED_ADDITIONAL_DEPENDENCY_DIRS', None)
            environment.pop('OWNED_THIRD_PARTY_DEPENDENCY_PROVENANCE', None)
            cached = root / 'cache/distribution'
            cached.mkdir(parents=True)
            (cached / 'provenance').mkdir()
            (cached / 'provenance/compile.json').write_text('{"status":"pass"}\n')
            (cached / 'start.sh').write_text('#!/bin/sh\nexit 0\n')
            (cached / 'start.sh').chmod(0o755)
            result = subprocess.run([
                sys.executable, str(ROOT / 'owned-distribution/package.py'),
                str(cached), str(source),
            ], env=environment, capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stderr)
            live = root / 'distribution'
            live.symlink_to('cache/distribution')
            for path in cached.rglob('*'):
                path.chmod(path.stat().st_mode & ~0o222)
            cached.chmod(0o555)
            before = {str(path.relative_to(cached)): (path.read_bytes(), path.stat().st_mode,
                      path.stat().st_mtime_ns) for path in cached.rglob('*') if path.is_file()}
            try:
                gate.verify_package_determinism(
                    ROOT, live, [source], environment,
                )
                after = {str(path.relative_to(cached)): (path.read_bytes(), path.stat().st_mode,
                         path.stat().st_mtime_ns) for path in cached.rglob('*') if path.is_file()}
                self.assertEqual(before, after)
                self.assertEqual(os.readlink(live), 'cache/distribution')
                # Matching application bytes alone must not hide drift in
                # another shipped file.
                classpath = cached / 'lib/classpath.txt'
                classpath.chmod(0o644)
                classpath.write_text('unexpected.jar\n')
                classpath.chmod(0o444)
                with self.assertRaisesRegex(AssertionError, 'distribution differs after repackaging'):
                    gate.verify_package_determinism(ROOT, live, [source], environment)
            finally:
                cached.chmod(0o755)
                for path in cached.rglob('*'):
                    path.chmod(path.stat().st_mode | 0o200)


if __name__ == '__main__':
    unittest.main()
