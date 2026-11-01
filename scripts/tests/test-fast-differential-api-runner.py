#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("fast_diff", ROOT / "scripts/fast_differential_api_runner.py")
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(MODULE)


class FastDifferentialTest(unittest.TestCase):
    def test_only_loopback_targets_are_allowed(self):
        self.assertEqual(MODULE.validate_loopback_url("http://127.0.0.1:18081", "supplier"),
                         "http://127.0.0.1:18081")
        with self.assertRaises(MODULE.ProbeError):
            MODULE.validate_loopback_url("http://10.100.2.196:8081", "supplier")
        with self.assertRaises(MODULE.ProbeError):
            MODULE.validate_loopback_url("http://user:pass@127.0.0.1:18081", "supplier")

    def test_stale_ledger_is_rejected_before_authentication(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            ledger = root / "ledger.json"
            ledger.write_text(json.dumps({
                "source_commit": "stale",
                "source_dirty": False,
                "route_execution_model": {
                    "boundary_process": False,
                    "internal_http_bridge": False,
                },
                "routes": [],
            }))
            password = root / "password"
            password.write_text("TEST_PASSWORD=unused\n")
            password.chmod(0o600)
            args = type("Args", (), {
                "supplier_base_url": "http://127.0.0.1:18081",
                "owned_base_url": "http://127.0.0.1:18088",
                "ledger": ledger,
                "auth_user": "Test",
                "password_file": password,
                "family": "",
                "route_kind": "inherited-api,api-resource",
                "role": "manager",
                "organization": "org-a",
                "limit": 1,
                "timeout": 1.0,
            })()
            with self.assertRaises(MODULE.ProbeError):
                MODULE.run(args)

    def test_only_read_queries_and_public_key_are_safe(self):
        self.assertTrue(MODULE.route_safe({"method": "POST", "path": "/x/queries", "route_kind": "inherited-api"})[0])
        self.assertFalse(MODULE.route_safe({"method": "PUT", "path": "/x/{id}", "route_kind": "inherited-api"})[0])
        self.assertTrue(MODULE.route_safe({"method": "GET", "path": "/open/security/public-key", "route_kind": "web-controller"})[0])

    def test_public_key_uses_wire_shape_not_dynamic_bytes(self):
        classification, diff = MODULE.compare(
            {"status": 200, "content_type": "application/json", "body_sha256": "a"},
            {"status": 200, "content_type": "text/plain", "body_sha256": "b"},
            "/open/security/public-key",
        )
        self.assertEqual(classification, "fast-equivalent")
        self.assertEqual(diff["reason"], "rsa-public-key-shape")

    def test_normalization_removes_dynamic_ids(self):
        value = MODULE.normalize({"id": 1, "name": "same", "rows": [{"id": 2}, {"id": 1}]})
        self.assertEqual(value["id"], "<NORMALIZED>")
        self.assertEqual(value["rows"], [{"id": "<NORMALIZED>"}, {"id": "<NORMALIZED>"}])

    def test_behavior_family_key_ignores_concrete_numeric_ids(self):
        left = {"method": "GET", "path": "/x/123456/info/status", "route_kind": "api-resource"}
        right = {"method": "GET", "path": "/x/999999/info/status", "route_kind": "api-resource"}
        self.assertEqual(MODULE.behavior_family_key(left), MODULE.behavior_family_key(right))

    def test_behavior_family_key_keeps_controller_identity(self):
        left = {"method": "POST", "path": "/x/queries", "route_kind": "inherited-api", "class": "a.Resource"}
        right = {"method": "POST", "path": "/x/queries", "route_kind": "inherited-api", "class": "b.Resource"}
        self.assertNotEqual(MODULE.behavior_family_key(left), MODULE.behavior_family_key(right))

    def test_row_projection_drift_keeps_shape_equivalent(self):
        classification, diff = MODULE.compare(
            {"status": 200, "content_type": "application/json", "json": {"total": 10, "rows": [{"id": 1}]}},
            {"status": 200, "content_type": "application/json", "json": {"total": 2, "rows": [{"id": 2}]}},
            "/x/queries",
        )
        self.assertEqual(classification, "fast-equivalent")
        self.assertEqual(diff["reason"], "row-projection-drift")

    def test_pagination_total_pages_is_data_scope_drift(self):
        classification, diff = MODULE.compare(
            {"status": 200, "content_type": "application/json",
             "json": {"total": 482, "totalPages": 49, "numberOfElements": 10,
                       "rows": [{"id": 1}], "columnTypeMap": {"id": "string"}}},
            {"status": 200, "content_type": "application/json",
             "json": {"total": 323, "totalPages": 33, "numberOfElements": 10,
                       "rows": [{"id": 2}], "columnTypeMap": {"id": "string"}}},
            "/secure/emc/module/mdm/emp/emps/queries",
        )
        self.assertEqual(classification, "fast-equivalent")
        self.assertEqual(diff["category"], "data-scope-mismatch")

    def test_empty_owned_rows_and_sql_helper_metadata_are_data_scope_drift(self):
        classification, diff = MODULE.compare(
            {"status": 200, "content_type": "application/json", "json": {
                "first": True, "last": False, "numberOfElements": 10,
                "pageNumber": 1, "pageSize": 10, "total": 100,
                "totalPages": 10, "totalMap": {"qty_": 100},
                "columnTypeMap": {"id": "number", "name": "string", "rn_": "number"},
                "rows": [{"id": 1, "name": "supplier"}],
            }},
            {"status": 200, "content_type": "application/json", "json": {
                "first": True, "last": True, "numberOfElements": 0,
                "pageNumber": 1, "pageSize": 10, "total": 0,
                "totalPages": 0, "totalMap": {},
                "columnTypeMap": {"id": "number", "name": "string"},
                "rows": [],
            }},
            "/x/queries",
        )
        self.assertEqual(classification, "fast-equivalent")
        self.assertEqual(diff["category"], "data-scope-mismatch")
        self.assertIn("rn_", diff["supplier_metadata_fields"])

    def test_get_non_query_is_not_safe_to_probe(self):
        self.assertFalse(MODULE.route_safe({"method": "GET", "path": "/module/page", "route_kind": "api-resource"})[0])

    def test_queries_tasks_remains_a_read_query(self):
        self.assertTrue(MODULE.route_safe({"method": "POST", "path": "/secure/x/queries/tasks", "route_kind": "api-resource"})[0])

    def test_get_query_profiles_include_pagination_empty_and_malformed(self):
        route = {"method": "GET", "path": "/secure/x/queries", "route_kind": "inherited-api"}
        profiles = MODULE.request_profiles(route)
        self.assertEqual([item["profile"] for item in profiles], ["default", "page-2", "empty", "malformed"])
        self.assertIn("page=2", profiles[1]["path"])

    def test_read_only_print_is_safe_but_other_action_is_not(self):
        self.assertTrue(MODULE.route_safe({"method": "GET", "path": "/x/123/action/print", "route_kind": "inherited-api"})[0])
        self.assertFalse(MODULE.route_safe({"method": "GET", "path": "/x/123/action/export", "route_kind": "inherited-api"})[0])

    def test_query_profiles_cover_page_empty_and_malformed_without_writes(self):
        route = {"method": "POST", "path": "/x/queries", "route_kind": "inherited-api"}
        profiles = MODULE.request_profiles(route)
        self.assertEqual([profile["profile"] for profile in profiles], ["default", "page-2", "empty", "malformed"])
        self.assertEqual(profiles[1]["body"]["p"]["n"], 2)
        self.assertEqual(profiles[2]["body"]["q"]["keyword"], "__sunway_fast_empty__")
        self.assertEqual(profiles[3]["body"], {})

    def test_special_read_profiles_are_named(self):
        self.assertEqual(MODULE.request_profile({"method": "GET", "path": "/x/action/print", "route_kind": "inherited-api"})["profile"], "print")
        self.assertEqual(MODULE.request_profile({"method": "GET", "path": "/x/columns/name", "route_kind": "inherited-api"})["profile"], "column")
        self.assertEqual(MODULE.request_profile({"method": "GET", "path": "/x/detail", "route_kind": "inherited-api"})["profile"], "detail")

    def test_schema_drift_is_not_data_projection_drift(self):
        classification, diff = MODULE.compare(
            {"status": 200, "content_type": "application/json", "json": {"total": 1, "rows": []}},
            {"status": 200, "content_type": "application/json", "json": {"total": 1, "rows": [], "pageSize": 10}},
            "/x/queries",
        )
        self.assertEqual(classification, "fast-mismatch")
        self.assertEqual(diff["reason"], "schema-mismatch")

    def test_safe_observation_hashes_rows_without_copying_business_values(self):
        result = MODULE.safe_observation({
            "status": 200,
            "json": {"total": 1, "rows": [{"id": 123, "name": "private"}]},
        })
        rows = result["json"]["rows"]
        self.assertEqual(rows["count"], 1)
        self.assertEqual(rows["fields"], ["id", "name"])
        self.assertNotIn("private", json.dumps(result, ensure_ascii=False))
        self.assertRegex(rows["projection_sha256"], r"^[0-9a-f]{64}$")

    def test_mismatch_diff_summarizes_rows(self):
        classification, diff = MODULE.compare(
            {"status": 200, "content_type": "application/json", "json": {"rows": [{"name": "supplier-secret"}], "pageNumber": 1}},
            {"status": 200, "content_type": "application/json", "json": {"rows": [{"name": "owned-secret"}], "pageNumber": 2}},
            "/x/queries",
        )
        self.assertEqual(classification, "fast-mismatch")
        self.assertNotIn("supplier-secret", json.dumps(diff))
        self.assertEqual(diff["supplier"]["rows"]["fields"], ["name"])


if __name__ == "__main__":
    unittest.main()
