#!/usr/bin/env python3
"""Run the real distribution build/cache entry with disposable build collaborators."""
from __future__ import annotations

import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[2]
BUILDER = ROOT / "scripts/build-owned-distribution.sh"


class ExternalSchemaBuildCacheTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="external-schema-build-cache-")
        self.root = Path(self.temporary.name)
        shutil.copyfile(BUILDER, self.write("scripts/build-owned-distribution.sh", ""))
        for directory in (
            "project/src", "owned-framework/src", "owned-personnel/src",
            "owned-equipment/src", "owned-consumables/src", "owned-attachments/src",
            "owned-report/src", "owned-scheduler/src", "owned-web/src",
            "reconstituted/framework", "reconstituted/overrides",
        ):
            (self.root / directory).mkdir(parents=True, exist_ok=True)
        for path in (
            "project/pom.xml", "reconstituted/framework/pom.xml",
            "scripts/stage-source-only-public-deps.py",
            "scripts/prepare-reconstituted-maven-repo.py",
            "scripts/record-reconstituted-framework-build.py",
            "scripts/verify-reconstituted-framework-modules.py",
            "owned-distribution/example/owned-application.properties",
        ):
            self.write(path, "fixture input\n")
        for path in (
            "reconstituted/framework/public-dependency-allowlist.json",
            "docs/reconstitution/decompiler-transforms.json",
        ):
            self.write(path, "{}\n")
        self.write("docs/reconstitution/framework-module-registry.json", '{"modules": []}\n')
        self.write("scripts/render-external-logical-mappers.py", "# renderer version one\n")
        self.write("scripts/verify-external-artifacts.sh", "#!/bin/sh\nexit 0\n", executable=True)
        self.write("scripts/reject-supplier-runtime.sh", "#!/bin/sh\nexit 0\n", executable=True)
        self.write("scripts/build-reconstituted-framework.sh", """#!/bin/sh
set -eu
root=$(cd "$(dirname "$0")/.." && pwd)
printf 'built\n' >> "$root/build/framework-invocations.txt"
""", executable=True)
        self.write("owned-distribution/start.sh", "#!/bin/sh\nexit 0\n", executable=True)
        self.write("jdk/bin/java", '#!/bin/sh\necho "java version fixture-8" >&2\n', executable=True)
        self.write("scripts/build-source-only-application.py", """from pathlib import Path
import argparse
import hashlib
import json

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--public-deps')
parser.add_argument('--external-schema-profile', type=Path)
parser.add_argument('--external-schema-manifest', type=Path)
args = parser.parse_args()
effective_manifest = args.external_schema_manifest
if args.external_schema_profile:
    effective_manifest = effective_manifest or root / 'api-fixtures/schema/external-schema-clone-manifest.json'
inputs = [p for p in (args.external_schema_profile, effective_manifest) if p]
if args.external_schema_profile:
    inputs.append(root / 'scripts/render-external-logical-mappers.py')
payload = json.dumps({str(p): hashlib.sha256(p.read_bytes()).hexdigest() for p in inputs}, sort_keys=True)
output = root / 'build/source-only-application'
output.mkdir(parents=True, exist_ok=True)
(output / 'lims-owned-application.jar').write_text(payload)
(output / 'compile-report.json').write_text('{}\\n')
(output / 'third-party-dependencies.json').write_text('{"entries": []}\\n')
(output / 'third-party-deps').mkdir(exist_ok=True)
print('{}')
""")
        self.write("owned-distribution/package.py", """from pathlib import Path
import json
import shutil
import sys
output = Path(sys.argv[1])
shutil.copyfile(sys.argv[2], output / 'app/lims-owned-application.jar')
(output / 'manifest.json').write_text(json.dumps({'fixture': True}) + '\\n')
""")
        self.profile = self.write("api-fixtures/schema/profile.json", '{"mode": "local-view"}\n')
        self.manifest = self.write(
            "api-fixtures/schema/external-schema-clone-manifest.json", '{"revision": 1}\n')
        self.explicit_manifest = self.write(
            "api-fixtures/schema/explicit-manifest.json", '{"revision": 10}\n')
        self.environment = dict(os.environ)
        for name in ("EXTERNAL_SCHEMA_PROFILE", "EXTERNAL_SCHEMA_MANIFEST", "LIMS_PROJECT_ROOT",
                     "OWNED_PUBLIC_DEPENDENCY_DIR", "OWNED_ADDITIONAL_DEPENDENCY_DIRS"):
            self.environment.pop(name, None)
        self.environment.update(JAVA_HOME=str(self.root / "jdk"),
                                LIMS_PUBLIC_DEPENDENCY_DIR=str(self.root / "public-deps"),
                                OWNED_DISTRIBUTION_REUSE="1")

    def write(self, relative, text, executable=False):
        path = self.root / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text, encoding="utf-8")
        if executable:
            path.chmod(0o755)
        return path

    def tearDown(self):
        # Successful cache entries are intentionally read-only, as in real builds.
        for path in self.root.rglob("*"):
            if not path.is_symlink():
                path.chmod(path.stat().st_mode | 0o700)
        self.temporary.cleanup()

    def build(self, *, explicit=True):
        environment = dict(self.environment, EXTERNAL_SCHEMA_PROFILE=str(self.profile))
        if explicit:
            environment["EXTERNAL_SCHEMA_MANIFEST"] = str(self.explicit_manifest)
        result = subprocess.run(
            ["bash", str(self.root / "scripts/build-owned-distribution.sh")],
            cwd=self.root, env=environment, text=True, capture_output=True, timeout=10)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        key = (self.root / "owned-distribution/target/.distribution-build-fingerprint").read_text().strip()
        distribution = self.root / "owned-distribution/target/distribution"
        self.assertTrue(distribution.is_symlink())
        self.assertEqual(distribution.resolve().parent.name, key)
        return key, (distribution / "app/lims-owned-application.jar").read_bytes(), result.stdout

    def invocation_count(self):
        path = self.root / "build/framework-invocations.txt"
        return len(path.read_text().splitlines()) if path.exists() else 0

    def test_unchanged_explicit_profile_and_manifest_reuse_cached_build(self):
        first_key, first_artifact, first_output = self.build()
        second_key, second_artifact, second_output = self.build()
        self.assertIn("build passed:", first_output)
        self.assertIn("build reused:", second_output)
        self.assertEqual(first_key, second_key)
        self.assertEqual(first_artifact, second_artifact)
        self.assertEqual(self.invocation_count(), 1)

    def test_renderer_change_rebuilds_the_profile_artifact(self):
        first_key, first_artifact, _ = self.build()
        self.write("scripts/render-external-logical-mappers.py", "# renderer version two\n")
        second_key, second_artifact, output = self.build()
        self.assertNotEqual(first_key, second_key)
        self.assertNotEqual(first_artifact, second_artifact)
        self.assertIn("build passed:", output)
        self.assertEqual(self.invocation_count(), 2)

    def test_implicit_effective_manifest_is_forwarded_and_changes_cache_key(self):
        first_key, first_artifact, _ = self.build(explicit=False)
        _, same_artifact, output = self.build(explicit=False)
        self.assertEqual(first_artifact, same_artifact)
        self.assertIn("build reused:", output)
        self.write("api-fixtures/schema/external-schema-clone-manifest.json", '{"revision": 2}\n')
        second_key, second_artifact, output = self.build(explicit=False)
        self.assertNotEqual(first_key, second_key)
        self.assertNotEqual(first_artifact, second_artifact)
        self.assertIn("build passed:", output)
        self.assertEqual(self.invocation_count(), 2)

    def test_manifest_without_profile_fails_before_downstream_build(self):
        environment = dict(self.environment, EXTERNAL_SCHEMA_MANIFEST=str(self.manifest))
        result = subprocess.run(
            ["bash", str(self.root / "scripts/build-owned-distribution.sh")],
            cwd=self.root, env=environment, text=True, capture_output=True, timeout=10)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("EXTERNAL_SCHEMA_PROFILE", result.stderr)
        self.assertEqual(self.invocation_count(), 0)
        self.assertFalse((self.root / "build/source-only-application/compile-report.json").exists())


if __name__ == "__main__":
    unittest.main()
