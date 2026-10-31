#!/usr/bin/env python3
from __future__ import annotations
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TOOLS = ROOT / "tools"
EVIDENCE = ROOT / "evidence"
sys.path.insert(0, str(TOOLS))
import native_api_registry  # noqa: E402


class NativeApiRegistryTest(unittest.TestCase):
    def test_committed_registry_is_complete_and_redacted(self):
        path = EVIDENCE / "native-api-registry.json"
        self.assertTrue(path.is_file())
        registry = json.loads(path.read_text(encoding="utf-8"))
        errors = native_api_registry.validate_registry(registry)
        self.assertEqual(errors, [], "; ".join(errors))
        self.assertEqual(registry["counts"]["native_declarations"], 48)
        self.assertEqual(registry["counts"]["java_native_exports"], 54)
        self.assertEqual(registry["counts"]["native_exports"], 128)
        self.assertGreaterEqual(registry["counts"]["license_deletion_items"], 20)
        serialized = json.dumps(registry, ensure_ascii=False)
        for secret_marker in ("license.lic:", "vendor.lic:", "ORACLE_SYS_PASSWORD", "password="):
            self.assertNotIn(secret_marker, serialized)

    def test_generator_is_deterministic(self):
        with tempfile.TemporaryDirectory() as directory:
            output = Path(directory) / "registry.json"
            subprocess.run(
                [sys.executable, str(TOOLS / "native_api_registry.py"), "--output", str(output)],
                cwd=ROOT,
                check=True,
                text=True,
            )
            expected = json.loads((EVIDENCE / "native-api-registry.json").read_text(encoding="utf-8"))
            actual = json.loads(output.read_text(encoding="utf-8"))
            self.assertEqual(actual, expected)


if __name__ == "__main__":
    unittest.main()
