#!/usr/bin/env python3
"""Generate a conservative, computable closure ledger for static candidates."""
import argparse
import csv
import json
from pathlib import Path


FIELDS = (
    "group_id", "domain", "resource", "operation_family", "source_record_ids",
    "source_paths", "route_candidates", "menu", "permission", "database_objects",
    "roles", "organizations", "mode", "status", "reason",
)


def build(catalog: Path, output: Path) -> int:
    data = json.loads(catalog.read_text(encoding="utf-8"))
    records = {row["record_id"]: row for row in data["records"]}
    rows = []
    for group in data["candidate_groups"]:
        ids = group["record_ids"]
        source_paths = sorted({records[item]["source"]["path"] for item in ids})
        routes = []
        db_objects = []
        for item in ids:
            record = records[item]
            routes.extend(route.get("path", "") for route in record.get("route_candidates", []))
            db_objects.extend(candidate.get("name", "") for candidate in record.get("table_candidates", []))
        rows.append({
            "group_id": group["group_id"],
            "domain": group["domain"],
            "resource": group["resource"],
            "operation_family": group["operation_family"],
            "source_record_ids": ";".join(ids),
            "source_paths": ";".join(source_paths),
            "route_candidates": ";".join(sorted(set(filter(None, routes)))),
            "menu": group.get("menu", "unknown"),
            "permission": group.get("permission", "unknown"),
            "database_objects": ";".join(sorted(set(filter(None, db_objects)))),
            "roles": "unknown",
            "organizations": "unknown",
            "mode": "unknown",
            "status": group.get("status", "unknown"),
            "reason": "no owned-runtime role/organization/data-baseline evidence registered for this candidate group",
        })
    rows.sort(key=lambda row: row["group_id"])
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=FIELDS, delimiter="\t", lineterminator="\n")
        writer.writeheader()
        writer.writerows(rows)
    return len(rows)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--catalog", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    print(build(args.catalog, args.output))
