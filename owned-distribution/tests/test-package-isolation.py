#!/usr/bin/env python3
"""Exercise the package determinism gate with an immutable distribution."""
import importlib.util
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
