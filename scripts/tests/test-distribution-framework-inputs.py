#!/usr/bin/env python3
"""Run the real distribution entry with a failed framework registry reader."""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]


class FrameworkInputsTest(unittest.TestCase):
    def invoke(self, jq_source):
        with tempfile.TemporaryDirectory(prefix="distribution-framework-inputs-") as temporary:
            root = Path(temporary)
            (root / "scripts").mkdir()
            (root / "bin").mkdir()
            (root / "jdk/bin").mkdir(parents=True)
            shutil.copy2(ROOT / "scripts/build-owned-distribution.sh", root / "scripts")
            registry = root / "docs/reconstitution/framework-module-registry.json"
            registry.parent.mkdir(parents=True)
            registry.write_text('{"modules":[{"output_jar":"owned-core-common/target/module.jar"}]}\n')
            jq = root / "bin/jq"
            jq.write_text(jq_source)
            jq.chmod(0o755)
            java = root / "jdk/bin/java"
            java.write_text('#!/bin/sh\nprintf \'java version "1.8.0_test"\\n\' >&2\n')
            java.chmod(0o755)
            # If preflight fails to stop the real entry, it reaches the first
            # build command. That command records an unwanted compilation.
            build = root / "scripts/verify-external-artifacts.sh"
            build.write_text('#!/bin/sh\nprintf compiled > "$FRAMEWORK_TEST_COMPILED"\nexit 97\n')
            build.chmod(0o755)
            compiled = root / "compiled"
            environment = dict(os.environ, PATH=str(root / "bin") + ":" + os.environ["PATH"],
                               JAVA_HOME=str(root / "jdk"), FRAMEWORK_TEST_COMPILED=str(compiled))
            for key in ("EXTERNAL_SCHEMA_MANIFEST", "EXTERNAL_SCHEMA_PROFILE", "OWNED_DISTRIBUTION_REUSE"):
                environment.pop(key, None)
            result = subprocess.run(["bash", str(root / "scripts/build-owned-distribution.sh")],
                                    env=environment, text=True, capture_output=True, timeout=10)
            self.assertNotEqual(result.returncode, 0)
            self.assertFalse(compiled.exists(), "failed registry reader reached compilation")
            self.assertFalse((root / "owned-distribution/target").exists(),
                             "failed registry reader created or activated distribution output")
            self.assertIn("framework module registry", result.stderr)

    def test_reader_failure_stops_before_build_even_after_partial_output(self):
        self.invoke('#!/bin/sh\nprintf "owned-core-common/target/module.jar\\n"\nexit 42\n')

    def test_empty_registry_output_stops_before_build(self):
        self.invoke('#!/bin/sh\nexit 0\n')

    def test_missing_reader_stops_before_build(self):
        self.invoke('#!/bin/sh\nprintf "jq: command not found\\n" >&2\nexit 127\n')


if __name__ == "__main__":
    unittest.main()
