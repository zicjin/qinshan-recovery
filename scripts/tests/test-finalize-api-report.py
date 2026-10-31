#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "tools" / "finalize_api_report.py"
spec = importlib.util.spec_from_file_location("finalize_api_report", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class FinalizeTest(unittest.TestCase):
    def report(self):
        return {"status": "executed-pass-pending-cleanup", "strict_evidence": True,
                "production_access": False, "cleanup_pending": True}

    def cleanup(self, value=True):
        return {key: value for key in module.REQUIRED}

    def test_promotes_only_after_all_cleanup_fields(self):
        result = module.finalize(self.report(), self.cleanup())
        self.assertEqual(result["status"], "executed-pass")
        self.assertFalse(result["cleanup_pending"])

    def test_incomplete_cleanup_is_rejected(self):
        with self.assertRaises(ValueError):
            module.finalize(self.report(), self.cleanup(False))

    def test_non_pending_report_is_rejected(self):
        report = self.report(); report["status"] = "diagnostic-pass"
        with self.assertRaises(ValueError):
            module.finalize(report, self.cleanup())


if __name__ == "__main__":
    unittest.main()
