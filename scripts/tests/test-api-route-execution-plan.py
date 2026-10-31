#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "tools" / "api_route_execution_plan.py"
spec = importlib.util.spec_from_file_location("api_route_execution_plan", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class ExecutionPlanTest(unittest.TestCase):
    def test_every_route_is_assigned_once_and_groups_are_deterministic(self):
        ledger = {"source_commit": "head", "source_dirty": False, "routes": [
            {"id": "r3", "family": "core", "route_kind": "api-resource", "source": "a/b/C.java"},
            {"id": "r1", "family": "core", "route_kind": "api-resource", "source": "a/b/A.java"},
            {"id": "r2", "family": "personnel", "route_kind": "web-controller", "source": "c/d/B.java"},
        ]}
        first = module.build_plan(ledger, group_size=2)
        second = module.build_plan(ledger, group_size=2)
        self.assertEqual(first["plan_id"], second["plan_id"])
        self.assertEqual(first["route_count"], 3)
        self.assertEqual(first["project_route_count"], 3)
        self.assertEqual(first["boundary_route_count"], 0)
        self.assertEqual(len(first["assignment"]), 3)
        self.assertEqual(sum(group["case_count"] for group in first["groups"]), 3)
        self.assertEqual(set(first["assignment"]), {"r1", "r2", "r3"})
        self.assertTrue(all(group["behavior_status"] == "unknown" for group in first["groups"]))
        self.assertEqual(first["wave_order"], list(module.WAVE_ORDER))
        self.assertEqual(first["wave_counts"], {
            "foundation": 2,
            "personnel": 1,
            "equipment": 0,
            "consumables-materials": 0,
            "secondary": 0,
        })
        self.assertEqual(first["runtime_count"], 2)
        self.assertTrue(all(group["wave_runtime_id"] == "api-wave-" + group["wave"]
                            for group in first["groups"]))
        self.assertTrue(all("one owned Spring application" in group["runtime_policy"]
                            and group["boundary_process"] is False
                            and group["internal_http_bridge"] is False
                            for group in first["groups"]))

    def test_duplicate_route_ids_fail(self):
        with self.assertRaises(ValueError):
            module.build_plan({"routes": [
                {"id": "same", "family": "core", "route_kind": "api", "source": "a"},
                {"id": "same", "family": "core", "route_kind": "api", "source": "b"},
            ]})

    def test_boundary_routes_share_assignment_without_changing_project_count(self):
        plan = module.build_plan({
            "routes": [{"id": "project", "family": "core", "route_kind": "api", "source": "a"}],
            "boundary_routes": [{"id": "boundary", "family": "core", "route_kind": "workspace", "source": "owned"}],
        })
        self.assertEqual(plan["route_count"], 2)
        self.assertEqual(plan["project_route_count"], 1)
        self.assertEqual(plan["boundary_route_count"], 1)
        self.assertEqual(set(plan["assignment"]), {"project", "boundary"})
        self.assertEqual(plan["wave_counts"]["foundation"], 2)
        self.assertEqual(plan["runtime_count"], 1)
        self.assertEqual({group["wave"] for group in plan["groups"]}, {"foundation"})


if __name__ == "__main__":
    unittest.main()
