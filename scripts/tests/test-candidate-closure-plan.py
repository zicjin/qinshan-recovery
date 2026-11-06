#!/usr/bin/env python3
"""Validate candidate priority planning without promoting any behavior state."""
import csv
from pathlib import Path
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))
from function_catalog.build_candidate_plan import FIELDS, build


def main():
    ledger = ROOT / ".scratch/sunway-supplier-exit-api-first/evidence-archive/function-catalog/candidate-closure.tsv"
    plan = ROOT / ".scratch/sunway-supplier-exit-api-first/evidence-archive/function-catalog/candidate-closure-plan.tsv"
    with tempfile.TemporaryDirectory(prefix="candidate-plan-") as temp:
        generated = Path(temp) / "candidate-closure-plan.tsv"
        if build(ledger, generated) != 914 or generated.read_bytes() != plan.read_bytes():
            raise SystemExit("candidate closure priority plan is stale")
    rows = list(csv.DictReader(plan.open(encoding="utf-8"), delimiter="\t"))
    if len(rows) != 914 or tuple(rows[0]) != FIELDS:
        raise SystemExit("candidate priority plan schema/count mismatch")
    if {row["priority"] for row in rows} != {"P0", "P1", "P2"}:
        raise SystemExit("candidate priority plan lacks all waves")
    if any(row["status"] not in {"unknown", "disabled-in-isolated-env"} for row in rows):
        raise SystemExit("candidate priority plan promoted a behavior status")
    print("candidate closure plan: PASS (914 groups; P0/P1/P2 ordering, source and boundary fields)")


if __name__ == "__main__":
    main()
