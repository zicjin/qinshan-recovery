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

    def fingerprint_report(self, *, validation_mode="executed",
                           family_fingerprint="fam-current"):
        bindings = {
            "application_binding": "app-current",
            "harness_binding": "harness-current",
            "runtime_contract_binding": "rt-current",
            "planning_binding": "plan-current",
            "implementation_binding": "impl-current",
            "implementation_tree_digest": "0" * 64,
            "implementation_dirty": False,
        }
        case = {
            "id": "fingerprinted",
            "status": 200,
            "route": {"route_id": "r1", "method": "GET", "path": "/ok",
                      "operation": "read", "disposition": "owned-pass"},
            "fixture": {"required": False, "status": "not-required"},
            "audit": {"required": False, "status": "not-required"},
            "request_fingerprint": "req-" + "a" * 64,
            "stimulus_contract_version": "3",
            "response_fingerprint": "res-" + "b" * 64,
            "normalizer_version": "2",
            "validation_mode": validation_mode,
            "batch_id": "batch-fingerprint",
            "application_binding": "app-current",
            "harness_binding": "harness-current",
            "runtime_contract_binding": "rt-current",
            "family_fingerprint": family_fingerprint,
        }
        report = {
            "schema": 2,
            "status": "executed-pass",
            "strict_evidence": True,
            "production_access": False,
            "source_commit": "head",
            "source_dirty": False,
            "route_execution_model": {
                "boundary_process": False,
                "internal_http_bridge": False,
                "jni_replacement_process": False,
            },
            "cleanup": {key: True for key in (
                "application_stopped", "oracle_stopped", "redis_stopped",
                "network_removed", "volumes_removed", "temporary_files_removed",
            )},
            "harness_semantics_unchanged": validation_mode == "revalidated-by-fingerprint",
            "cases": [case],
            **bindings,
        }
        ledger = {
            **bindings,
            "source_commit": "head",
            "source_dirty": False,
            "route_execution_model": report["route_execution_model"],
            "routes": [{
                "id": "r1", "method": "GET", "path": "/ok", "family": "core",
                "behavior_family_id": "family-current",
                "family_fingerprint": "fam-current",
                "behavior_status": "unknown", "disposition": "unknown",
                "unknown_reason": "no evidence",
            }],
            "boundary_routes": [],
        }
        return report, ledger

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

    def test_fast_aliases_use_explicit_route_ids_across_owned_source_class_seam(self):
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
        self.assertEqual(result["diagnostic_observation_count"], 1)
        self.assertFalse(any("source class" in item.get("reason", "") for item in result["diagnostics"]))
        self.assertEqual(result["routes"][0]["diagnostic_observations"][0]["source_class"], "b.Resource")

    def test_strict_report_promotes_only_unique_route(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text('{"status":"executed-pass","strict_evidence":true,"production_access":false,"source_commit":"head","source_dirty":false,"route_execution_model":{"boundary_process":false,"internal_http_bridge":false,"jni_replacement_process":false},"cleanup":{"application_stopped":true,"oracle_stopped":true,"redis_stopped":true,"network_removed":true,"volumes_removed":true,"temporary_files_removed":true},"cases":[{"id":"x","status":200,"route":{"route_id":"r1","method":"GET","path":"/ok","disposition":"owned-pass","operation":"read"},"fixture":{"restore":"not-required"},"audit":"not-required"}]}')
            result = module.build_closure(self.ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertEqual(result["required_unknown"], 2)
        self.assertFalse(result["failures"])

    def test_strict_behavior_family_without_per_route_proof_stays_diagnostic(self):
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
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["executed_strict_count"], 0)
        self.assertEqual(result["revalidated_by_fingerprint_count"], 0)
        self.assertEqual(result["required_unknown"], 2)
        self.assertEqual(result["diagnostic_observation_count"], 2)
        self.assertEqual(result["affected_replay"]["route_ids"], ["a", "b"])
        for route in result["routes"]:
            self.assertFalse(route["evidence"])
            self.assertEqual(len(route["diagnostic_observations"]), 1)
        self.assertTrue(all(
            "per-target request/auth/organization equivalence proof" in item["reason"]
            for item in result["diagnostics"]
        ))
        self.assertFalse(result["failures"])

    def test_alias_reuse_stays_diagnostic_across_path_role_auth_and_organization(self):
        for mode in ("executed", "revalidated-by-fingerprint"):
            for difference in ("path", "role", "auth", "organization"):
                with self.subTest(mode=mode, difference=difference):
                    report, ledger = self.fingerprint_report(validation_mode=mode)
                    first = ledger["routes"][0]
                    first.update(roles=["reader"], organizations=["org-a"])
                    second = dict(first, id="r2")
                    request = {"role": "reader", "auth": {"profile": "reader-a"},
                               "organization": "org-a"}
                    if difference == "path":
                        second["path"] = "/ok/raw"
                    elif difference == "role":
                        second["roles"] = ["administrator"]
                    elif difference == "auth":
                        request["auth"] = {"profile": "reader-b"}
                    else:
                        second["organizations"] = ["org-b"]
                    ledger["routes"].append(second)
                    case = report["cases"][0]
                    case["request"] = request
                    case["route"].pop("route_id")
                    case["route"]["route_ids"] = ["r1", "r2"]
                    with tempfile.TemporaryDirectory() as temp:
                        path = Path(temp) / "unproved-aliases.json"
                        path.write_text(json.dumps(report), encoding="utf-8")
                        result = module.build_closure(ledger, [path])
                    self.assertEqual(result["strict_pass_count"], 0)
                    self.assertEqual(result["executed_strict_count"], 0)
                    self.assertEqual(result["revalidated_by_fingerprint_count"], 0)
                    self.assertEqual(result["required_unknown"], 2)
                    self.assertEqual(result["diagnostic_observation_count"], 2)
                    self.assertEqual(result["affected_replay"]["route_ids"], ["r1", "r2"])
                    self.assertFalse(result["reports"][0]["family_stale"])
                    self.assertEqual(result["reports"][0]["strict_contract"],
                                     "strict report contract valid")
                    self.assertFalse(result["failures"])

    def test_single_alias_id_without_per_route_proof_stays_diagnostic(self):
        for mode in ("executed", "revalidated-by-fingerprint"):
            with self.subTest(mode=mode):
                report, ledger = self.fingerprint_report(validation_mode=mode)
                report["cases"][0]["route"].pop("route_id")
                report["cases"][0]["route"]["route_ids"] = ["r1"]
                with tempfile.TemporaryDirectory() as temp:
                    path = Path(temp) / "single-alias.json"
                    path.write_text(json.dumps(report), encoding="utf-8")
                    result = module.build_closure(ledger, [path])
                self.assertEqual(result["strict_pass_count"], 0)
                self.assertEqual(result["executed_strict_count"], 0)
                self.assertEqual(result["revalidated_by_fingerprint_count"], 0)
                self.assertEqual(result["diagnostic_observation_count"], 1)
                self.assertEqual(result["affected_replay"]["route_ids"], ["r1"])
                self.assertFalse(result["routes"][0]["evidence"])

    def test_strict_report_promotes_unique_method_and_path_without_route_id(self):
        report, ledger = self.fingerprint_report()
        report["cases"][0]["route"].pop("route_id")
        report["cases"][0].pop("family_fingerprint")
        report["schema"] = 1
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "unique-path.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertEqual(result["executed_strict_count"], 1)
        self.assertEqual(result["required_unknown"], 0)
        self.assertFalse(result["diagnostics"])
        self.assertFalse(result["failures"])

    def test_legacy_explicit_route_without_family_fingerprint_still_passes(self):
        report, ledger = self.fingerprint_report()
        report["schema"] = 1
        report["cases"][0].pop("family_fingerprint")
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "legacy-route.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertFalse(result["reports"][0]["family_stale"])
        self.assertFalse(result["diagnostics"])
        self.assertFalse(result["failures"])

    def test_family_fingerprint_revalidation_counts_without_new_execution(self):
        report, ledger = self.fingerprint_report(
            validation_mode="revalidated-by-fingerprint",
        )
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "revalidated.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertEqual(result["executed_strict_count"], 0)
        self.assertEqual(result["revalidated_by_fingerprint_count"], 1)
        self.assertFalse(result["failures"])

    def test_invalid_report_schema_is_diagnostic_with_complete_fingerprints(self):
        for schema in ("invalid", None, True, False, 1.0, 1.5, "1", "2", 0, -1, [], {}):
            with self.subTest(schema=schema):
                report, ledger = self.fingerprint_report()
                report["schema"] = schema
                with tempfile.TemporaryDirectory() as temp:
                    path = Path(temp) / "invalid-schema.json"
                    path.write_text(json.dumps(report), encoding="utf-8")
                    result = module.build_closure(ledger, [path])
                self.assertEqual(result["strict_pass_count"], 0)
                self.assertEqual(result["executed_strict_count"], 0)
                self.assertEqual(result["revalidated_by_fingerprint_count"], 0)
                self.assertEqual(result["required_unknown"], 1)
                self.assertEqual(result["diagnostic_observation_count"], 1)
                self.assertEqual(result["affected_replay"]["route_ids"], ["r1"])
                self.assertEqual(result["reports"][0]["strict_contract"],
                                 "report schema must be a positive integer")
                self.assertFalse(result["routes"][0]["evidence"])
                self.assertFalse(result["failures"])

    def test_schema_coercion_cannot_bypass_fingerprint_requirements(self):
        for schema in (True, False, 1.0, 1.5, "1"):
            with self.subTest(schema=schema):
                report, ledger = self.fingerprint_report()
                report["schema"] = schema
                for field in ("family_fingerprint", "request_fingerprint", "response_fingerprint"):
                    report["cases"][0].pop(field)
                with tempfile.TemporaryDirectory() as temp:
                    path = Path(temp) / "stripped-fingerprints.json"
                    path.write_text(json.dumps(report), encoding="utf-8")
                    result = module.build_closure(ledger, [path])
                self.assertEqual(result["strict_pass_count"], 0)
                self.assertEqual(result["required_unknown"], 1)
                self.assertEqual(result["diagnostic_observation_count"], 1)
                self.assertEqual(result["affected_replay"]["route_ids"], ["r1"])
                self.assertEqual(result["reports"][0]["strict_contract"],
                                 "report schema must be a positive integer")

                self.assertTrue(any(
                    item["reason"] == "family-fingerprint-missing"
                    for item in result["reports"][0]["family_stale"]
                ))

    def test_missing_schema_keeps_legacy_reports_without_fingerprints_valid(self):
        report, ledger = self.fingerprint_report()
        report.pop("schema")
        for field in ("family_fingerprint", "request_fingerprint", "response_fingerprint"):
            report["cases"][0].pop(field)
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "legacy-default-schema.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertEqual(result["executed_strict_count"], 1)
        self.assertEqual(result["required_unknown"], 0)
        self.assertFalse(result["diagnostics"])
        self.assertFalse(result["failures"])

    def test_family_fingerprint_mismatch_is_stale_and_enters_affected_replay(self):
        report, ledger = self.fingerprint_report(
            validation_mode="revalidated-by-fingerprint",
            family_fingerprint="fam-stale",
        )
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "stale.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["required_unknown"], 1)
        self.assertEqual(result["affected_replay"]["route_ids"], ["r1"])
        self.assertTrue(any(
            item.get("stale_reason") == "family fingerprint is stale or incomplete"
            for item in result["diagnostics"]
        ))

    def test_family_fingerprint_must_match_every_alias_target(self):
        report, ledger = self.fingerprint_report()
        ledger["routes"].append(dict(
            ledger["routes"][0], id="r2", family_fingerprint="fam-other",
        ))
        report["cases"][0]["route"].pop("route_id")
        report["cases"][0]["route"]["route_ids"] = ["r1", "r2"]
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "mixed-family.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["affected_replay"]["route_ids"], ["r1", "r2"])
        self.assertEqual(result["reports"][0]["strict_contract"],
                         "family fingerprint is stale or incomplete")
        self.assertEqual(result["reports"][0]["family_stale"], [{
            "case_index": 0,
            "route_ids": ["r1", "r2"],
            "reason": "family-fingerprint-mismatch",
            "declared_family_fingerprint": "fam-current",
            "expected_family_fingerprints": ["fam-current", "fam-other"],
        }])

    def test_missing_declared_family_fingerprint_is_stale_when_ledger_requires_it(self):
        for schema in (2, "invalid", None):
            with self.subTest(schema=schema):
                report, ledger = self.fingerprint_report()
                report["schema"] = schema
                report["cases"][0].pop("family_fingerprint")
                with tempfile.TemporaryDirectory() as temp:
                    path = Path(temp) / "missing-family.json"
                    path.write_text(json.dumps(report), encoding="utf-8")
                    result = module.build_closure(ledger, [path])
                self.assertEqual(result["strict_pass_count"], 0)
                self.assertEqual(result["required_unknown"], 1)
                self.assertEqual(result["affected_replay"]["route_ids"], ["r1"])
                self.assertTrue(result["reports"][0]["family_stale"])

    def test_declared_family_fingerprint_rejects_missing_target_fingerprints(self):
        for missing_value in ("absent", "", None):
            with self.subTest(missing_value=missing_value):
                report, ledger = self.fingerprint_report()
                target = dict(ledger["routes"][0], id="r2")
                if missing_value == "absent":
                    target.pop("family_fingerprint")
                else:
                    target["family_fingerprint"] = missing_value
                ledger["routes"].append(target)
                report["cases"][0]["route"].pop("route_id")
                report["cases"][0]["route"]["route_ids"] = ["r1", "r2"]
                with tempfile.TemporaryDirectory() as temp:
                    path = Path(temp) / "incomplete-family.json"
                    path.write_text(json.dumps(report), encoding="utf-8")
                    result = module.build_closure(ledger, [path])
                self.assertEqual(result["strict_pass_count"], 0)
                self.assertEqual(result["diagnostic_observation_count"], 2)
                self.assertEqual(result["affected_replay"]["route_ids"], ["r1", "r2"])
                self.assertEqual(result["reports"][0]["strict_contract"],
                                 "family fingerprint is stale or incomplete")
                self.assertTrue(result["reports"][0]["family_stale"])

    def test_unknown_alias_target_is_visible_in_diagnostics_and_affected_replay(self):
        report, ledger = self.fingerprint_report()
        report["cases"][0]["route"].pop("route_id")
        report["cases"][0]["route"]["route_ids"] = ["r1", "unknown-target"]
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "unknown-alias.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["reports"][0]["strict_contract"],
                         "family fingerprint is stale or incomplete")
        self.assertTrue(result["reports"][0]["family_stale"])
        self.assertEqual(result["diagnostic_observation_count"], 2)
        self.assertEqual(result["affected_replay"]["route_ids"], ["r1", "unknown-target"])
        unknown = next(item for item in result["diagnostics"] if item["route_id"] == "unknown-target")
        self.assertIn("route id or method does not match ledger alias", unknown["reason"])

    def test_revalidation_requires_golden_harness_proof(self):
        report, ledger = self.fingerprint_report(
            validation_mode="revalidated-by-fingerprint",
        )
        report["harness_semantics_unchanged"] = False
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "unproved.json"
            path.write_text(json.dumps(report), encoding="utf-8")
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["affected_replay"]["route_ids"], ["r1"])

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

    def test_explicit_route_id_accepts_ledger_any_but_preserves_actual_method(self):
        report, ledger = self.fingerprint_report()
        ledger["routes"][0]["method"] = "ANY"
        report["cases"][0]["route"]["method"] = "POST"
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text(json.dumps(report))
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        self.assertFalse(result["failures"])
        self.assertEqual(report["cases"][0]["route"]["method"], "POST")

    def test_closure_serializes_report_surfaces_across_governance_only_change(self):
        report, ledger = self.fingerprint_report()
        ledger["implementation_binding"] = "impl-new-governance"
        ledger["implementation_tree_digest"] = "1" * 64
        ledger["governance_binding"] = "gov-new"
        report["governance_binding"] = "gov-captured"
        report["runtime_contract_binding"] = "rt-milestone"
        report["cases"][0]["runtime_contract_binding"] = "rt-milestone"
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text(json.dumps(report))
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 1)
        for surface in ("application", "harness", "planning", "governance"):
            self.assertEqual(result[surface + "_binding"], ledger[surface + "_binding"])
        for surface in ("application", "harness", "runtime_contract", "planning", "governance"):
            self.assertEqual(result["reports"][0][surface + "_binding"], report[surface + "_binding"])

    def test_explicit_route_id_still_rejects_different_concrete_method(self):
        report, ledger = self.fingerprint_report()
        report["cases"][0]["route"]["method"] = "POST"
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "strict.json"
            path.write_text(json.dumps(report))
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertTrue(result["failures"])

    def test_alias_id_accepts_ledger_any_for_diagnostic_observation(self):
        ledger = dict(self.ledger)
        ledger["routes"] = [dict(self.ledger["routes"][0], method="ANY")]
        report = {"status": "diagnostic-pass", "execution": "fast-differential",
                  "strict_evidence": False, "production_access": False,
                  "source_commit": "head", "source_dirty": False,
                  "cases": [{"id": "any-alias", "route_ids": ["r1"],
                             "method": "POST", "path": "/ok",
                             "classification": "fast-equivalent"}]}
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "fast.json"
            path.write_text(json.dumps(report))
            result = module.build_closure(ledger, [path])
        self.assertEqual(result["strict_pass_count"], 0)
        self.assertEqual(result["diagnostic_observation_count"], 1)
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
