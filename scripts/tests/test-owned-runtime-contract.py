#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "owned_runtime_contract.py"
spec = importlib.util.spec_from_file_location("owned_runtime_contract", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class RuntimeContractTest(unittest.TestCase):
    def test_debug_runtime_requires_reuse_and_supplier_preservation(self):
        result = module.validate_report({
            "status": "diagnostic-running", "strict_evidence": False,
            "production_access": False, "loopback_url": "http://127.0.0.1:18088",
            "container": "owned-debug", "reused_oracle": "oracle", "reused_redis": "redis",
            "supplier_port_preserved": 18081,
        }, "debug")
        self.assertTrue(result["reuse"])

    def test_strict_runtime_requires_clone_hashes_and_isolation(self):
        result = module.validate_report({
            "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
            "strict_evidence": False, "production_access": False,
            "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
            "existing_application_preserved": True, "loopback_url": "http://127.0.0.1:18082",
            "source_schema": "SOURCE", "target_schema": "TARGET",
            "clone_baseline": {
                "source_dump_sha256": "a" * 64,
                "target_schema_metadata_sha256": "b" * 64,
                "source_read_only": True, "target_isolated": True,
            },
        }, "strict")
        self.assertFalse(result["strict_evidence"])
        self.assertIn("TARGET", result["runtime_identity"])

    def test_old_executed_pass_is_rejected_as_strict(self):
        with self.assertRaises(module.RuntimeContractError):
            module.validate_report({
                "status": "executed-pass", "production_access": False,
                "loopback_url": "http://127.0.0.1:18087",
            }, "strict")

    def test_persistent_oracle_runtime_requires_baseline_and_runtime_hashes(self):
        result = module.validate_report({
            "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
            "strict_evidence": False, "production_access": False,
            "existing_application_preserved": True, "loopback_url": "http://127.0.0.1:18089",
            "oracle_instance_id": "lims-owned-sss2-oracle", "oracle_container": "lims-owned-sss2-oracle",
            "oracle_reused": True, "baseline_schema": "LIMS_BASELINE_SSS2", "runtime_schema": "LIMS_RUNTIME_SSS2",
            "baseline_schema_hash": "a" * 64, "runtime_schema_hash_before": "b" * 64,
            "runtime_schema_hash_after": "b" * 64, "schema_copy_method": "baseline-expdp-impdp-same-instance",
            "baseline_key_data_hash": "c" * 64, "runtime_key_data_hash_before": "d" * 64,
            "runtime_key_data_hash_after": "d" * 64,
            "baseline_refreshed": False, "baseline_schema_immutable": True,
            "runtime_schema_restored": True, "runtime_dirty": False,
            "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
        }, "strict")
        self.assertTrue(result["reuse"])
        self.assertEqual(result["baseline_schema"], "LIMS_BASELINE_SSS2")
        self.assertEqual(result["baseline_key_data_hash"], "c" * 64)
        self.assertEqual(result["runtime_key_data_hash_before"], "d" * 64)
        self.assertEqual(result["runtime_key_data_hash_after"], "d" * 64)
        self.assertEqual(result["schema_copy_method"], "baseline-expdp-impdp-same-instance")

    def test_schema_metadata_hash_can_be_used_when_dump_was_cleaned(self):
        result = module.validate_report({
            "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
            "strict_evidence": False, "production_access": False,
            "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
            "existing_application_preserved": True, "loopback_url": "http://127.0.0.1:18082",
            "source_schema": "SOURCE", "target_schema": "TARGET", "source_commit": "head",
            "clone_baseline": {
                "source_schema_metadata_sha256": "a" * 64,
                "target_schema_metadata_sha256": "b" * 64,
                "source_read_only": True, "target_isolated": True,
            },
        }, "strict")
        self.assertEqual(result["source_commit"], "head")

    def test_production_or_remote_url_is_rejected(self):
        with self.assertRaises(module.RuntimeContractError):
            module.validate_report({
                "status": "diagnostic-running", "strict_evidence": False,
                "production_access": False, "loopback_url": "http://10.100.2.196:8081",
                "reused_oracle": "oracle", "reused_redis": "redis",
                "supplier_port_preserved": 18081,
            }, "debug")


if __name__ == "__main__":
    unittest.main()
