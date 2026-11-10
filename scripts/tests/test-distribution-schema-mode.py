#!/usr/bin/env python3
"""Exercise the real launcher before Java with bounded package/config fixtures."""
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[2]


class DistributionSchemaModeTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="distribution-schema-mode-")
        self.root = Path(self.temp.name)
        for name in ("app", "provenance", "jdk/bin"):
            (self.root / name).mkdir(parents=True)
        shutil.copy2(ROOT / "owned-distribution/start.sh", self.root / "start.sh")
        self.config = self.root / "runtime.properties"
        self.config.write_text("owned.external-schema.access-mode=local-view\n")
        java = self.root / "jdk/bin/java"
        java.write_text('#!/bin/sh\nprintf launched > "$SCHEMA_TEST_LAUNCH"\n')
        java.chmod(0o755)
        # The JDK archive reader is the same one used by the deployed launcher.
        jar = ROOT / "build/jdk1.8.0_391/bin/jar"
        if not jar.is_file():
            found = shutil.which("jar")
            if not found:
                self.skipTest("JDK jar command unavailable")
            jar = Path(found)
        (self.root / "jdk/bin/jar").symlink_to(jar.resolve())
        self.launch = self.root / "launched"
        self.package("local-view")

    def tearDown(self):
        self.temp.cleanup()

    def package(self, mode):
        raw = (json.dumps({"schema": "external-schema-build-contract-v1", "mode": mode},
                          sort_keys=True) + "\n").encode()
        props = ("mode=" + mode + "\ncontract_sha256=" + hashlib.sha256(raw).hexdigest() + "\n").encode()
        with zipfile.ZipFile(self.root / "app/lims-owned-application.jar", "w") as jar:
            jar.writestr("META-INF/lims-external-schema.json", raw)
            jar.writestr("META-INF/lims-external-schema.properties", props)
        (self.root / "provenance/external-schema.properties").write_bytes(props)

    def run_launcher(self):
        environment = dict(os.environ, JAVA_HOME=str(self.root / "jdk"),
                           SCHEMA_TEST_LAUNCH=str(self.launch))
        return subprocess.run(["bash", str(self.root / "start.sh"), str(self.config)],
                              capture_output=True, text=True, env=environment)

    def reject(self):
        result = self.run_launcher()
        self.assertNotEqual(result.returncode, 0)
        self.assertFalse(self.launch.exists(), "mismatched artifact reached Java")
        self.assertIn("external-schema", result.stderr)

    def test_matching_local_view_starts_java(self):
        result = self.run_launcher()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertTrue(self.launch.exists())

    def test_other_explicit_modes_and_properties_whitespace(self):
        for mode in ("original-db-link", "direct-schema"):
            with self.subTest(mode=mode):
                self.package(mode)
                self.config.write_text("# mode is explicit\n owned.external-schema.access-mode : " + mode + "\r\n")
                result = self.run_launcher()
                self.assertEqual(result.returncode, 0, result.stderr)

    def test_plain_rebuild_is_rejected_by_local_view_runtime(self):
        self.package("original-db-link")
        self.reject()

    def test_missing_runtime_choice_is_rejected(self):
        self.config.write_text("server.port=18087\n")
        self.reject()

    def test_duplicate_or_unknown_runtime_choice_is_rejected(self):
        for value in ("local-view\nowned.external-schema.access-mode=local-view", "guess"):
            with self.subTest(value=value):
                self.config.write_text("owned.external-schema.access-mode=" + value + "\n")
                self.reject()

    def test_missing_or_tampered_package_metadata_is_rejected(self):
        (self.root / "provenance/external-schema.properties").unlink()
        self.reject()
        self.package("local-view")
        (self.root / "provenance/external-schema.properties").write_text(
            "mode=original-db-link\ncontract_sha256=" + "a" * 64 + "\n")
        self.reject()

    def test_embedded_json_change_is_rejected(self):
        with zipfile.ZipFile(self.root / "app/lims-owned-application.jar", "w") as jar:
            jar.writestr("META-INF/lims-external-schema.json", b'{}\n')
            jar.writestr("META-INF/lims-external-schema.properties",
                         (self.root / "provenance/external-schema.properties").read_bytes())
        self.reject()

    def test_manifest_without_profile_is_rejected_before_compilation(self):
        environment = dict(os.environ, EXTERNAL_SCHEMA_MANIFEST="unneeded.json")
        environment.pop("EXTERNAL_SCHEMA_PROFILE", None)
        result = subprocess.run(["bash", str(ROOT / "scripts/build-owned-distribution.sh")],
                                env=environment, text=True, capture_output=True, timeout=10)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("EXTERNAL_SCHEMA_PROFILE", result.stderr)


if __name__ == "__main__":
    unittest.main()
