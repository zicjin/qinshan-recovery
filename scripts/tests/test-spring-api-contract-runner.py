#!/usr/bin/env python3
import importlib.util
import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
import threading
import tempfile
import unittest
from unittest import mock

ROOT = Path(__file__).resolve().parents[2]
RUNNER = ROOT / "scripts" / "spring_api_contract_runner.py"
spec = importlib.util.spec_from_file_location("spring_api_contract_runner", RUNNER)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/ok":
            body = b'{"publicKey":"redacted-test-key","status":"ok"}'
            self.send_response(200)
        elif self.path == "/text":
            body = b"/files/secure/20261031/hash/fixture.txt"
            self.send_response(200)
            self.send_header("Content-Type", "text/plain")
        else:
            body = b'{"error":"missing"}'
            self.send_response(404)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(body)

    def do_POST(self):
        length = int(self.headers.get("Content-Length", "0"))
        self.rfile.read(length)
        body = b'{"id":"created"}'
        self.send_response(201)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, *_args):
        pass


class RunnerTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = ThreadingHTTPServer(("127.0.0.1", 0), Handler)
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.base = "http://127.0.0.1:%d" % cls.server.server_address[1]

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()
        cls.thread.join(timeout=5)

    def run_document(self, document, mode="debug", clone=None):
        with tempfile.TemporaryDirectory() as temp:
            case_file = Path(temp) / "cases.json"
            output = Path(temp) / "report.json"
            case_file.write_text(json.dumps(document), encoding="utf-8")
            document = dict(document)
            document["case_file"] = str(case_file)
            report = module.run_cases(document, base_url=self.base, mode=mode,
                                      role="anonymous", organization="none",
                                      clone_identity=clone, artifacts=[], output=output,
                                      timeout=3)
            return report

    def test_debug_records_http_without_strict_promotion(self):
        report = self.run_document({"cases": [{
            "id": "public-key", "method": "GET", "path": "/ok",
            "expected_status": 200, "assert": {"json_keys": ["publicKey"]},
        }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertFalse(report["strict_evidence"])
        self.assertEqual(report["cases"][0]["status"], 200)
        self.assertTrue(report["cases"][0]["body_sha256"])

    def test_json_contains_assertion_selects_a_row(self):
        body = b'{"rows":[{"id":"00375QS","role":"0"},{"id":"Test","role":"1"}]}'
        result = module._assert_response({"expected_status": 200, "assert": {
            "json_contains": [{"path": "rows", "where": {"id": "Test"}, "values": {"role": "1"}}]
        }}, 200, body, {"content-type": "application/json"})
        self.assertEqual(result["body_bytes"], len(body))

    def test_json_values_assertion_checks_exact_top_level_value(self):
        body = b'{"rows":[],"total":0}'
        result = module._assert_response({"expected_status": 200,
                                           "assert": {"json_values": {"total": 0}}},
                                          200, body, {"content-type": "application/json"})
        self.assertEqual(result["json_top_level_keys"], ["rows", "total"])

    def test_loopback_ignores_environment_proxy(self):
        with mock.patch.dict(module.os.environ, {
            "http_proxy": "http://127.0.0.1:1",
            "https_proxy": "http://127.0.0.1:1",
            "no_proxy": "",
            "HTTP_PROXY": "http://127.0.0.1:1",
            "HTTPS_PROXY": "http://127.0.0.1:1",
            "NO_PROXY": "",
        }, clear=False):
            report = self.run_document({"cases": [{
                "id": "proxy-free", "method": "GET", "path": "/ok",
                "expected_status": 200, "assert": {"json_keys": ["publicKey"]},
            }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertEqual(report["cases"][0]["status"], 200)

    def test_strict_requires_clone_and_fixture_restore_for_declared_fixture(self):
        with tempfile.TemporaryDirectory() as temp:
            artifact = Path(temp) / "app.jar"
            artifact.write_bytes(b"owned-test-artifact")
            runtime_report = Path(temp) / "runtime.json"
            runtime_report.write_text(json.dumps({
                "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
                "strict_evidence": False, "production_access": False,
                "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
                "existing_application_preserved": True,
                "loopback_url": self.base,
                "source_schema": "SOURCE", "target_schema": "TARGET",
                "clone_baseline": {
                    "source_dump_sha256": "a" * 64,
                    "target_schema_metadata_sha256": "b" * 64,
                    "source_read_only": True, "target_isolated": True,
                },
            }), encoding="utf-8")
            document = {"cases": [{
                "id": "write", "method": "POST", "path": "/create", "operation": "write",
                "expected_status": 201,
                "fixture": {
                    "mode": "api",
                    "setup_requests": [{"id": "fixture-setup", "method": "POST", "path": "/create", "expected_status": 201}],
                    "restore_requests": [{"id": "fixture-restore", "method": "POST", "path": "/create", "expected_status": 201}],
                },
                "audit": {"required": True, "status": "verified", "event": "create", "outcome": "allowed"},
            }], "cleanup_contract": {
                "application_stopped": True, "oracle_stopped": True, "redis_stopped": True,
                "network_removed": True, "volumes_removed": True, "temporary_files_removed": True,
            }}
            case_file = Path(temp) / "cases.json"
            output = Path(temp) / "report.json"
            case_file.write_text(json.dumps(document), encoding="utf-8")
            document["case_file"] = str(case_file)
            with mock.patch.object(module, "source_metadata", return_value={
                "source_commit": "test-commit", "source_dirty": False, "source_scope": [],
            }):
                report = module.run_cases(document, base_url=self.base, mode="strict",
                                          role="anonymous", organization="none",
                                          clone_identity="clone-test", artifacts=[artifact],
                                          output=output, timeout=3,
                                          runtime_report=runtime_report)
        self.assertEqual(report["status"], "executed-pass")
        self.assertTrue(report["strict_evidence"])

    def test_strict_accepts_persistent_oracle_cleanup_contract(self):
        with tempfile.TemporaryDirectory() as temp:
            artifact = Path(temp) / "app.jar"
            artifact.write_bytes(b"owned-test-artifact")
            runtime_report = Path(temp) / "runtime.json"
            runtime_report.write_text(json.dumps({
                "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
                "strict_evidence": False, "production_access": False,
                "existing_application_preserved": True, "loopback_url": self.base,
                "source_commit": "test-commit", "source_dirty": False,
                "oracle_instance_id": "lims-owned-sss2-oracle",
                "oracle_container": "lims-owned-sss2-oracle", "oracle_reused": True,
                "baseline_schema": "LIMS_BASELINE_SSS2", "runtime_schema": "LIMS_RUNTIME_SSS2",
                "baseline_schema_hash": "a" * 64, "runtime_schema_hash_before": "b" * 64,
                "runtime_schema_hash_after": "b" * 64, "schema_copy_method": "baseline-expdp-impdp-same-instance",
                "baseline_key_data_hash": "c" * 64, "runtime_key_data_hash_before": "d" * 64,
                "runtime_key_data_hash_after": "d" * 64,
                "baseline_refreshed": False, "baseline_schema_immutable": True,
                "runtime_schema_restored": True, "runtime_dirty": False,
                "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
            }), encoding="utf-8")
            document = {"cases": [{"id": "read", "method": "GET", "path": "/ok", "expected_status": 200}],
                        "cleanup_contract": {
                            "cleanup_mode": "persistent-oracle", "application_stopped": True,
                            "oracle_stopped": False, "redis_stopped": False, "redis_preserved": True,
                            "oracle_reused": True, "oracle_preserved": True,
                            "baseline_schema_immutable": True, "runtime_schema_restored": True,
                            "runtime_dirty": False, "network_preserved": True,
                            "volume_preserved": True, "temporary_files_removed": True,
                        }}
            case_file = Path(temp) / "cases.json"; output = Path(temp) / "report.json"
            case_file.write_text(json.dumps(document), encoding="utf-8"); document["case_file"] = str(case_file)
            with mock.patch.object(module, "source_metadata", return_value={"source_commit": "test-commit", "source_dirty": False, "source_scope": []}):
                report = module.run_cases(document, base_url=self.base, mode="strict", role="anonymous", organization="none",
                                          clone_identity="persistent-test", artifacts=[artifact], output=output, timeout=3,
                                          runtime_report=runtime_report)
        self.assertEqual(report["status"], "executed-pass")

    def test_business_write_cannot_be_declared_sql_only(self):
        report = self.run_document({"cases": [{
            "id": "bad-write", "method": "POST", "path": "/create", "operation": "write",
            "write_via": "sql", "expected_status": 201,
        }]})
        self.assertEqual(report["status"], "executed-fail")
        self.assertIn("HTTP API", report["reason"])

    def test_api_fixture_capture_expands_followup_request(self):
        report = self.run_document({"cases": [{
            "id": "dynamic-write", "method": "POST", "path": "/create",
            "operation": "write", "write_via": "http", "expected_status": 201,
            "body": {"id": "${fixture.fixture-setup.id}"},
            "fixture": {
                "mode": "api",
                "setup_requests": [{"id": "fixture-setup", "capture_json": True,
                                     "method": "POST", "path": "/create", "expected_status": 201}],
                "restore_requests": [{"id": "fixture-restore", "method": "POST",
                                       "path": "/create", "expected_status": 201}],
            },
        }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertEqual(report["cases"][0]["status"], 201)
        self.assertNotIn("_captured_json", json.dumps(report))

    def test_operation_capture_expands_http_restore(self):
        report = self.run_document({"cases": [{
            "id": "generated-child-write", "method": "POST", "path": "/create",
            "operation": "write", "write_via": "http", "expected_status": 201,
            "capture_json": True, "capture_as": "inserted-child",
            "fixture": {
                "mode": "api",
                "restore_requests": [{"id": "child-restore", "method": "POST",
                                       "path": "/create", "expected_status": 201,
                                       "body": {"id": "${fixture.inserted-child.id}"}}],
            },
        }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertEqual(report["cases"][0]["status"], 201)
        self.assertEqual(report["cases"][0]["fixture"]["restore_http"][0]["status"], 201)
        self.assertNotIn("_captured_json", json.dumps(report))

    def test_text_capture_expands_followup_request(self):
        report = self.run_document({"cases": [{
            "id": "text-capture", "method": "GET", "path": "/text",
            "expected_status": 200, "capture_text": True,
            "fixture": {
                "mode": "api",
                "restore_requests": [{"id": "content-read", "method": "GET",
                                       "path": "${fixture.text-capture}", "expected_status": 404}],
            },
        }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertEqual(report["cases"][0]["fixture"]["restore_http"][0]["status"], 404)
        self.assertNotIn("_captured_text", json.dumps(report))

    def test_readback_expands_sequential_text_capture(self):
        """A readback may use text captured by the immediately prior readback."""
        report = self.run_document({"cases": [{
            "id": "create-and-fetch", "method": "POST", "path": "/create",
            "operation": "write", "write_via": "http", "expected_status": 201,
            "readback_requests": [
                {"id": "url", "method": "GET", "path": "/text",
                 "expected_status": 200, "capture_text": True},
                {"id": "content", "method": "GET", "path": "${fixture.url}",
                 "expected_status": [200, 404]},
            ],
            "fixture": {"mode": "api", "cleanup_via": "case-http"},
        }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertEqual([item["status"] for item in report["cases"][0]["readback_http"]], [200, 404])
        self.assertNotIn("_captured_text", json.dumps(report))

    def test_multipart_fixture_request_is_encoded_and_captured(self):
        report = self.run_document({"cases": [{
            "id": "multipart-upload", "method": "POST", "path": "/upload",
            "expected_status": 201, "capture_json": True,
            "multipart": {
                "fields": {"targetId": "T_EMC_HOME_ATTACHMENT$1", "bizCategory": "document"},
                "file": {"field": "file", "filename": "fixture.txt",
                         "content_type": "text/plain", "content": "fixture bytes\n"},
            },
        }]})
        self.assertEqual(report["status"], "diagnostic-pass")
        self.assertIn("multipart/form-data", report["cases"][0]["request_headers"]["content-type"])
        self.assertNotIn("_captured_json", json.dumps(report))

    def test_embedded_fixture_response_reference(self):
        value = module._expand_fixture_value(
            "/secure/core/module/bp/reagents/${fixture.fixture-setup.id}/compositions",
            {"fixture-setup": {"id": 123}},
        )
        self.assertEqual(value, "/secure/core/module/bp/reagents/123/compositions")

    def test_runtime_fixture_reference_is_expanded_per_run(self):
        value = module._expand_fixture_value(
            {"id": "${runtime.id}", "path": "/runs/${runtime.id}"},
            {}, {"id": "api-run-abc123"},
        )
        self.assertEqual(value, {"id": "api-run-abc123", "path": "/runs/api-run-abc123"})

    def test_strict_sql_fixture_requires_external_executor(self):
        with self.assertRaises(ValueError):
            module._fixture_plan({
                "id": "sql-fixture", "operation": "read",
                "fixture": {"mode": "sql-control", "sql_files": ["core/example.sql"]},
            }, strict=True, sql_executor_available=False)

    def test_non_loopback_and_strict_without_clone_are_rejected(self):
        with self.assertRaises(ValueError):
            module.validate_base_url("http://10.100.2.196:8081", strict=False)
        with self.assertRaises(ValueError):
            module.run_cases({"cases": [{"id": "x", "path": "/ok"}]}, base_url=self.base,
                             mode="strict", role="anonymous", organization="none",
                             clone_identity=None, artifacts=[], output=Path(tempfile.mktemp()), timeout=3)

    def test_strict_rejects_runtime_from_an_old_source_commit(self):
        with tempfile.TemporaryDirectory() as temp:
            runtime = Path(temp) / "runtime.json"
            runtime.write_text(json.dumps({
                "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
                "strict_evidence": False, "production_access": False,
                "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
                "existing_application_preserved": True, "loopback_url": self.base,
                "source_commit": "old-commit", "source_schema": "SOURCE", "target_schema": "TARGET",
                "clone_baseline": {
                    "source_schema_metadata_sha256": "a" * 64,
                    "target_schema_metadata_sha256": "b" * 64,
                    "source_read_only": True, "target_isolated": True,
                },
            }), encoding="utf-8")
            with mock.patch.object(module, "source_metadata", return_value={
                "source_commit": "current-commit", "source_dirty": False, "source_scope": [],
            }):
                with self.assertRaises(module.RuntimeContractError):
                    module.run_cases({"cases": [{"id": "x", "path": "/ok"}]}, base_url=self.base,
                                     mode="strict", role="anonymous", organization="none",
                                     clone_identity="clone", artifacts=[], output=Path(temp) / "out.json",
                                     timeout=3, runtime_report=runtime)

    def test_strict_can_defer_cleanup_without_promoting_pass(self):
        with tempfile.TemporaryDirectory() as temp:
            runtime = Path(temp) / "runtime.json"
            runtime.write_text(json.dumps({
                "status": "clone-ready-unverified", "execution": "clone-ready-unverified",
                "strict_evidence": False, "production_access": False,
                "route_execution_model": {"boundary_process": False, "internal_http_bridge": False},
                "existing_application_preserved": True, "loopback_url": self.base,
                "source_schema": "SOURCE", "target_schema": "TARGET",
                "clone_baseline": {
                    "source_schema_metadata_sha256": "a" * 64,
                    "target_schema_metadata_sha256": "b" * 64,
                    "source_read_only": True, "target_isolated": True,
                },
            }), encoding="utf-8")
            artifact = Path(temp) / "app.jar"; artifact.write_bytes(b"artifact")
            with mock.patch.object(module, "source_metadata", return_value={
                "source_commit": "current", "source_dirty": False, "source_scope": [],
            }):
                report = module.run_cases({"cases": [{"id": "x", "path": "/ok", "expected_status": 200}]},
                                          base_url=self.base, mode="strict", role="anonymous",
                                          organization="none", clone_identity="clone", artifacts=[artifact],
                                          output=Path(temp) / "out.json", timeout=3,
                                          runtime_report=runtime, defer_cleanup=True)
            self.assertEqual(report["status"], "executed-pass-pending-cleanup")
            self.assertTrue(report["cleanup_pending"])


if __name__ == "__main__":
    unittest.main()
