#!/usr/bin/env python3
"""Immutable evidence batch and milestone manifests.

A report is materialized once as immutable case records plus a batch manifest.
Registry and closure files remain derived views; promotion advances a mutable
latest pointer without rewriting the original case records.
"""
from __future__ import annotations

import hashlib
import json
from pathlib import Path
import re
from typing import Any, Mapping


BATCH_SCHEMA_VERSION = 1
BINDING_FIELDS = (
    "application_binding",
    "harness_binding",
    "runtime_contract_binding",
    "planning_binding",
    "governance_binding",
    "implementation_binding",
    "implementation_tree_digest",
)
CASE_FINGERPRINT_FIELDS = (
    "request_fingerprint",
    "stimulus_fingerprint",
    "response_fingerprint",
    "family_fingerprint",
    "validation_mode",
    "stale_reason",
    "batch_id",
)


class EvidenceBatchError(ValueError):
    pass


def _canonical_bytes(value: Any) -> bytes:
    return json.dumps(
        value, ensure_ascii=False, sort_keys=True, separators=(",", ":"),
    ).encode("utf-8")


def _digest(value: Any) -> str:
    return hashlib.sha256(_canonical_bytes(value)).hexdigest()


def _file_digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def _safe_name(value: str) -> str:
    result = re.sub(r"[^A-Za-z0-9._-]+", "-", value).strip("-.")
    return result or "case"


def _write_immutable(path: Path, value: Mapping[str, Any]) -> str:
    payload = json.dumps(value, ensure_ascii=False, indent=2, sort_keys=True) + "\n"
    data = payload.encode("utf-8")
    if path.exists():
        if path.read_bytes() != data:
            raise EvidenceBatchError("immutable evidence record already exists with different content: " + str(path))
        return hashlib.sha256(data).hexdigest()
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(data)
    path.chmod(0o444)
    return hashlib.sha256(data).hexdigest()


def _report_binding(report: Mapping[str, Any]) -> dict[str, str]:
    return {
        field: str(report.get(field, ""))
        for field in BINDING_FIELDS
    }


def _case_binding(case: Mapping[str, Any], report: Mapping[str, Any]) -> dict[str, str]:
    result = _report_binding(report)
    for field in BINDING_FIELDS:
        if case.get(field):
            result[field] = str(case[field])
    return result


def _case_summary(case: Mapping[str, Any], report: Mapping[str, Any]) -> dict[str, Any]:
    route = case.get("route") if isinstance(case.get("route"), Mapping) else {}
    route_ids = []
    if route.get("route_id"):
        route_ids.append(str(route["route_id"]))
    if isinstance(route.get("route_ids"), list):
        route_ids.extend(str(item) for item in route["route_ids"])
    if case.get("route_id"):
        route_ids.append(str(case["route_id"]))
    if isinstance(case.get("route_ids"), list):
        route_ids.extend(str(item) for item in case["route_ids"])
    route_ids = list(dict.fromkeys(route_ids))
    return {
        "case_id": str(case.get("id") or case.get("case_id") or "case"),
        "route_ids": route_ids,
        "request_fingerprint": str(case.get("request_fingerprint", "")),
        "stimulus_fingerprint": str(
            case.get("stimulus_fingerprint") or case.get("request_fingerprint", "")
        ),
        "response_fingerprint": str(case.get("response_fingerprint", "")),
        "family_fingerprint": str(case.get("family_fingerprint", "")),
        "validation_mode": str(case.get("validation_mode", "")),
        "stale_reason": str(case.get("stale_reason", "")),
        "batch_id": str(case.get("batch_id", report.get("batch_id", ""))),
    }


def registry_metadata(report: Mapping[str, Any]) -> dict[str, str]:
    """Return registry view fields without rewriting immutable case records."""
    cases = [item for item in report.get("cases", []) if isinstance(item, Mapping)]
    summaries = [_case_summary(item, report) for item in cases]

    def unique(field: str) -> str:
        values = sorted({
            str(item.get(field, "")).strip()
            for item in summaries
            if str(item.get(field, "")).strip()
        })
        return ";".join(values)

    result = _report_binding(report)
    result.update({
        "family_fingerprint": unique("family_fingerprint"),
        "stimulus_fingerprint": unique("stimulus_fingerprint"),
        "response_fingerprint": unique("response_fingerprint"),
        "validation_mode": unique("validation_mode"),
        "stale_reason": unique("stale_reason") or str(report.get("stale_reason", "")),
        "batch_id": unique("batch_id") or str(report.get("batch_id", "")),
    })
    return result


