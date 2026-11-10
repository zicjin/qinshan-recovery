#!/usr/bin/env python3
import subprocess
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-clean-device-contract.py"


class CleanDeviceContractTest(unittest.TestCase):
    def test_contract_is_fail_closed_about_missing_core_schema(self):
        result = subprocess.run(["python3", str(SCRIPT)], text=True, capture_output=True)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('"deployment_ready": false', result.stdout)
        self.assertIn('"historical_sql_replay_allowed": false', result.stdout)


if __name__ == "__main__":
    unittest.main()
