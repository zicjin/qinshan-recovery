#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from source_binding import REPO_ROOT, is_standalone_root
MODULE_PATH = ROOT / "tools" / "api_contract_skeleton.py"
spec = importlib.util.spec_from_file_location("api_contract_skeleton", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class ContractSkeletonTest(unittest.TestCase):
    def setUp(self):
        if is_standalone_root(REPO_ROOT):
            ledger_path = REPO_ROOT / "evidence/api-route-ledger-current-head.json"
            plan_path = REPO_ROOT / "evidence/api-route-execution-plan-current-head.json"
        else:
            ledger_path = REPO_ROOT.parent / ".scratch/sunway-supplier-exit-api-first/archive/historical/api-route-ledger-20261027-current-head.json"
            plan_path = REPO_ROOT.parent / ".scratch/sunway-supplier-exit-api-first/archive/historical/api-route-execution-plan-20261027-current-head.json"
        self.ledger = json.loads(ledger_path.read_text())
        self.plan = json.loads(plan_path.read_text())

    def test_all_routes_are_assigned_to_ordered_waves(self):
        output = module.build(self.ledger, self.plan)
        # The boundary inventory is generated from the current owned source
        # roots and can grow when a new owned compatibility seam is added.
        # Assert consistency with the generated wave rows rather than freezing
        # yesterday's route total.
        self.assertEqual(output["route_count"], sum(wave["route_count"] for wave in output["waves"]))
        self.assertEqual([wave["wave"] for wave in output["waves"]], list(module.WAVE_ORDER))
        cases = [case for wave in output["waves"] for case in wave["cases"]]
        self.assertEqual(len(cases), output["route_count"])
        self.assertEqual(len({case["id"] for case in cases}), output["route_count"])
        self.assertTrue(all(case["execution_group"] for case in cases))
        self.assertEqual([wave["runtime_id"] for wave in output["waves"]],
                         ["api-wave-" + wave for wave in module.WAVE_ORDER])
        self.assertTrue(all(case["wave_runtime_id"] == "api-wave-" + case["wave"]
                            for case in cases))

    def test_skeleton_does_not_promote_behavior(self):
        output = module.build(self.ledger, self.plan)
        cases = [case for wave in output["waves"] for case in wave["cases"]]
        self.assertTrue(all(case["disposition"] == "unknown" for case in cases))
        self.assertTrue(all(case["unknown_reason"] for case in cases))
        self.assertTrue(all(wave["strict_ready"] is False for wave in output["waves"]))

    def test_fast_skeleton_jobs_are_executable_diagnostic_metadata(self):
        fast_path = ROOT / "tools" / "fast_contract_skeleton.py"
        fast_spec = __import__("importlib.util").util.spec_from_file_location("fast_contract_skeleton", fast_path)
        fast = __import__("importlib.util").util.module_from_spec(fast_spec)
        assert fast_spec and fast_spec.loader
        fast_spec.loader.exec_module(fast)
        output = fast.build(self.ledger, families={"personnel"}, route_kinds={"inherited-api", "api-resource"},
                            limit=10, role="manager", organization="org-a", plan=self.plan)
        self.assertTrue(output["jobs"])
        job = output["jobs"][0]
        self.assertEqual(job["runner"], "lims-recovery/scripts/fast_differential_api_runner.py")
        self.assertTrue(job["probe_ids"])
        self.assertIn("--profiles", job["command_template"])
        self.assertTrue(all("source_class" in case for case in output["cases"]))


if __name__ == "__main__":
    unittest.main()
