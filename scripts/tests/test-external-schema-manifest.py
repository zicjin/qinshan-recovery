#!/usr/bin/env python3
import json
from pathlib import Path
import subprocess
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-external-schema-manifest.py"


class ExternalSchemaManifestTest(unittest.TestCase):
    def test_manifest_gate_passes_as_blocked_inventory(self):
        result = subprocess.run(["python3", str(SCRIPT)], text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"status": "pass"', result.stdout)

    def test_manifest_does_not_claim_clone_ready(self):
        manifest = json.loads((ROOT / "api-fixtures/schema/external-schema-clone-manifest.json").read_text())
        self.assertEqual(manifest["status"], "environment-not-ready-external-schema")
        self.assertFalse(manifest["strict_evidence"])
        self.assertFalse(manifest["business_rows_read"])
        self.assertFalse(manifest["writes"])


if __name__ == "__main__":
    unittest.main()
