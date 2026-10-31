#!/usr/bin/env python3
"""Independent contract tests for the source-derived full capability catalog."""
from __future__ import annotations

import importlib.util
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]


def load(name: str, path: Path):
    spec = importlib.util.spec_from_file_location(name, path)
    module = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(module)
    return module


BUILD = load("build_full_catalog", ROOT / "tools/function_catalog/build_full_catalog.py")
VALIDATE = load("validate_full_catalog", ROOT / "tools/validate_full_catalog.py")


class FullCatalogTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)
        self.index = self.root / "recovered-source.json"
        source = {"path": "project/src/main/java/p/People.java", "line": 7, "sha256": "a" * 64}
        self.index.write_text(json.dumps({
            "format": "lims-static-function-catalog-v1",
            "source_commit": "1" * 40,
            "source_tree_sha256": "b" * 64,
            "counts": {"source_files": 3, "method_mappings": 2, "templates": 1,
                       "service_mapper_interfaces": 1, "mapper_xml": 1},
            "source_manifest": [source, {"path": "project/src/main/resources/templates/p/detail.html", "sha256": "c" * 64},
                                {"path": "project/src/main/resources/p.xml", "sha256": "d" * 64}],
            "endpoints": [
                {"symbol": "p.People", "member": "list", "annotation": "GetMapping",
                 "source": source, "http_methods": ["GET"], "declared_paths": ["/people"],
                 "candidate_routes": [{"path": "/people", "basis": "declared-type-and-method"}],
                 "dynamic_status": "unknown"},
                {"symbol": "p.People", "member": "approve", "annotation": "PostMapping",
                 "source": {**source, "line": 12}, "http_methods": ["POST"], "declared_paths": ["/approve"],
                 "candidate_routes": [{"path": "/approve", "basis": "declared-type-and-method"}],
                 "dynamic_status": "unknown"},
            ],
            "templates": [{"source": {"path": "project/src/main/resources/templates/p/detail.html", "line": 1, "sha256": "c" * 64},
                           "view_name": "p/detail", "dynamic_status": "unknown"}],
            "interfaces": [{"symbol": "p.PeopleService", "kind": "interface", "role": "service",
                            "source": source, "parents": [], "dynamic_status": "unknown"}],
            "mapper_xml": [{"namespace": "p.PeopleMapper", "source": {"path": "project/src/main/resources/p.xml", "line": 1, "sha256": "d" * 64},
                            "statements": [{"kind": "select", "id": "list", "line": 2}],
                            "table_candidates": [], "dynamic_status": "unknown"}],
        }, ensure_ascii=False, sort_keys=True), encoding="utf-8")

    def tearDown(self):
        self.tmp.cleanup()

    def test_generation_is_deterministic_and_preserves_source_evidence(self):
        first = self.root / "one.json"
        second = self.root / "two.json"
        BUILD.build(self.index, first)
        BUILD.build(self.index, second)
        self.assertEqual(first.read_bytes(), second.read_bytes())
        result = json.loads(first.read_text(encoding="utf-8"))
        self.assertEqual(result["counts"]["endpoint"], 2)
        self.assertEqual(result["counts"]["template"], 1)
        self.assertEqual(result["counts"]["scheduled"], 0)
        endpoint = result["records"][0]
        self.assertEqual(endpoint["source"]["sha256"], "a" * 64)
        self.assertEqual(endpoint["route_candidates"][0]["path"], "/people")
        self.assertEqual(endpoint["operation_family"], "read")
        self.assertEqual(result["records"][1]["operation_family"], "approve")
        self.assertEqual(result["coverage"]["unassigned_endpoint_template_indices"], [0, 1])

    def test_validator_rejects_promotion_missing_reference_and_count_mismatch(self):
        out = self.root / "catalog.json"
        BUILD.build(self.index, out)
        self.assertEqual(VALIDATE.validate(out, self.index), 0)
        data = json.loads(out.read_text(encoding="utf-8"))

        data["records"][0]["status"] = "read-flow-verified"
        bad_status = self.root / "bad-status.json"
        bad_status.write_text(json.dumps(data), encoding="utf-8")
        with self.assertRaises(ValueError):
            VALIDATE.validate(bad_status, self.index)

        data = json.loads(out.read_text(encoding="utf-8"))
        data["records"][0]["source"]["path"] = "project/missing.java"
        bad_ref = self.root / "bad-ref.json"
        bad_ref.write_text(json.dumps(data), encoding="utf-8")
        with self.assertRaises(ValueError):
            VALIDATE.validate(bad_ref, self.index)

        data = json.loads(out.read_text(encoding="utf-8"))
        data["counts"]["endpoint"] += 1
        bad_count = self.root / "bad-count.json"
        bad_count.write_text(json.dumps(data), encoding="utf-8")
        with self.assertRaises(ValueError):
            VALIDATE.validate(bad_count, self.index)

        data = json.loads(out.read_text(encoding="utf-8"))
        data["source_assets"].append(dict(data["source_assets"][0]))
        bad_assets = self.root / "bad-assets.json"
        bad_assets.write_text(json.dumps(data), encoding="utf-8")
        with self.assertRaises(ValueError):
            VALIDATE.validate(bad_assets, self.index)

    def test_source_inventory_detects_silent_omission_with_adjusted_counts(self):
        out = self.root / "catalog.json"
        BUILD.build(self.index, out)
        data = json.loads(out.read_text(encoding="utf-8"))
        data["records"] = [row for row in data["records"] if row["record_id"] != "endpoint.000001"]
        data["counts"]["endpoint"] -= 1
        data["counts"]["record"] -= 1
        out.write_text(json.dumps(data), encoding="utf-8")
        with self.assertRaises(ValueError):
            VALIDATE.validate(out, self.index)


if __name__ == "__main__":
    unittest.main()
