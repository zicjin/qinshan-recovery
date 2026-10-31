#!/usr/bin/env python3
"""Regression tests for implementation binding versus repository provenance."""
from __future__ import annotations

import importlib.util
import subprocess
import tempfile
from pathlib import Path
import unittest

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


if __name__ == "__main__":
    unittest.main()
