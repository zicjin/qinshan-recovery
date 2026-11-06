#!/usr/bin/env python3
"""Add orthogonal binding/fingerprint columns to an existing evidence registry.

The migration never edits evidence artifacts or changes class semantics. Rows
that predate the fingerprint model remain explicitly marked as legacy
composite-binding records and are not promoted to current evidence.
"""
from __future__ import annotations

import argparse
import csv
import hashlib
import json
from pathlib import Path
from typing import Any


NEW_FIELDS = (
    "application_binding",
    "harness_binding",
    "runtime_contract_binding",
    "planning_binding",
    "governance_binding",
    "family_fingerprint",
    "stimulus_fingerprint",
    "response_fingerprint",
    "validation_mode",
    "stale_reason",
    "batch_id",
)


def file_sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def migrate(registry: Path, report: Path, *, apply: bool) -> dict[str, Any]:
    registry_sha256_before = file_sha256(registry)
    with registry.open(encoding="utf-8", newline="") as handle:
        reader = csv.DictReader(handle, delimiter="\t")
        rows = list(reader)
        fields = list(reader.fieldnames or [])
    for field in NEW_FIELDS:
        if field not in fields:
            fields.append(field)

    changes = []
    class_counts: dict[str, int] = {}
    storage_counts: dict[str, int] = {}
    validation_counts: dict[str, int] = {}
    for row in rows:
        cls = row.get("class", "")
        storage = row.get("storage", "")
        class_counts[cls] = class_counts.get(cls, 0) + 1
        storage_counts[storage] = storage_counts.get(storage, 0) + 1
        old = {field: row.get(field, "") for field in NEW_FIELDS}
        for field in NEW_FIELDS:
            if not row.get(field) or row.get(field) == "-":
                if field == "validation_mode":
                    row[field] = "legacy-composite-binding"
                elif field == "stale_reason":
                    row[field] = "legacy-composite-binding"
                else:
                    row[field] = "-"
        validation = row.get("validation_mode", "")
        validation_counts[validation] = validation_counts.get(validation, 0) + 1
        changes.append({
            "path": row.get("path", ""),
            "class": cls,
            "storage": storage,
            "evidence_sha256": row.get("sha256", ""),
            "old_validation_mode": old.get("validation_mode", ""),
            "new_validation_mode": row.get("validation_mode", ""),
            "old_stale_reason": old.get("stale_reason", ""),
            "new_stale_reason": row.get("stale_reason", ""),
            "action": "add-legacy-fingerprint-metadata",
            "reason": "preserve immutable evidence; legacy composite binding cannot be promoted without derivable fingerprints",
        })

    if apply:
        with registry.open("w", encoding="utf-8", newline="") as handle:
            writer = csv.DictWriter(
                handle, fieldnames=fields, delimiter="\t",
                extrasaction="ignore", lineterminator="\n",
            )
            writer.writeheader()
            for row in rows:
                normalized = dict(row)
                for field in fields:
                    if not normalized.get(field):
                        normalized[field] = "-"
                writer.writerow(normalized)
    report.parent.mkdir(parents=True, exist_ok=True)
    with report.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(
            handle,
            fieldnames=(
                "path", "class", "storage", "evidence_sha256",
                "old_validation_mode", "new_validation_mode",
                "old_stale_reason", "new_stale_reason", "action", "reason",
            ),
            delimiter="\t",
            lineterminator="\n",
        )
        writer.writeheader()
        writer.writerows(changes)
    return {
        "registry": str(registry),
        "report": str(report),
        "rows": len(rows),
        "class_counts": dict(sorted(class_counts.items())),
        "storage_counts": dict(sorted(storage_counts.items())),
        "validation_counts": dict(sorted(validation_counts.items())),
        "registry_sha256_before": registry_sha256_before,
        "applied": apply,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--registry", type=Path, required=True)
    parser.add_argument("--report", type=Path, required=True)
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()
    result = migrate(args.registry, args.report, apply=args.apply)
    print(json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