def materialize_batch(report: Mapping[str, Any], report_path: Path,
                      batch_root: Path, *, milestone_id: str) -> dict[str, Any]:
    """Materialize one report as immutable case records and a batch manifest."""
    if not isinstance(report, Mapping):
        raise EvidenceBatchError("report must be a mapping")
    batch_id = str(report.get("batch_id", ""))
    if not batch_id:
        raise EvidenceBatchError("report has no batch_id")
    if not milestone_id:
        raise EvidenceBatchError("milestone_id is required")
    cases = report.get("cases")
    if not isinstance(cases, list) or not cases:
        raise EvidenceBatchError("batch report has no cases")

    batch_dir = Path(batch_root) / _safe_name(milestone_id) / _safe_name(batch_id)
    case_dir = batch_dir / "cases"
    case_rows = []
    used_names: set[str] = set()
    for index, case in enumerate(cases):
        if not isinstance(case, Mapping):
            raise EvidenceBatchError("batch case is not a mapping")
        summary = _case_summary(case, report)
        case_record = {
            "schema": BATCH_SCHEMA_VERSION,
            "kind": "immutable-case-record",
            "milestone_id": milestone_id,
            "batch_id": batch_id,
            "report_path": str(report_path),
            "report_sha256": _file_digest(Path(report_path)),
            "binding": _case_binding(case, report),
            "summary": summary,
            "case": dict(case),
        }
        base = _safe_name(summary["case_id"])
        name = base + ".json"
        serial = 2
        while name in used_names:
            name = "%s-%03d.json" % (base, serial)
            serial += 1
        used_names.add(name)
        case_path = case_dir / name
        case_sha256 = _write_immutable(case_path, case_record)
        case_rows.append({
            **summary,
            "case_record_path": case_path.relative_to(batch_root).as_posix(),
            "case_record_sha256": case_sha256,
        })

    batch_payload = {
        "schema": BATCH_SCHEMA_VERSION,
        "kind": "immutable-batch-manifest",
        "milestone_id": milestone_id,
        "batch_id": batch_id,
        "report_path": str(report_path),
        "report_sha256": _file_digest(Path(report_path)),
        "binding": _report_binding(report),
        "case_count": len(case_rows),
        "cases": case_rows,
    }
    batch_payload["manifest_digest"] = "manifest-" + _digest(batch_payload)
    batch_path = batch_dir / "batch.json"
    batch_sha256 = _write_immutable(batch_path, batch_payload)

    milestone_dir = Path(batch_root) / _safe_name(milestone_id)
    milestone_path = milestone_dir / (_safe_name(batch_id) + ".json")
    if milestone_path.is_file():
        existing_milestone = json.loads(milestone_path.read_text(encoding="utf-8"))
        if existing_milestone.get("batch_manifest_sha256") != batch_sha256:
            raise EvidenceBatchError("milestone snapshot already references a different batch manifest")
        return {
            "batch_root": str(Path(batch_root)),
            "batch_manifest": str(batch_path),
            "batch_manifest_sha256": batch_sha256,
            "milestone_manifest": str(milestone_path),
            "milestone_manifest_sha256": _file_digest(milestone_path),
            "case_count": len(case_rows),
        }
    latest_path = milestone_dir / "latest.json"
    previous = None
    if latest_path.is_file():
        previous = json.loads(latest_path.read_text(encoding="utf-8"))
    milestone_payload = {
        "schema": BATCH_SCHEMA_VERSION,
        "kind": "immutable-milestone-snapshot",
        "milestone_id": milestone_id,
        "batch_id": batch_id,
        "batch_manifest_path": batch_path.relative_to(Path(batch_root)).as_posix(),
        "batch_manifest_sha256": batch_sha256,
        "previous_manifest_sha256": (
            str(previous.get("milestone_manifest_sha256", ""))
            if isinstance(previous, Mapping) else ""
        ),
    }
    milestone_payload["manifest_digest"] = "manifest-" + _digest(milestone_payload)
    milestone_sha256 = _write_immutable(milestone_path, milestone_payload)
    latest = {
        "schema": BATCH_SCHEMA_VERSION,
        "milestone_id": milestone_id,
        "batch_id": batch_id,
        "batch_manifest_path": batch_path.relative_to(Path(batch_root)).as_posix(),
        "batch_manifest_sha256": batch_sha256,
        "milestone_manifest_path": milestone_path.relative_to(Path(batch_root)).as_posix(),
        "milestone_manifest_sha256": milestone_sha256,
    }
    latest_path.parent.mkdir(parents=True, exist_ok=True)
    latest_path.write_text(
        json.dumps(latest, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )
    return {
        "batch_root": str(Path(batch_root)),
        "batch_manifest": str(batch_path),
        "batch_manifest_sha256": batch_sha256,
        "milestone_manifest": str(milestone_path),
        "milestone_manifest_sha256": milestone_sha256,
        "case_count": len(case_rows),
    }


def verify_batch(batch_manifest: Path) -> dict[str, Any]:
    """Verify immutable case hashes and reject any manifest tampering."""
    batch_manifest = Path(batch_manifest)
    payload = json.loads(batch_manifest.read_text(encoding="utf-8"))
    if payload.get("kind") != "immutable-batch-manifest":
        raise EvidenceBatchError("not a batch manifest")
    declared_digest = payload.pop("manifest_digest", "")
    actual_digest = "manifest-" + _digest(payload)
    if declared_digest != actual_digest:
        raise EvidenceBatchError("batch manifest digest mismatch")
    root = batch_manifest.parent.parent.parent
    seen = set()
    for row in payload.get("cases", []):
        if not isinstance(row, Mapping):
            raise EvidenceBatchError("batch case summary is invalid")
        path = root / str(row.get("case_record_path", ""))
        if not path.is_file():
            raise EvidenceBatchError("case record is missing: " + str(path))
        if _file_digest(path) != row.get("case_record_sha256"):
            raise EvidenceBatchError("case record digest mismatch: " + str(path))
        record = json.loads(path.read_text(encoding="utf-8"))
        if record.get("batch_id") != payload.get("batch_id"):
            raise EvidenceBatchError("case record batch_id mismatch: " + str(path))
        key = str(row.get("case_record_path", ""))
        if key in seen:
            raise EvidenceBatchError("duplicate case record path: " + key)
        seen.add(key)
    actual_case_files = {
        path.relative_to(root).as_posix()
        for path in (batch_manifest.parent / "cases").glob("*.json")
    }
    if actual_case_files != seen:
        raise EvidenceBatchError("batch case record set is not immutable")
    return dict(payload)
