#!/usr/bin/env python3
"""Generate an ordered, non-promoting plan for the static candidate ledger."""
import argparse
import csv
from pathlib import Path


FIELDS = ("group_id", "domain", "priority", "next_wave", "status", "reason",
          "source_paths", "route_candidates", "menu", "permission",
          "database_objects", "roles", "organizations", "mode")

DOMAIN_PLAN = {
    "administration": ("P0", "identity-organization-role-menu"),
    "identity": ("P0", "identity-organization-role-menu"),
    "results": ("P1", "results-approval-report"),
    "orders": ("P1", "orders-procurement-workflow"),
    "projects": ("P1", "projects-workflow"),
    "integrations": ("P1", "external-integration-replay"),
    "tasks": ("P1", "scheduler-replay"),
}


def build(ledger: Path, output: Path) -> int:
    rows = []
    with ledger.open(encoding="utf-8", newline="") as stream:
        for row in csv.DictReader(stream, delimiter="\t"):
            priority, wave = DOMAIN_PLAN.get(row["domain"], ("P2", "domain-specific-read-write"))
            rows.append({
                "group_id": row["group_id"], "domain": row["domain"],
                "priority": priority, "next_wave": wave,
                "status": row["status"], "reason": row["reason"],
                "source_paths": row["source_paths"],
                "route_candidates": row["route_candidates"],
                "menu": row["menu"], "permission": row["permission"],
                "database_objects": row["database_objects"],
                "roles": row["roles"], "organizations": row["organizations"],
                "mode": row["mode"],
            })
    rows.sort(key=lambda row: (row["priority"], row["domain"], row["group_id"]))
    output.parent.mkdir(parents=True, exist_ok=True)
    with output.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.DictWriter(stream, fieldnames=FIELDS, delimiter="\t", lineterminator="\n")
        writer.writeheader(); writer.writerows(rows)
    return len(rows)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    print(build(args.ledger, args.output))
