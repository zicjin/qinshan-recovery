#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
MODULE = ROOT / "scripts" / "verify-harness-semantics.py"
spec = importlib.util.spec_from_file_location("verify_harness_semantics", MODULE)
assert spec and spec.loader
verify = importlib.util.module_from_spec(spec)
spec.loader.exec_module(verify)


class HarnessGoldenCorpusTest(unittest.TestCase):
    def test_fixed_corpus_matches_committed_semantic_digest(self):
        expected = json.loads(verify.EXPECTED.read_text(encoding="utf-8"))
        self.assertEqual(verify.evaluate(), expected)

    def test_corpus_covers_all_screening_dispositions(self):
        result = verify.evaluate()
        self.assertEqual(result["record_count"], 20)
        self.assertEqual(
            set(result["classification_counts"]),
            {"fast-equivalent", "data-scope-mismatch", "fast-mismatch"},
        )

    def test_versions_are_explicit(self):
        result = verify.evaluate()
        self.assertEqual(result["stimulus_contract_version"], "3")
        self.assertEqual(result["normalizer_version"], "2")


if __name__ == "__main__":
    unittest.main()
