#!/usr/bin/env python3
import subprocess
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-clean-device-contract.py"


class CleanDeviceContractTest(unittest.TestCase):
    def test_contract_is_fail_closed_about_missing_core_schema(self):
        result = subprocess.run(["python3", str(SCRIPT)], text=True, capture_output=True)
        self.assertEqual(result.returncode, 2, result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt["status"], "blocked")
        self.assertTrue(receipt["contract_valid"])
        self.assertFalse(receipt["deployment_ready"])
        self.assertFalse(receipt["historical_sql_replay_allowed"])

    def test_offline_contract_check_does_not_claim_deployment_readiness(self):
        result = subprocess.run(
            ["python3", str(SCRIPT), "--check-contract"], text=True, capture_output=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt["status"], "blocked")
        self.assertTrue(receipt["contract_valid"])
        self.assertFalse(receipt["deployment_ready"])

    def test_shell_deployment_sequence_stops_before_next_command(self):
        result = subprocess.run(
            ["bash", "-ec", 'python3 "$1"; printf deployment-continued', "bash", str(SCRIPT)],
            text=True, capture_output=True,
        )
        self.assertEqual(result.returncode, 2, result.stderr)
        self.assertNotIn("deployment-continued", result.stdout)


if __name__ == "__main__":
    unittest.main()
