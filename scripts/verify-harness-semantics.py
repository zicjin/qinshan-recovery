#!/usr/bin/env python3
"""Verify request/response fingerprints and differential classification semantics."""
from __future__ import annotations

import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parents[1]
CORPUS = ROOT / "scripts/tests/fixtures/harness-golden-transcript-corpus.jsonl"
EXPECTED = ROOT / "scripts/tests/fixtures/harness-golden-transcript-expected.json"


def load_module(name: str, path: Path):
    spec = importlib.util.spec_from_file_location(name, path)
    if not spec or not spec.loader:
        raise RuntimeError("cannot load " + str(path))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


fingerprints = load_module("golden_evidence_fingerprint", ROOT / "scripts/evidence_fingerprint.py")
differential = load_module("golden_fast_differential", ROOT / "scripts/fast_differential_api_runner.py")


def observation(value: dict[str, Any]) -> dict[str, Any]:
    body = str(value.get("body", "")).encode("utf-8")
    return differential.Session._observation(
        int(value.get("status", 200)),
        dict(value.get("headers") or {"Content-Type": "application/json"}),
        body,
    )


def canonical_digest(value: Any) -> str:
    payload = json.dumps(value, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(payload.encode("utf-8")).hexdigest()


def evaluate() -> dict[str, Any]:
    records = []
    counts: dict[str, int] = {}
    for line_number, line in enumerate(CORPUS.read_text(encoding="utf-8").splitlines(), 1):
        if not line.strip():
            continue
        record = json.loads(line)
        left = observation(record["left"])
        right = observation(record["right"])
        classification, diff = differential.compare(
            left,
            right,
            str(record.get("path", "")),
            profile=record.get("profile"),
        )
        counts[classification] = counts.get(classification, 0) + 1
        records.append({
            "name": record["name"],
            "request_fingerprint": fingerprints.request_fingerprint(record["request"]),
            "left_response_fingerprint": left["response_fingerprint"],
            "right_response_fingerprint": right["response_fingerprint"],
            "left_normalized": differential.safe_observation(left),
            "right_normalized": differential.safe_observation(right),
            "classification": classification,
            "diff": diff,
        })
    return {
        "schema": 1,
        "stimulus_contract_version": fingerprints.STIMULUS_CONTRACT_VERSION,
        "normalizer_version": fingerprints.NORMALIZER_VERSION,
        "record_count": len(records),
        "classification_counts": dict(sorted(counts.items())),
        "semantic_digest": canonical_digest(records),
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--print-result", action="store_true")
    args = parser.parse_args()
    actual = evaluate()
    if args.print_result:
        print(json.dumps(actual, ensure_ascii=False, indent=2, sort_keys=True))
        return 0
    expected = json.loads(EXPECTED.read_text(encoding="utf-8"))
    if actual != expected:
        print(json.dumps({
            "status": "FAIL",
            "expected": expected,
            "actual": actual,
        }, ensure_ascii=False, indent=2, sort_keys=True))
        return 1
    print("harness semantics: PASS (%d golden transcripts)" % actual["record_count"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
