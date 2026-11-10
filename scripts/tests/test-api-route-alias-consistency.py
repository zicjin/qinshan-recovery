#!/usr/bin/env python3
"""Check inventory aliases resolve through ledger, plan and skeleton fixtures."""
import importlib.util
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]


def load(name, path):
    spec = importlib.util.spec_from_file_location(name, ROOT / path)
    module = importlib.util.module_from_spec(spec)
    assert spec and spec.loader
    spec.loader.exec_module(module)
    return module


ledger = load("api_route_ledger", "tools/api_route_ledger.py")
plan = load("api_route_execution_plan", "tools/api_route_execution_plan.py")
skeleton = load("api_contract_skeleton", "tools/api_contract_skeleton.py")


class RouteAliasConsistencyTest(unittest.TestCase):
    def inventory(self):
        return {
            "source_commit": "fixture-head",
            "source_dirty": False,
            "routes": [
                {
                    "id": "canonical-route",
                    "method": "GET",
                    "path": "/fixture/items",
                    "class": "fixture.ItemResource",
                    "source": "project/src/main/java/fixture/ItemResource.java",
                    "annotation": "RequestMapping",
                    "behavior_status": "unknown",
                },
                {
                    "id": "legacy-route",
                    "method": "GET",
                    "path": "/fixture/items",
                    "class": "fixture.LegacyItemResource",
                    "source": "legacy/src/main/java/fixture/LegacyItemResource.java",
                    "annotation": "RequestMapping",
                    "behavior_status": "unknown",
                    "source_route_aliases": [{
                        "alias_id": "legacy-route",
                        "alias_kind": "legacy-source-declaration",
                        "target_route_id": "canonical-route",
                        "method": "GET",
                        "path": "/fixture/items",
                        "source": "legacy/src/main/java/fixture/LegacyItemResource.java",
                        "class": "fixture.LegacyItemResource",
                        "source_sha256": "legacy-sha256",
                        "provenance_status": "source-row-retained",
                    }],
                },
            ],
            "boundary_routes": [],
        }

    def test_explicit_legacy_alias_resolves_and_is_not_planned_as_a_second_route(self):
        built = ledger.build(self.inventory())
        aliases = {row["alias_id"]: row for row in built["source_route_aliases"]}
        self.assertIn("legacy-route", aliases)
        self.assertEqual(aliases["legacy-route"]["target_route_id"], "canonical-route")
        self.assertEqual(aliases["legacy-route"]["provenance_status"], "source-row-retained")

        execution_plan = plan.build_plan(built)
        self.assertEqual(set(execution_plan["assignment"]), {"canonical-route"})
        self.assertNotIn("legacy-route", execution_plan["assignment"])
        # The fixture has no Java source tree; family fingerprints are outside
        # this provenance test and are intentionally omitted before skeleton
        # generation.
        execution_plan["family_fingerprints"] = {}

        with tempfile.TemporaryDirectory(prefix="api-route-alias-") as root:
            contract = skeleton.build(built, execution_plan, source_root=Path(root))
        cases = [case for wave in contract["waves"] for case in wave["cases"]]
        self.assertEqual({case["route_id"] for case in cases}, {"canonical-route"})
        self.assertNotIn("legacy-route", contract["behavior_family_assignment"])

    def test_unknown_alias_id_is_rejected(self):
        built = ledger.build(self.inventory())
        aliases = list(built["source_route_aliases"])
        aliases.append({"alias_id": "unknown-route", "target_route_id": "canonical-route"})
        with self.assertRaisesRegex(ValueError, "unknown source route alias id: unknown-route"):
            ledger.validate_source_route_aliases(built["routes"], built["boundary_routes"], aliases)

    def test_alias_target_must_be_a_canonical_route(self):
        built = ledger.build(self.inventory())
        aliases = list(built["source_route_aliases"])
        alias = next(row for row in aliases if row["alias_id"] == "legacy-route")
        aliases[aliases.index(alias)] = dict(alias, target_route_id="missing-route")
        with self.assertRaisesRegex(ValueError, "source route alias target is not a direct route: legacy-route"):
            ledger.validate_source_route_aliases(built["routes"], built["boundary_routes"], aliases)


if __name__ == "__main__":
    unittest.main()
