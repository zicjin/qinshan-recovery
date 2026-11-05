#!/usr/bin/env python3
import importlib.util
from pathlib import Path
import subprocess
import unittest
from unittest import mock

ROOT = Path(__file__).resolve().parents[2]
MODULE_PATH = ROOT / "scripts" / "generate-fast-strict-contracts.py"
spec = importlib.util.spec_from_file_location("fast_strict", MODULE_PATH)
module = importlib.util.module_from_spec(spec)
assert spec and spec.loader
spec.loader.exec_module(module)


class FastStrictContractTest(unittest.TestCase):
    def test_equivalent_probe_keeps_aliases_and_source_identity(self):
        head = subprocess.check_output(["git", "rev-parse", "HEAD"], text=True).strip()
        binding = {
            "binding_id": "impl-test",
            "implementation_tree_digest": "a" * 64,
            "implementation_dirty": False,
            "implementation_scope": ["src"],
        }
        ledger = {
            "source_commit": head,
            "source_dirty": False,
            "implementation_binding": binding["binding_id"],
            "implementation_tree_digest": binding["implementation_tree_digest"],
            "implementation_dirty": False,
            "implementation_scope": binding["implementation_scope"],
        }
        fast = {
            "execution": "fast-differential", "source_commit": head, "source_dirty": False,
            "implementation_binding": binding["binding_id"],
            "implementation_tree_digest": binding["implementation_tree_digest"],
            "implementation_dirty": False,
            "implementation_scope": binding["implementation_scope"],
            "cases": [{
                "classification": "fast-equivalent", "route_ids": ["a", "b"],
                "method": "POST", "path": "/x/queries", "request_path": "/x/queries",
                "class": "x.Resource", "family": "personnel", "behavior_family": "family-1",
                "request": {"method": "POST", "body": {"p": {"n": 1, "s": 10}}, "profile": "default"},
                "supplier": {"status": 200, "content_type": "application/json", "json": {"rows": [], "total": 0}},
                "owned": {"status": 200, "content_type": "application/json", "json": {"rows": [], "total": 0}},
            }],
        }
        with mock.patch.object(module, "current_implementation_binding", return_value=binding):
            result = module.build(fast, ledger, family="personnel")
        self.assertEqual(len(result["cases"]), 1)
        self.assertEqual(result["cases"][0]["route_ids"], ["a", "b"])
        self.assertEqual(result["cases"][0]["source_class"], "x.Resource")
        self.assertEqual(result["cases"][0]["expected_status"], 200)
        self.assertFalse(result["strict_evidence"])

    def test_equivalent_error_is_not_promoted_to_strict(self):
        head = subprocess.check_output(["git", "rev-parse", "HEAD"], text=True).strip()
        binding = {
            "binding_id": "impl-test",
            "implementation_tree_digest": "a" * 64,
            "implementation_dirty": False,
            "implementation_scope": ["src"],
        }
        ledger = {
            "source_commit": head, "source_dirty": False,
            "implementation_binding": binding["binding_id"],
            "implementation_tree_digest": binding["implementation_tree_digest"],
            "implementation_dirty": False, "implementation_scope": binding["implementation_scope"],
        }
        fast = {
            "execution": "fast-differential", "source_commit": head, "source_dirty": False,
            "implementation_binding": binding["binding_id"],
            "implementation_tree_digest": binding["implementation_tree_digest"],
            "implementation_dirty": False, "implementation_scope": binding["implementation_scope"],
            "cases": [{
                "classification": "fast-equivalent", "route_ids": ["error-route"],
                "method": "GET", "path": "/x/0/action/print", "request_path": "/x/0/action/print",
                "class": "x.Resource", "family": "secondary", "behavior_family": "error-family",
                "request": {"method": "GET", "profile": "print"},
                "supplier": {"status": 500, "content_type": "application/json", "json": {"error": "unexpected"}},
                "owned": {"status": 500, "content_type": "application/json", "json": {"error": "unexpected"}},
            }],
        }
        with mock.patch.object(module, "current_implementation_binding", return_value=binding):
            result = module.build(fast, ledger)
        self.assertEqual(result["cases"], [])
        self.assertEqual(result["skipped"], {"fast-equivalent-error": 1})


if __name__ == "__main__":
    unittest.main()
