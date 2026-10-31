#!/usr/bin/env python3
"""Validate source coverage and conservative status policy for full catalog."""
from __future__ import annotations

import argparse
import hashlib
import json
import re
import sys
from pathlib import Path


SECRET = re.compile(r"(?i)(password|passwd|token|secret|authorization|bearer|private[_ -]?key)\s*[:=]")
PRODUCTION = re.compile(r"(?i)(10\.100\.|218\.93\.|192\.168\.|ssh\s+sss|https?://[^ ]*production)")
TYPES = {"endpoint", "template", "mapper", "interface", "scheduled"}


def fail(message: str) -> None:
    raise ValueError(message)


def validate(path: Path, source_index: Path | None = None) -> int:
    if not path.is_file() or path.is_symlink():
        fail("catalog is not a regular file")
    data = json.loads(path.read_text(encoding="utf-8"))
    if data.get("format") != "lims-full-capability-catalog-v1":
        fail("unsupported catalog format")
    records = data.get("records")
    if not isinstance(records, list) or not records:
        fail("catalog has no records")
    counts = data.get("counts", {})
    actual = {"record": len(records)}
    for kind in TYPES:
        actual[kind] = sum(1 for row in records if row.get("asset_type") == kind)
    for key, value in actual.items():
        if counts.get(key) != value:
            fail(f"count mismatch for {key}: {counts.get(key)} != {value}")
    assets = data.get("source_assets")
    if not isinstance(assets, list) or not assets:
        fail("source asset status inventory is empty")
    manifest = set()
    for asset in assets:
        if not isinstance(asset, dict) or not re.fullmatch(r"project/.+", str(asset.get("path"))) or not re.fullmatch(r"[0-9a-f]{64}", str(asset.get("sha256"))):
            fail("invalid source asset reference")
        if asset.get("status") != "unknown" or asset.get("static_status") != "static-present":
            fail(f"source asset was promoted: {asset.get('path')}")
        manifest.add(asset["path"])
    if counts.get("source_file") != len(assets) or len(manifest) != len(assets):
        fail("source asset count or uniqueness mismatch")
    if not manifest:
        fail("covered source path set is empty")
    if source_index is not None:
        reference = json.loads(source_index.read_text(encoding="utf-8"))
        if data.get("source_index_sha256") != hashlib.sha256(source_index.read_bytes()).hexdigest():
            fail("source index hash mismatch")
        expected = {"endpoint": len(reference.get("endpoints", [])), "template": len(reference.get("templates", [])), "mapper": len(reference.get("mapper_xml", [])), "interface": len(reference.get("interfaces", []))}
        for kind, expected_count in expected.items():
            if counts.get(kind) != expected_count:
                fail(f"source index count mismatch for {kind}: {counts.get(kind)} != {expected_count}")
    seen = set()
    by_type = {kind: [] for kind in TYPES}
    for row in records:
        rid = row.get("record_id")
        if not re.fullmatch(r"(?:endpoint|template|mapper|interface|scheduled)\.\d{6}", str(rid)):
            fail(f"invalid record id: {rid}")
        if rid in seen:
            fail(f"duplicate record id: {rid}")
        seen.add(rid)
        by_type[row["asset_type"]].append(row)
        if row.get("asset_type") not in TYPES:
            fail(f"invalid asset type: {row.get('asset_type')}")
        if row.get("status") != "unknown" or row.get("dynamic_status") != "unknown":
            fail(f"static source record was promoted: {rid}")
        if row.get("static_status") != "static-present":
            fail(f"missing static-present status: {rid}")
        source = row.get("source", {})
        if not re.fullmatch(r"project/.+", str(source.get("path"))) or not re.fullmatch(r"[0-9a-f]{64}", str(source.get("sha256"))):
            fail(f"invalid source reference: {rid}")
        if not isinstance(source.get("line"), int) or source["line"] < 1:
            fail(f"invalid source line: {rid}")
        if source["path"] not in manifest:
            # A scheduled declaration can be scanned from a source checkout;
            # it is still required to appear in the source manifest.
            fail(f"source reference absent from covered path set: {source['path']}")
        if row.get("operation_family") not in {"read", "create", "update", "delete", "submit", "approve", "attachments", "reports", "import-export", "tasks", "integrations", "unknown"}:
            fail(f"invalid operation family: {rid}")
        joined = json.dumps(row, ensure_ascii=False)
        if SECRET.search(joined) or PRODUCTION.search(joined):
            fail(f"sensitive or production material in record: {rid}")
    if source_index is not None:
        ref_manifest = {item["path"]: item["sha256"] for item in reference["source_manifest"]}
        if {item["path"]: item["sha256"] for item in assets} != ref_manifest:
            fail("source asset inventory differs from pinned source manifest")
        for source_kind, kind in (("endpoints", "endpoint"), ("templates", "template"), ("mapper_xml", "mapper"), ("interfaces", "interface")):
            positions = [row.get("source_index") for row in by_type[kind]]
            if sorted(positions) != list(range(len(reference[source_kind]))):
                fail(f"source index positions missing or duplicated: {kind}")
            for row in by_type[kind]:
                original = reference[source_kind][row["source_index"]]
                if row["source"] != original["source"]:
                    fail(f"source reference differs from pinned index: {row['record_id']}")
                fields = {"endpoint": (("symbol", "symbol"), ("http_methods", "http_methods"), ("declared_paths", "declared_paths"), ("route_candidates", "candidate_routes")), "template": (("view_name", "view_name"),), "mapper": (("namespace", "namespace"), ("statements", "statements"), ("table_candidates", "table_candidates")), "interface": (("symbol", "symbol"), ("role", "role"), ("parents", "parents"))}[kind]
                for output_field, original_field in fields:
                    if row.get(output_field) != original.get(original_field, []):
                        fail(f"source declaration changed: {row['record_id']}:{output_field}")
    coverage = data.get("coverage", {})
    for key in ("unassigned_endpoint_template_indices", "unassigned_template_controller_indices", "unassigned_endpoint_service_indices", "unassigned_endpoint_mapper_indices", "unassigned_mapper_runtime_indices", "unassigned_interface_runtime_indices"):
        values = coverage.get(key)
        if not isinstance(values, list) or any(not isinstance(x, int) or x < 0 for x in values):
            fail(f"invalid coverage indices: {key}")
    groups = data.get("candidate_groups", [])
    if counts.get("candidate_group") != len(groups):
        fail("candidate group count mismatch")
    memberships = []
    for group in groups:
        if group.get("status") != "unknown" or group.get("menu") != "unknown" or group.get("permission") != "unknown":
            fail("static candidate group was promoted")
        memberships.extend(group.get("record_ids", []))
    if sorted(memberships) != sorted(seen):
        fail("candidate groups omit or duplicate source records")
    expected_unassigned = {
        "unassigned_endpoint_template_indices": [row["source_index"] for row in by_type["endpoint"] if not row.get("page_candidates")],
        "unassigned_endpoint_service_indices": list(range(counts["endpoint"])),
        "unassigned_endpoint_mapper_indices": list(range(counts["endpoint"])),
        "unassigned_mapper_runtime_indices": list(range(counts["mapper"])),
        "unassigned_interface_runtime_indices": list(range(counts["interface"])),
    }
    linked_templates = {link["template_index"] for link in data.get("controller_template_links", [])}
    expected_unassigned["unassigned_template_controller_indices"] = [i for i in range(counts["template"]) if i not in linked_templates]
    for key, expected_values in expected_unassigned.items():
        if coverage.get(key) != expected_values:
            fail(f"unassigned coverage differs from records: {key}")
    for candidate in data.get("integration_candidates", []):
        if candidate.get("status") != "unknown" or not candidate.get("source_refs"):
            fail("integration candidate was promoted or lacks source refs")
    print(f"full capability catalog valid: {len(records)} records; source paths={len(manifest)}")
    return 0


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("catalog", type=Path)
    ap.add_argument("--source-index", type=Path)
    args = ap.parse_args()
    try:
        return validate(args.catalog, args.source_index)
    except (OSError, ValueError, json.JSONDecodeError) as exc:
        print(f"full capability catalog invalid: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
