#!/usr/bin/env python3
import json
import subprocess
import sys
import tempfile
import unittest
from unittest import mock
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
TOOLS = ROOT / "tools"
sys.path.insert(0, str(TOOLS))
import native_api_handoff


class NativeApiHandoffTest(unittest.TestCase):
    def test_family_reports_are_aggregated_and_unknown_is_diagnostic(self):
        registry = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json").read_text())
        report = native_api_handoff.build_report(registry)
        self.assertIn(report["status"], {"executed-pass", "executed-fail"})
        self.assertGreater(report["required_item_count"], 0)
        expected = {
            item["id"]
            for item in native_api_handoff.native_api_coverage.required_items(registry)
        }
        result_ids = {item["registry_id"] for item in report["results"]}
        diagnostic_ids = {item["registry_id"] for item in report["diagnostics"]}
        # One item may have a result plus provenance diagnostics (for example,
        # an older family report whose source scope changed). Count IDs rather
        # than rows so those annotations do not masquerade as extra coverage.
        self.assertEqual(result_ids | diagnostic_ids, expected)
        self.assertTrue(result_ids <= expected)
        self.assertTrue(all("registry_id" in item for item in report["diagnostics"]))

    def test_deferred_third_party_inputs_are_not_required(self):
        registry = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json").read_text())
        required = native_api_handoff.native_api_coverage.required_items(registry)
        self.assertFalse(any(
            item.get("classification") == "retain-for-PageOffice-until-authority-review"
            for item in required
        ))

    def test_handoff_uses_derived_source_state(self):
        registry = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json").read_text())
        with mock.patch.object(
            native_api_handoff.native_api_coverage,
            "git_source_state",
            return_value=("derived-head", True),
        ), mock.patch.object(
            native_api_handoff,
            "family_source_scope_proof",
            return_value={"verified": True, "family_source_commit": "derived-head"},
        ):
            report = native_api_handoff.build_report(registry)
        self.assertEqual(report["source_commit"], "derived-head")
        self.assertTrue(report["source_dirty"])

    def test_auto_classification_is_rejected_even_with_generic_evidence(self):
        registry = json.loads((ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json").read_text())
        report = native_api_handoff.build_report(registry)
        report["results"][0]["auto_classified"] = True
        errors = native_api_handoff.native_api_coverage.validate_coverage(registry, report)
        self.assertTrue(any("auto-classified result is not an explicit item contract" in error for error in errors))

    def test_family_scope_proof_is_available_for_current_head(self):
        current = native_api_handoff.source_commit()
        proof = native_api_handoff.family_source_scope_proof(current, current)
        self.assertTrue(proof["verified"])


if __name__ == "__main__":
    unittest.main()

