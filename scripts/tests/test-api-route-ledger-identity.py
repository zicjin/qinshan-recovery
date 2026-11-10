#!/usr/bin/env python3
"""One method/path has one disposition while every source declaration survives."""
import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("ledger_identity", ROOT / "tools/api_route_ledger.py")
ledger = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(ledger)


def route(identifier, owner, path="/secure/example/queries", method="POST"):
    return {"id": identifier, "method": method, "path": path,
            "class": owner, "source": owner.replace(".", "/") + ".java",
            "annotation": "inherited.GenericResource", "behavior_status": "unknown"}


class RouteIdentityTest(unittest.TestCase):
    def test_inherited_and_override_declarations_are_one_route_with_all_provenance(self):
        rows = [route("base-id", "BaseResource"), route("specific-id", "SpecificResource")]
        result = ledger.build({"routes": rows, "boundary_routes": []})
        self.assertEqual(result["counts"]["total_route_count"], 1)
        only = result["routes"][0]
        self.assertEqual(set(only["source_route_ids"]), {"base-id", "specific-id"})
        self.assertEqual({row["class"] for row in only["route_sources"]},
                         {"BaseResource", "SpecificResource"})
        self.assertEqual(only["disposition"], "unknown")
        self.assertEqual(result["counts"]["source_route_count"], 2)
        self.assertEqual(result["counts"]["deduplicated_source_rows"], 1)

    def test_project_and_framework_same_route_do_not_get_two_dispositions(self):
        framework = route("framework-id", "FrameworkResource")
        framework.update(boundary_scope="owned-framework-source-only",
                         route_origin="reconstituted-framework")
        result = ledger.build({"routes": [route("project-id", "ProjectResource")],
                               "boundary_routes": [framework]})
        self.assertEqual(result["counts"]["total_route_count"], 1)
        self.assertEqual(len(result["boundary_routes"]), 0)
        self.assertEqual(set(result["routes"][0]["source_route_ids"]),
                         {"project-id", "framework-id"})

    def test_existing_single_route_id_and_different_http_methods_survive(self):
        first = route("existing-id", "ExampleResource")
        result = ledger.build({"routes": [first, route("get-id", "ExampleResource", method="GET")],
                               "boundary_routes": []})
        self.assertEqual(result["counts"]["total_route_count"], 2)
        self.assertEqual({row["id"] for row in result["routes"]}, {"existing-id", "get-id"})

    def test_merge_identity_is_deterministic_and_does_not_drop_legacy_aliases(self):
        first = route("z-id", "ZResource")
        second = route("a-id", "AResource")
        second["source_route_ids"] = ["earlier-interface-id"]
        second["source_route_aliases"] = [{
            "alias_id": "earlier-interface-id",
            "method": "POST",
            "path": "/secure/example/queries",
            "source": "EarlierResource.java",
            "class": "EarlierResource",
            "source_sha256": "b" * 64,
        }]
        second["source_route_aliases"] = [{
            "alias_id": "earlier-interface-id", "target_route_id": "a-id",
            "method": "POST", "path": "/secure/example/queries",
            "source": "EarlierResource.java", "class": "EarlierResource",
        }]
        a = ledger.build({"routes": [first, second], "boundary_routes": []})
        b = ledger.build({"routes": [second, first], "boundary_routes": []})
        self.assertEqual(a["routes"], b["routes"])
        self.assertEqual(set(a["routes"][0]["source_route_ids"]),
                         {"z-id", "a-id", "earlier-interface-id"})

    def test_explicit_alias_metadata_is_retained_and_resolves_to_authoritative_route(self):
        row = route("current-id", "CurrentResource")
        row["source_route_ids"] = ["legacy-id"]
        row["source_route_aliases"] = [{
            "alias_id": "legacy-id",
            "target_route_id": "stale-target-id",
            "method": "POST",
            "path": "/old-path",
            "source": "legacy/Resource.java",
            "class": "legacy.Resource",
            "source_sha256": "a" * 64,
            "provenance_status": "source-row-retained",
        }]
        result = ledger.build({"routes": [row], "boundary_routes": []})
        alias = next(item for item in result["source_route_aliases"] if item["alias_id"] == "legacy-id")
        self.assertEqual(alias["target_route_id"], "current-id")
        self.assertEqual(alias["source"], "legacy/Resource.java")
        self.assertEqual(alias["class"], "legacy.Resource")
        self.assertEqual(alias["source_sha256"], "a" * 64)


if __name__ == "__main__":
    unittest.main()
