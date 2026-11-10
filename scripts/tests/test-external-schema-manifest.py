#!/usr/bin/env python3
import json
from pathlib import Path
import subprocess
import tempfile
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

    def test_db_link_is_provenance_and_logical_clone_modes_are_explicit(self):
        manifest = json.loads((ROOT / "api-fixtures/schema/external-schema-clone-manifest.json").read_text())
        self.assertEqual(manifest["production_reference"]["access_mechanism"], "oracle-db-link")
        self.assertFalse(manifest["production_reference"]["required_for_clone"])
        contract = manifest["clone_contract"]
        self.assertFalse(contract["db_link_transport_required"])
        self.assertEqual(set(contract["allowed_access_modes"]), {"direct-schema", "local-view"})
        for domain in manifest["domains"]:
            self.assertEqual(domain["clone_access"]["selected_mode"], None)
            self.assertEqual(set(domain["clone_access"]["allowed_modes"]), {"direct-schema", "local-view"})

    def test_unknown_clone_mode_is_rejected(self):
        manifest = json.loads((ROOT / "api-fixtures/schema/external-schema-clone-manifest.json").read_text())
        manifest["domains"][0]["clone_access"]["selected_mode"] = "db-link"
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "manifest.json"
            path.write_text(json.dumps(manifest), encoding="utf-8")
            result = subprocess.run(["python3", str(SCRIPT), "--manifest", str(path)], text=True, capture_output=True)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("clone selected mode is invalid", result.stderr + result.stdout)


if __name__ == "__main__":
    unittest.main()
