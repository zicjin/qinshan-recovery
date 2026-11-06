#!/usr/bin/env python3
"""Validate the static candidate closure ledger without promoting behavior."""
import csv
from pathlib import Path
import re
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools"))
from function_catalog.build_candidate_closure import FIELDS, build


def main() -> int:
    catalog = ROOT / ".scratch/sunway-supplier-exit-api-first/evidence-archive/function-catalog/full-candidate-capabilities.json"
    ledger = ROOT / ".scratch/sunway-supplier-exit-api-first/evidence-archive/function-catalog/candidate-closure.tsv"
    with tempfile.TemporaryDirectory(prefix="candidate-closure-") as temporary:
        generated = Path(temporary) / "candidate-closure.tsv"
        count = build(catalog, generated)
        if count != 914 or generated.read_bytes() != ledger.read_bytes():
            raise SystemExit("candidate closure is stale or count is not 914")
    with ledger.open(encoding="utf-8", newline="") as stream:
        rows = list(csv.DictReader(stream, delimiter="\t"))
    if len(rows) != 914 or tuple(rows[0]) != FIELDS:
        raise SystemExit("candidate closure schema/count mismatch")
    ids = [row["group_id"] for row in rows]
    if ids != [f"candidate.{index:06d}" for index in range(914)]:
        raise SystemExit("candidate closure group ids are not contiguous")
    for row in rows:
        if row["status"] not in {"unknown", "disabled-in-isolated-env"}:
            raise SystemExit("candidate closure promoted a status: " + row["group_id"])
        if row["menu"] != "unknown" or row["permission"] != "unknown":
            raise SystemExit("candidate closure promoted menu/permission: " + row["group_id"])
        if row["roles"] != "unknown" or row["organizations"] != "unknown" or row["mode"] != "unknown":
            raise SystemExit("candidate closure promoted baseline: " + row["group_id"])
        if re.search(r"(?i)(password|token|secret|authorization|production|ssh\\s+sss)", row["reason"]):
            raise SystemExit("candidate closure contains sensitive wording")
    print("candidate closure list: PASS (914 unknown groups; source, route, menu, permission, database and baseline columns)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
