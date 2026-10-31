#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "tools" / "api_route_closure.py"
spec = importlib.util.spec_from_file_location("api_route_closure", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class ClosureTest(unittest.TestCase):
    def setUp(self):
        self.ledger = {
            "source_commit": "head",
            "source_dirty": False,
            "route_execution_model": {"boundary_process": False, "internal_http_bridge": False, "jni_replacement_process": False},
            "routes": [
                {"id": "r1", "method": "GET", "path": "/ok", "family": "core",
                 "behavior_status": "unknown", "disposition": "unknown",
                 "unknown_reason": "no evidence"},
                {"id": "r2", "method": "GET", "path": "/duplicate", "family": "core",
                 "behavior_status": "unknown", "disposition": "unknown",
                 "unknown_reason": "no evidence"},
                {"id": "r3", "method": "GET", "path": "/duplicate", "family": "core",
                 "behavior_status": "unknown", "disposition": "unknown",
                 "unknown_reason": "no evidence"},
            ],
        }

    def test_diagnostic_report_is_recorded_without_promotion(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "diagnostic.json"
            path.write_text('{"status":"diagnostic-pass","strict_evidence":false,"production_access":false,"source_commit":"head","source_dirty":false,"cases":[{"id":"x","status":200,"route":{"method":"GET","path":"/ok","disposition":"owned-pass"}}]}')
            result = module.build_closure(self.ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["required_unknown"], 3)
        self.assertEqual(result["diagnostic_observation_count"], 1)

    def test_fast_differential_case_is_diagnostic_not_binding_failure(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "fast.json"
            path.write_text('{"status":"diagnostic-pass","execution":"fast-differential","strict_evidence":false,"production_access":false,"source_commit":"head","source_dirty":false,"cases":[{"id":"fast-1","route_id":"r1","method":"GET","path":"/ok","classification":"fast-mismatch","diff":{"reason":"status"}}]}')
            result = module.build_closure(self.ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["diagnostic_observation_count"], 1)
        self.assertFalse(result["failures"])

    def test_fast_aliases_require_matching_source_class(self):
        ledger = dict(self.ledger)
        ledger["routes"] = [dict(self.ledger["routes"][0], id="r1", **{"class": "a.Resource"})]
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "fast.json"
            path.write_text(json.dumps({
                "status": "diagnostic-pass", "execution": "fast-differential",
                "strict_evidence": False, "production_access": False,
                "source_commit": "head", "source_dirty": False,
                "cases": [{"id": "probe", "route_ids": ["r1"], "method": "GET", "path": "/ok",
                            "class": "b.Resource", "behavior_family": "wrong",
                            "request": {"profile": "default"}, "classification": "fast-equivalent"}]}),
                encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertTrue(any("source class" in item.get("reason", "") for item in result["diagnostics"]))

    def test_strict_report_promotes_only_unique_route(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text('{"status":"executed-pass","strict_evidence":true,"production_access":false,"source_commit":"head","source_dirty":false,"route_execution_model":{"boundary_process":false,"internal_http_bridge":false,"jni_replacement_process":false},"cleanup":{"application_stopped":true,"oracle_stopped":true,"redis_stopped":true,"network_removed":true,"volumes_removed":true,"temporary_files_removed":true},"cases":[{"id":"x","status":200,"route":{"route_id":"r1","method":"GET","path":"/ok","disposition":"owned-pass","operation":"read"},"fixture":{"restore":"not-required"},"audit":"not-required"}]}')
            result = module.build_closure(self.ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertEqual(result["required_unknown"], 2)
        self.assertFalse(result["failures"])

    def test_strict_behavior_family_promotes_all_alias_routes(self):
        ledger = {
            "source_commit": "head", "source_dirty": False,
            "route_execution_model": {"boundary_process": False, "internal_http_bridge": False, "jni_replacement_process": False},
            "routes": [
                {"id": "a", "method": "POST", "path": "/x/queries", "class": "same.Resource", "behavior_status": "unknown", "disposition": "unknown"},
                {"id": "b", "method": "POST", "path": "/x/queries/raw", "class": "same.Resource", "behavior_status": "unknown", "disposition": "unknown"},
            ],
        }
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict-family.json"
            path.write_text(json.dumps({
                "status": "executed-pass", "strict_evidence": True, "production_access": False,
                "source_commit": "head", "source_dirty": False,
                "route_execution_model": {"boundary_process": False, "internal_http_bridge": False, "jni_replacement_process": False},
                "cleanup": {key: True for key in ("application_stopped", "oracle_stopped", "redis_stopped", "network_removed", "volumes_removed", "temporary_files_removed")},
                "cases": [{"id": "family", "status": 200, "class": "same.Resource", "behavior_family": "family-1",
                            "route": {"method": "POST", "path": "/x/queries", "route_ids": ["a", "b"], "operation": "read", "disposition": "owned-pass"},
                            "fixture": {"restore": "not-required"}, "audit": "not-required"}],
            }))
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 2)
        self.assertEqual(result["required_unknown"], 0)
        self.assertFalse(result["failures"])

    def test_ambiguous_path_requires_route_id(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text('{"status":"executed-pass","strict_evidence":true,"production_access":false,"source_commit":"head","source_dirty":false,"route_execution_model":{"boundary_process":false,"internal_http_bridge":false,"jni_replacement_process":false},"cleanup":{"application_stopped":true,"oracle_stopped":true,"redis_stopped":true,"network_removed":true,"volumes_removed":true,"temporary_files_removed":true},"cases":[{"id":"x","status":200,"route":{"method":"GET","path":"/duplicate","disposition":"owned-pass","operation":"read"},"fixture":{"restore":"not-required"},"audit":"not-required"}]}')
            result = module.build_closure(self.ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertTrue(any("ambiguous" in item for item in result["failures"]))

    def test_route_id_binds_concrete_path_to_template_route(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            ledger = {
                "source_commit": "head", "source_dirty": False,
                "route_execution_model": {"boundary_process": False, "internal_http_bridge": False, "jni_replacement_process": False},
                "routes": [{"id": "template", "method": "POST",
                             "path": "/items/{id}/parts/queries", "family": "core",
                             "behavior_status": "unknown", "disposition": "unknown",
                             "unknown_reason": "no evidence"}],
            }
            path.write_text('{"status":"executed-pass","strict_evidence":true,"production_access":false,"source_commit":"head","source_dirty":false,"route_execution_model":{"boundary_process":false,"internal_http_bridge":false,"jni_replacement_process":false},"cleanup":{"application_stopped":true,"oracle_stopped":true,"redis_stopped":true,"network_removed":true,"volumes_removed":true,"temporary_files_removed":true},"cases":[{"id":"x","status":200,"route":{"route_id":"template","method":"POST","path":"/items/7/parts/queries","disposition":"owned-pass","operation":"read"},"fixture":{"restore":"not-required"},"audit":"not-required"}]}')
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertFalse(result["failures"])

    def test_strict_write_requires_http_restore_rollback_and_audit(self):
        report = {
            "status": "executed-pass", "strict_evidence": True,
            "production_access": False, "source_commit": "head",
            "source_dirty": False,
            "route_execution_model": {"boundary_process": False,
                                       "internal_http_bridge": False,
                                       "jni_replacement_process": False},
            "cleanup": {key: True for key in ("application_stopped", "oracle_stopped", "redis_stopped",
                                               "network_removed", "volumes_removed", "temporary_files_removed")},
            "cases": [{"status": 200,
                       "route": {"method": "PUT", "path": "/items/1",
                                  "operation": "write"},
                       "fixture": {"business_write_via": "sql",
                                   "restore": "not-required"},
                       "audit": "not-required"}],
        }
        ok, reason = module.strict_case_contract(report)
        self.assertFalse(ok)
        self.assertIn("HTTP", reason)

    def test_strict_report_rejects_source_mismatch_even_when_scope_is_unchanged(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text(json.dumps({
                "status": "executed-pass", "strict_evidence": True,
                "production_access": False, "source_commit": "ancestor",
                "source_dirty": False,
                "route_execution_model": {"boundary_process": False,
                                           "internal_http_bridge": False,
                                           "jni_replacement_process": False},
                "cleanup": {key: True for key in ("application_stopped", "oracle_stopped", "redis_stopped",
                                                   "network_removed", "volumes_removed", "temporary_files_removed")},
                "cases": [{"status": 200,
                           "route": {"method": "GET", "path": "/ok", "operation": "read"},
                           "fixture": {"restore": "not-required"}, "audit": "not-required"}],
            }), encoding="utf-8")
            ledger = dict(self.ledger, source_commit="head")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["diagnostic_observation_count"], 1)


if __name__ == "__main__":
    unittest.main()
