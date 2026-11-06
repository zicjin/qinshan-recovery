#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE = ROOT / "scripts" / "evidence_batch.py"
spec = importlib.util.spec_from_file_location("evidence_batch", MODULE)
assert spec and spec.loader
batch = importlib.util.module_from_spec(spec)
spec.loader.exec_module(batch)


class EvidenceBatchTest(unittest.TestCase):
    def report(self):
        case = {
            "id": "case-1",
            "batch_id": "batch-fixed",
            "route": {
                "route_id": "route-1",
                "route_ids": ["route-1", "route-alias"],
                "method": "GET",
                "path": "/ok",
            },
            "request_fingerprint": "req-" + "a" * 64,
            "stimulus_fingerprint": "req-" + "a" * 64,
            "response_fingerprint": "res-" + "b" * 64,
            "family_fingerprint": "fam-" + "c" * 64,
            "validation_mode": "revalidated-by-fingerprint",
            "stale_reason": "",
            "application_binding": "app-current",
            "harness_binding": "harness-current",
            "runtime_contract_binding": "rt-current",
        }
        return {
            "schema": 2,
            "batch_id": "batch-fixed",
            "application_binding": "app-current",
            "harness_binding": "harness-current",
            "runtime_contract_binding": "rt-current",
            "planning_binding": "plan-current",
            "governance_binding": "gov-current",
            "implementation_binding": "impl-current",
            "implementation_tree_digest": "0" * 64,
            "cases": [case],
        }

    def materialize(self, base, report=None):
        source = base / "pending" / "report.json"
        source.parent.mkdir(parents=True, exist_ok=True)
        source.write_text(json.dumps(report or self.report()), encoding="utf-8")
        return batch.materialize_batch(
            report or self.report(),
            source,
            base / "batches",
            milestone_id="milestone-1",
        )

    def test_batch_materializes_immutable_case_alias_and_binding(self):
        with tempfile.TemporaryDirectory() as temp:
            base = Path(temp)
            info = self.materialize(base)
            manifest_path = Path(info["batch_manifest"])
            manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
            self.assertEqual(manifest["case_count"], 1)
            self.assertEqual(
                manifest["cases"][0]["route_ids"],
                ["route-1", "route-alias"],
            )
            self.assertEqual(
                manifest["cases"][0]["validation_mode"],
                "revalidated-by-fingerprint",
            )
            self.assertEqual(manifest["binding"]["application_binding"], "app-current")
            self.assertTrue(manifest["manifest_digest"].startswith("manifest-"))
            self.assertEqual(batch.verify_batch(manifest_path)["batch_id"], "batch-fixed")

    def test_case_record_tamper_is_rejected(self):
        with tempfile.TemporaryDirectory() as temp:
            base = Path(temp)
            info = self.materialize(base)
            manifest = json.loads(Path(info["batch_manifest"]).read_text(encoding="utf-8"))
            case_path = base / "batches" / manifest["cases"][0]["case_record_path"]
            case_path.chmod(0o644)
            case_path.write_text(case_path.read_text(encoding="utf-8").replace("case-1", "case-2"))
            with self.assertRaisesRegex(batch.EvidenceBatchError, "digest mismatch"):
                batch.verify_batch(Path(info["batch_manifest"]))

    def test_manifest_tamper_is_rejected(self):
        with tempfile.TemporaryDirectory() as temp:
            base = Path(temp)
            info = self.materialize(base)
            manifest_path = Path(info["batch_manifest"])
            manifest_path.chmod(0o644)
            payload = json.loads(manifest_path.read_text(encoding="utf-8"))
            payload["case_count"] = 2
            manifest_path.write_text(json.dumps(payload), encoding="utf-8")
            with self.assertRaisesRegex(batch.EvidenceBatchError, "manifest digest mismatch"):
                batch.verify_batch(manifest_path)

    def test_same_batch_is_idempotent_but_different_content_fails(self):
        with tempfile.TemporaryDirectory() as temp:
            base = Path(temp)
            first = self.materialize(base)
            second = self.materialize(base)
            self.assertEqual(first["batch_manifest_sha256"], second["batch_manifest_sha256"])
            changed = self.report()
            changed["cases"][0]["response_fingerprint"] = "res-" + "d" * 64
            with self.assertRaisesRegex(batch.EvidenceBatchError, "different content"):
                self.materialize(base, changed)

    def test_registry_metadata_maps_case_fingerprints_to_view(self):
        metadata = batch.registry_metadata(self.report())
        self.assertEqual(metadata["batch_id"], "batch-fixed")
        self.assertEqual(metadata["family_fingerprint"], "fam-" + "c" * 64)
        self.assertEqual(metadata["stimulus_fingerprint"], "req-" + "a" * 64)
        self.assertEqual(metadata["validation_mode"], "revalidated-by-fingerprint")


if __name__ == "__main__":
    unittest.main()
