#!/usr/bin/env python3
"""Regression tests for implementation binding versus repository provenance."""
from __future__ import annotations

import importlib.util
import os
import subprocess
import tempfile
from pathlib import Path
import unittest
from unittest import mock

MODULE = Path(__file__).resolve().parents[1] / "source_binding.py"
spec = importlib.util.spec_from_file_location("source_binding", MODULE)
assert spec and spec.loader
binding = importlib.util.module_from_spec(spec)
spec.loader.exec_module(binding)


class SourceBindingTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        (self.root / "src").mkdir()
        (self.root / "runner.py").write_text("runner-v1\n", encoding="utf-8")
        (self.root / "docs").mkdir()
        (self.root / "docs" / "spec.md").write_text("docs-v1\n", encoding="utf-8")
        (self.root / "evidence").mkdir()
        (self.root / "evidence" / "index.tsv").write_text("index-v1\n", encoding="utf-8")
        (self.root / "project").mkdir()
        (self.root / "owned-framework").mkdir()
        subprocess.run(["git", "init", "-q"], cwd=self.root, check=True)
        subprocess.run(["git", "config", "user.email", "test@example.invalid"], cwd=self.root, check=True)
        subprocess.run(["git", "config", "user.name", "source-binding-test"], cwd=self.root, check=True)
        subprocess.run(["git", "add", "."], cwd=self.root, check=True)
        subprocess.run(["git", "commit", "-qm", "initial"], cwd=self.root, check=True)

    def tearDown(self):
        self.temp.cleanup()

    def capture(self):
        return binding.capture_binding(
            self.root,
            extra_scope=("runner.py", "src"),
        )

    def test_docs_only_change_preserves_digest_but_head_changes(self):
        first = self.capture()
        (self.root / "docs" / "spec.md").write_text("docs-v2\n", encoding="utf-8")
        subprocess.run(["git", "add", "docs/spec.md"], cwd=self.root, check=True)
        subprocess.run(["git", "commit", "-qm", "docs"], cwd=self.root, check=True)
        second = self.capture()
        self.assertNotEqual(first["repository_head_at_capture"], second["repository_head_at_capture"])
        self.assertEqual(first["implementation_tree_digest"], second["implementation_tree_digest"])
        self.assertFalse(second["implementation_dirty"])

    def test_evidence_only_change_preserves_digest(self):
        first = self.capture()
        (self.root / "evidence" / "index.tsv").write_text("index-v2\n", encoding="utf-8")
        second = self.capture()
        self.assertEqual(first["implementation_tree_digest"], second["implementation_tree_digest"])
        self.assertFalse(second["implementation_dirty"])

    def test_implementation_change_changes_digest_and_dirty(self):
        first = self.capture()
        (self.root / "src" / "Owned.java").write_text("class Owned {}\n", encoding="utf-8")
        second = self.capture()
        self.assertNotEqual(first["implementation_tree_digest"], second["implementation_tree_digest"])
        self.assertTrue(second["implementation_dirty"])

    def test_runner_change_changes_digest(self):
        first = self.capture()
        (self.root / "runner.py").write_text("runner-v2\n", encoding="utf-8")
        second = self.capture()
        self.assertNotEqual(first["implementation_tree_digest"], second["implementation_tree_digest"])
        self.assertTrue(second["implementation_dirty"])

    def test_feature_root_prefers_parent_workspace_for_standalone_repo(self):
        parent = self.root.parent
        feature = parent / ".scratch" / binding.FEATURE_SLUG
        feature.mkdir(parents=True, exist_ok=True)
        self.assertEqual(binding.feature_root(self.root), feature.resolve())

    def test_feature_root_override_is_respected(self):
        override = self.root / "external-feature"
        with mock.patch.dict(os.environ, {
            "SUNWAY_FEATURE_ROOT": str(override),
        }):
            self.assertEqual(binding.feature_root(self.root), override.resolve())

    def test_workspace_path_resolves_legacy_prefixes_in_standalone_mode(self):
        parent = self.root.parent
        feature = parent / ".scratch" / binding.FEATURE_SLUG
        feature.mkdir(parents=True, exist_ok=True)
        self.assertEqual(
            binding.resolve_workspace_path("lims-recovery/api-contracts/core/read.json", self.root),
            self.root / "api-contracts/core/read.json",
        )
        self.assertEqual(
            binding.resolve_workspace_path(".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/jobs.json", self.root),
            parent / ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending/jobs.json",
        )


if __name__ == "__main__":
    unittest.main()
