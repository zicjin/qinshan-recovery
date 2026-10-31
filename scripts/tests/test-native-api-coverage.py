#!/usr/bin/env python3
from __future__ import annotations

import copy
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

import native_api_coverage  # noqa: E402


class NativeApiCoverageTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.registry = json.loads((EVIDENCE / "native-api-registry.json").read_text(encoding="utf-8"))

    def complete_report(self):
        report = native_api_coverage.new_report(
            self.registry,
            source_commit="test-commit",
            source_dirty=False,
        )
        for item in native_api_coverage.required_items(self.registry):
            report["results"].append({
                "registry_id": item["id"],
                "status": "removed-negative-pass" if item["scope"] == "sunway-license" else "owned-pass",
                "execution": "http-contract" if item["kind"].endswith("route") else "java-contract",
                "evidence": "test/evidence.json",
                "observed": {"result": "independent-test-observation"},
            })
        report["status"] = "executed-pass"
        return report

    def test_complete_report_passes(self):
        report = self.complete_report()
        self.assertEqual(native_api_coverage.validate_coverage(self.registry, report), [])

    def test_current_registry_is_blocked_by_unknown_items(self):
        report = native_api_coverage.new_report(
            self.registry,
            source_commit="test-commit",
            source_dirty=False,
        )
        report["results"].append({
            "registry_id": native_api_coverage.required_items(self.registry)[0]["id"],
            "status": "unknown",
            "execution": "java-contract",
            "evidence": "test/evidence.json",
            "observed": {"result": "not-executed"},
        })
        errors = native_api_coverage.validate_coverage(self.registry, report)
        self.assertTrue(any("missing result" in error for error in errors))
        self.assertTrue(any("unknown" in error for error in errors))

    def test_missing_result_is_rejected(self):
        report = self.complete_report()
        report["results"].pop()
        errors = native_api_coverage.validate_coverage(self.registry, report)
        self.assertTrue(any("missing result" in error for error in errors))

    def test_unregistered_result_is_rejected(self):
        report = self.complete_report()
        report["results"].append({
            "registry_id": "external-native:not-registered",
            "status": "owned-pass",
            "execution": "java-contract",
            "evidence": "test/evidence.json",
            "observed": {"result": "independent-test-observation"},
        })
        errors = native_api_coverage.validate_coverage(self.registry, report)
        self.assertTrue(any("unregistered result" in error for error in errors))

    def test_static_only_and_unknown_results_are_rejected(self):
        report = self.complete_report()
        report["results"][0]["execution"] = "static-inventory"
        report["results"][1]["status"] = "unknown"
        errors = native_api_coverage.validate_coverage(self.registry, report)
        self.assertTrue(any("static-only" in error for error in errors))
        self.assertTrue(any("unknown status" in error for error in errors))

    def test_metadata_and_cleanup_are_required(self):
        report = self.complete_report()
        report["production_access"] = True
        report["cleanup"] = {"fixture_restore": False}
        errors = native_api_coverage.validate_coverage(self.registry, report)
        self.assertTrue(any("production_access" in error for error in errors))
        self.assertTrue(any("cleanup" in error for error in errors))

    def test_cli_writes_diagnostic_for_current_unclosed_registry(self):
        with tempfile.TemporaryDirectory() as directory:
            output = Path(directory) / "coverage.json"
            result = subprocess.run(
                [
                    sys.executable,
                    str(TOOLS / "native_api_coverage.py"),
                    "--registry",
                    str(EVIDENCE / "native-api-registry.json"),
                    "--source-commit",
                    "test-commit",
                    "--output",
                    str(output),
                ],
                cwd=ROOT,
                text=True,
                capture_output=True,
            )
            self.assertEqual(result.returncode, 1)
            diagnostic = json.loads(output.read_text(encoding="utf-8"))
            self.assertEqual(diagnostic["status"], "executed-fail")
            self.assertTrue(diagnostic["errors"])


if __name__ == "__main__":
    unittest.main()
