#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "run-fast-differential-jobs.py"
spec = importlib.util.spec_from_file_location("fast_jobs", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class FastJobsTest(unittest.TestCase):
    def test_build_command_uses_generated_families_profiles_and_diagnostic_output(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            command = module.build_command(
                {"wave": "secondary", "families": ["page-other", "result-order-project"],
                 "route_kind": ["inherited-api"], "profiles": ["default", "page-2"],
                 "probe_ids": ["p1", "p2"]},
                ledger=root / "ledger.json", plan=root / "plan.json",
                supplier="http://127.0.0.1:19081", owned="http://127.0.0.1:19087",
                auth_user="Test", password_file=root / "password",
                output=root / "report.json", timeout=7.0)
        self.assertIn("--family", command)
        self.assertIn("page-other,result-order-project", command)
        self.assertIn("--profiles", command)
        self.assertIn("default,page-2", command)
        self.assertIn("--limit", command)
        self.assertIn("2", command)


if __name__ == "__main__":
    unittest.main()
