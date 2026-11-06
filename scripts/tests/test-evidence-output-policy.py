#!/usr/bin/env python3
import importlib.util
import os
from pathlib import Path
import tempfile
import unittest
from unittest import mock

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("evidence_output", ROOT / "scripts" / "evidence_output.py")
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(MODULE)


class EvidenceOutputPolicyTest(unittest.TestCase):
    def test_surface_bindings_prefer_dependency_face_over_aggregate(self):
        current = MODULE.current_implementation_binding()
        payload = {
            surface + "_binding": current[surface + "_binding"]
            for surface in ("application", "harness", "runtime_contract", "planning")
        }
        payload.update({
            surface + "_dirty": False
            for surface in ("application", "harness", "runtime_contract", "planning")
        })
        self.assertTrue(MODULE.implementation_binding_valid(payload, require_clean=False))

        payload["application_binding"] = "app-stale"
        self.assertFalse(MODULE.implementation_binding_valid(payload, require_clean=False))

        payload["application_binding"] = current["application_binding"]
        payload["application_dirty"] = True
        self.assertFalse(MODULE.implementation_binding_valid(payload, require_clean=True))

    def test_schema_two_strict_case_requires_fingerprints(self):
        case = {
            "status": 200,
            "route": {"method": "GET", "path": "/ok"},
            "fixture": {"required": False, "status": "not-required"},
            "audit": {"required": False, "status": "not-required"},
        }
        ok, reason = MODULE.strict_case_contract({"schema": 2, "cases": [case]})
        self.assertFalse(ok)
        self.assertIn("request_fingerprint", reason)

        case.update({
            "request_fingerprint": "req-" + "a" * 64,
            "stimulus_contract_version": "3",
            "response_fingerprint": "res-" + "b" * 64,
            "normalizer_version": "2",
            "validation_mode": "executed",
            "batch_id": "batch-1",
            "application_binding": "app-" + "c" * 16,
            "harness_binding": "harness-" + "d" * 16,
            "runtime_contract_binding": "rt-" + "e" * 16,
            "family_fingerprint": "fam-" + "f" * 64,
        })
        ok, reason = MODULE.strict_case_contract({"schema": 2, "cases": [case]})
        self.assertTrue(ok, reason)

    def test_canonical_requires_finalize_context(self):
        with self.assertRaises(ValueError):
            MODULE.guard_output_path(MODULE.CANONICAL_ROOT / "unapproved.json")

    def test_environment_variable_cannot_bypass_publication_guard(self):
        with mock.patch.dict(os.environ, {"EVIDENCE_FINALIZE": "1"}):
            with self.assertRaises(ValueError):
                MODULE.guard_output_path(MODULE.CANONICAL_ROOT / "still-unapproved.json")

    def test_diagnostic_path_is_allowed(self):
        with tempfile.TemporaryDirectory() as temp:
            MODULE.guard_output_path(Path(temp) / "diagnostic.json")

    def test_api_runners_use_guard_and_diagnostic_runtime_defaults(self):
        scripts = ROOT / "scripts"
        for name in ("spring_api_contract_runner.py", "fast_differential_api_runner.py",
                     "frontend-baseline-readonly.py", "frontend-authenticated-baseline-sss2.py"):
            text = (scripts / name).read_text(encoding="utf-8")
            self.assertIn("guard_output_path", text, name)
        closure = (ROOT / "tools" / "api_route_closure.py").read_text(encoding="utf-8")
        self.assertIn("guard_output_path", closure)
        for name in ("api_route_ledger.py", "api_route_execution_plan.py",
                     "fast_contract_skeleton.py", "business_api_inventory.py"):
            text = (ROOT / "tools" / name).read_text(encoding="utf-8")
            self.assertIn("guard_output_path", text, name)
        self.assertIn("guard_output_path", (scripts / "frontend-source-runtime-inventory.py").read_text(encoding="utf-8"))
        self.assertIn("guard_output_path", (ROOT / "tools" / "finalize_api_report.py").read_text(encoding="utf-8"))
        orchestrator = (scripts / "api_wave_orchestrator.py").read_text(encoding="utf-8")
        self.assertIn("DIAGNOSTIC_ROOT", orchestrator)
        self.assertNotIn('runtime_report or (ROOT / "evidence"', orchestrator)
        runtime = (scripts / "start-strict-sss2-api-runtime.sh").read_text(encoding="utf-8")
        self.assertIn("diagnostic-evidence/runtime", runtime)
        self.assertNotIn('OUTPUT="${OUTPUT:-$ROOT/evidence/', runtime)
        legacy = ROOT / "scripts" / "tests"
        for name in ("test-attachment-runtime-oracle.py", "test-consumables-query-oracle.py",
                     "test-consumables-runtime-oracle.py", "test-equipment-query-oracle.py",
                     "test-equipment-runtime-oracle.py", "test-result-run-write-oracle.py"):
            text = (legacy / name).read_text(encoding="utf-8")
            self.assertIn("guard_output_path", text, name)
            self.assertNotIn("default=ROOT / 'evidence/", text, name)
        distribution_runner = (ROOT / "owned-distribution/tests/run-result-write-browser.py").read_text(encoding="utf-8")
        self.assertIn("guard_output_path", distribution_runner)
        self.assertNotIn('default=ROOT / "evidence/', distribution_runner)
        for path in sorted((ROOT / "owned-distribution/tests").glob("run-*.py")):
            text = path.read_text(encoding="utf-8")
            if "--output" in text:
                self.assertIn("guard_output_path", text, str(path))
        for path in sorted((scripts / "tests").glob("test-*-oracle.py")):
            text = path.read_text(encoding="utf-8")
            if "--output" in text:
                self.assertIn("guard_output_path", text, str(path))
        independent = (ROOT / "scripts/replay-independent-current.py").read_text(encoding="utf-8")
        self.assertNotIn("'--output', 'evidence/", independent)


if __name__ == "__main__":
    unittest.main()
