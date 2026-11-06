#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE = ROOT / "scripts" / "evidence_fingerprint.py"
spec = importlib.util.spec_from_file_location("evidence_fingerprint", MODULE)
assert spec and spec.loader
fingerprints = importlib.util.module_from_spec(spec)
spec.loader.exec_module(fingerprints)


class EvidenceFingerprintTest(unittest.TestCase):
    def test_query_and_json_key_order_are_normalized(self):
        first = fingerprints.request_fingerprint({
            "method": "GET",
            "path_template": "/queries/{id}",
            "path": "/queries/123?page=2&size=10",
            "headers": {"Accept": "application/json"},
            "role": "manager",
            "organization": "org-a",
        })
        second = fingerprints.request_fingerprint({
            "method": "get",
            "path_template": "/queries/{id}",
            "path": "/queries/123?size=10&page=2",
            "headers": {"accept": "application/json"},
            "role": "manager",
            "organization": "org-a",
        })
        self.assertEqual(first, second)

    def test_accept_is_part_of_the_stimulus(self):
        first = fingerprints.request_fingerprint({
            "method": "GET", "path_template": "/ok", "headers": {"Accept": "application/json"},
        })
        second = fingerprints.request_fingerprint({
            "method": "GET", "path_template": "/ok", "headers": {"Accept": "text/html"},
        })
        self.assertNotEqual(first, second)

    def test_sensitive_headers_and_auth_are_hashed(self):
        value = fingerprints.request_fingerprint({
            "method": "GET",
            "path_template": "/ok",
            "headers": {"Authorization": "Bearer secret", "Cookie": "SESSION=secret"},
            "auth": {"session": "secret"},
        })
        self.assertTrue(value.startswith("req-"))
        self.assertNotIn("secret", value)
        self.assertNotIn("secret", json.dumps(value))

    def test_request_body_and_role_change_stimulus(self):
        base = {
            "method": "POST", "path_template": "/queries",
            "body": {"p": {"n": 1, "s": 10}}, "role": "manager",
        }
        body_changed = dict(base, body={"p": {"n": 2, "s": 10}})
        role_changed = dict(base, role="reader")
        self.assertNotEqual(
            fingerprints.request_fingerprint(base),
            fingerprints.request_fingerprint(body_changed),
        )
        self.assertNotEqual(
            fingerprints.request_fingerprint(base),
            fingerprints.request_fingerprint(role_changed),
        )

    def test_response_fingerprint_tracks_raw_shape_projection_and_pagination(self):
        headers = {"Content-Type": "application/json"}
        first_body = b'{"rows":[{"id":"1"}],"total":1}'
        reordered_rows = b'{"rows":[{"id":"1"}],"total":1}'
        changed_body = b'{"rows":[{"id":"2"}],"total":1}'
        changed_total = b'{"rows":[{"id":"1"}],"total":2}'
        self.assertEqual(
            fingerprints.response_fingerprint(200, headers, first_body),
            fingerprints.response_fingerprint(200, headers, reordered_rows),
        )
        self.assertNotEqual(
            fingerprints.response_fingerprint(200, headers, first_body),
            fingerprints.response_fingerprint(200, headers, changed_body),
        )
        self.assertNotEqual(
            fingerprints.response_fingerprint(200, headers, first_body),
            fingerprints.response_fingerprint(200, headers, changed_total),
        )

    def test_transcript_requires_executed_validation_by_default(self):
        result = fingerprints.transcript_fingerprints(
            {"method": "GET", "path_template": "/ok"},
            200,
            {"Content-Type": "application/json"},
            b"{}",
            batch_id="batch-1",
        )
        self.assertEqual(result["validation_mode"], "executed")
        self.assertEqual(result["batch_id"], "batch-1")
        self.assertEqual(result["stimulus_contract_version"], "3")
        self.assertEqual(result["normalizer_version"], "2")


if __name__ == "__main__":
    unittest.main()
