#!/usr/bin/env python3
"""Promote a strict API report only after real runtime cleanup is verified."""
from __future__ import annotations

import argparse
import datetime as dt
import json
from pathlib import Path
from typing import Any, Dict
import sys

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import cleanup_contract_complete, guard_output_path  # noqa: E402


def finalize(report: Dict[str, Any], cleanup: Dict[str, Any]) -> Dict[str, Any]:
    if report.get("status") != "executed-pass-pending-cleanup":
        raise ValueError("report is not awaiting cleanup finalization")
    if report.get("strict_evidence") is not True or report.get("production_access") is not False:
        raise ValueError("only isolated strict reports can be finalized")
    if not cleanup_contract_complete(cleanup):
        raise ValueError("cleanup contract is incomplete")
    report = dict(report)
    report["cleanup"] = dict(cleanup)
    report["cleanup_pending"] = False
    report["status"] = "executed-pass"
    report["cleanup_finalized_at"] = dt.datetime.now(dt.timezone.utc).isoformat()
    return report


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", type=Path, required=True)
    parser.add_argument("--cleanup-contract", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args(argv)
    report = json.loads(args.input.read_text(encoding="utf-8"))
    cleanup = json.loads(args.cleanup_contract.read_text(encoding="utf-8"))
    finalized = finalize(report, cleanup)
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(finalized, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    args.output.chmod(0o600)
    print("API report finalizer: executed-pass")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
