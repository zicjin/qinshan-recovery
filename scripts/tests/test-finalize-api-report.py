#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "tools" / "finalize_api_report.py"
spec = importlib.util.spec_from_file_location("finalize_api_report", MODULE_PATH)
assert spec and spec.loader
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def report():
    return {
        "status": "executed-pass-pending-cleanup",
        "strict_evidence": True,
        "production_access": False,
    }


class FinalizeApiReportTest(unittest.TestCase):
    def test_disposable_cleanup_is_accepted(self):
        cleanup = {
            "application_stopped": True, "oracle_stopped": True,
            "redis_stopped": True, "network_removed": True,
            "volumes_removed": True, "temporary_files_removed": True,
        }
        result = module.finalize(report(), cleanup)
        self.assertEqual(result["status"], "executed-pass")
        self.assertFalse(result["cleanup_pending"])

    def test_persistent_oracle_cleanup_is_accepted(self):
        cleanup = {
            "cleanup_mode": "persistent-oracle", "application_stopped": True,
            "oracle_stopped": False, "redis_stopped": False,
            "redis_preserved": True, "oracle_reused": True,
            "oracle_preserved": True, "baseline_schema_immutable": True,
            "runtime_schema_restored": True, "runtime_dirty": False,
            "network_preserved": True, "volume_preserved": True,
            "temporary_files_removed": True,
        }
        result = module.finalize(report(), cleanup)
        self.assertEqual(result["status"], "executed-pass")
        self.assertFalse(result["cleanup_pending"])


if __name__ == "__main__":
    unittest.main()
