#!/usr/bin/env python3
import subprocess
import json
from pathlib import Path
import shutil
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/verify-clean-device-contract.py"


class CleanDeviceContractTest(unittest.TestCase):
    def test_partial_core_schema_is_present_but_deployment_stays_blocked(self):
        result = subprocess.run(["python3", str(SCRIPT)], text=True, capture_output=True)
        self.assertEqual(result.returncode, 2, result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt["status"], "blocked")
        self.assertTrue(receipt["contract_valid"])
        self.assertFalse(receipt["deployment_ready"])
        self.assertTrue(receipt["combined_oracle_installation_valid"])
        self.assertFalse(receipt["historical_sql_replay_allowed"])
        self.assertEqual(receipt["core_schema_status"], "partial")
        self.assertTrue(receipt["core_schema_source_bound"])
        self.assertEqual(receipt["first_administrator_status"], "implemented")

    def test_offline_contract_check_does_not_claim_deployment_readiness(self):
        result = subprocess.run(
            ["python3", str(SCRIPT), "--check-contract"], text=True, capture_output=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)
        receipt = json.loads(result.stdout)
        self.assertEqual(receipt["status"], "blocked")
        self.assertTrue(receipt["contract_valid"])
        self.assertFalse(receipt["deployment_ready"])
        self.assertTrue(receipt["combined_oracle_installation_valid"])

    def test_shell_deployment_sequence_stops_before_next_command(self):
        result = subprocess.run(
            ["bash", "-ec", 'python3 "$1"; printf deployment-continued', "bash", str(SCRIPT)],
            text=True, capture_output=True,
        )
        self.assertEqual(result.returncode, 2, result.stderr)
        self.assertNotIn("deployment-continued", result.stdout)

    def test_inaccurate_installation_claims_are_rejected_in_isolated_checkout(self):
        with tempfile.TemporaryDirectory(prefix="clean-device-contract-") as temporary:
            checkout = Path(temporary)
            # Exercise the actual entry point without a parent or private inputs.
            paths = (
                "scripts/verify-clean-device-contract.py",
                "docs/deployment/clean-device-schema-manifest.json",
                "api-fixtures/schema/external-logical-clone.sql",
                "api-fixtures/schema/external-logical-clone-restore.sql",
                "scripts/verify-external-logical-clone-fixture.py",
                "scripts/verify-external-logical-clone-runtime.py",
                "scripts/verify-core-schema-package.py",
                "scripts/verify-combined-oracle-installation.py",
                "owned-distribution/initialize-admin.sh",
                "docs/deployment/first-administrator.md",
            )
            for relative in paths:
                target = checkout / relative
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / relative, target)
            shutil.copytree(ROOT / "deployment/oracle", checkout / "deployment/oracle")
            contract_path = checkout / "docs/deployment/clean-device-schema-manifest.json"
            original = json.loads(contract_path.read_text())

            def run():
                return subprocess.run(
                    ["python3", str(checkout / SCRIPT.relative_to(ROOT)), "--check-contract"],
                    cwd=checkout, text=True, capture_output=True,
                )

            result = run()
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
            self.assertFalse(json.loads(result.stdout)["deployment_ready"])
            for section, field, value in (
                ("production_schema_bootstrap", "status", "unavailable"),
                ("production_schema_bootstrap", "source_bound", False),
                ("production_schema_bootstrap", "complete_current_production_ddl", True),
                ("core_schema_package", "complete_application_bootstrap", True),
                ("first_administrator", "completes_application_bootstrap", True),
                ("combined_oracle_installation", "deployment_ready", True),
                ("combined_oracle_installation", "complete_external_domains", True),
            ):
                with self.subTest(section=section, field=field):
                    changed = json.loads(json.dumps(original))
                    changed[section][field] = value
                    contract_path.write_text(json.dumps(changed))
                    result = run()
                    self.assertNotEqual(result.returncode, 0, result.stdout)
                    self.assertIn("clean-device-contract: FAIL", result.stderr)
            contract_path.write_text(json.dumps(original))
            package_path = checkout / "deployment/oracle/core-schema/manifest.json"
            package = json.loads(package_path.read_text())
            package["deployment_ready"] = True
            package_path.write_text(json.dumps(package))
            result = run()
            self.assertNotEqual(result.returncode, 0, result.stdout)
            self.assertIn("contradicts", result.stderr)

    def test_missing_or_corrupt_combined_plan_is_rejected_in_clean_checkout(self):
        with tempfile.TemporaryDirectory(prefix="clean-device-combined-contract-") as temporary:
            checkout = Path(temporary)
            paths = (
                "scripts/verify-clean-device-contract.py",
                "scripts/verify-combined-oracle-installation.py",
                "scripts/verify-core-schema-package.py",
                "docs/deployment/clean-device-schema-manifest.json",
                "api-fixtures/schema/external-logical-clone.sql",
                "api-fixtures/schema/external-logical-clone-restore.sql",
                "scripts/verify-external-logical-clone-fixture.py",
                "scripts/verify-external-logical-clone-runtime.py",
                "owned-distribution/initialize-admin.sh",
                "docs/deployment/first-administrator.md",
            )
            for relative in paths:
                target = checkout / relative
                target.parent.mkdir(parents=True, exist_ok=True)
                shutil.copyfile(ROOT / relative, target)
            shutil.copytree(ROOT / "deployment/oracle", checkout / "deployment/oracle")

            def run():
                return subprocess.run(
                    ["python3", str(checkout / SCRIPT.relative_to(ROOT)), "--check-contract"],
                    cwd=checkout, text=True, capture_output=True,
                )

            valid = run()
            self.assertEqual(valid.returncode, 0, valid.stdout + valid.stderr)
            self.assertTrue(json.loads(valid.stdout)["contract_valid"])
            self.assertFalse((checkout / "private").exists())
            self.assertFalse((checkout / ".scratch").exists())
            for relative in ("deployment/oracle/installation-manifest.json",
                             "deployment/oracle/install.sql", "deployment/oracle/00-preflight.sql",
                             "deployment/oracle/99-verify.sql",
                             "scripts/verify-combined-oracle-installation.py"):
                with self.subTest(missing=relative):
                    path = checkout / relative
                    original = path.read_bytes()
                    path.unlink()
                    try:
                        result = run()
                        self.assertNotEqual(result.returncode, 0, result.stdout)
                        self.assertIn("clean-device-contract: FAIL", result.stderr)
                        self.assertNotIn('"contract_valid": true', result.stdout)
                    finally:
                        path.write_bytes(original)
            plan_path = checkout / "deployment/oracle/installation-manifest.json"
            original_plan = plan_path.read_bytes()
            plan = json.loads(original_plan)
            plan["phases"][1], plan["phases"][2] = plan["phases"][2], plan["phases"][1]
            plan_path.write_text(json.dumps(plan))
            result = run()
            self.assertNotEqual(result.returncode, 0, result.stdout)
            self.assertIn("clean-device-contract: FAIL", result.stderr)
            self.assertNotIn('"contract_valid": true', result.stdout)
            plan_path.write_bytes(original_plan)
            for relative in ("deployment/oracle/installation-manifest.json",
                             "deployment/oracle/99-verify.sql", "deployment/oracle/core-schema/20-tables.sql"):
                with self.subTest(corrupt=relative):
                    path = checkout / relative
                    original = path.read_bytes()
                    path.write_bytes(b"{}" if path.suffix == ".json" else original + b"-- changed\n")
                    try:
                        result = run()
                        self.assertNotEqual(result.returncode, 0, result.stdout)
                        self.assertIn("clean-device-contract: FAIL", result.stderr)
                        self.assertNotIn('"contract_valid": true', result.stdout)
                    finally:
                        path.write_bytes(original)


if __name__ == "__main__":
    unittest.main()
