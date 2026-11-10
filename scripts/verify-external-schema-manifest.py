#!/usr/bin/env python3
"""Validate the source-owned external-schema clone manifest without connecting.

This gate checks topology and provenance only. It deliberately cannot create a
database link, read a link definition, import business rows, or claim that an
isolated clone is ready.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re
from typing import Any, Mapping

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "api-fixtures/schema/external-schema-clone-manifest.json"
INVENTORY = ROOT / "private/external-schema-clone-inventory-20261110.json"
REMOTE_METADATA = ROOT / "private/production-baseline-20261110/external-schema-remote-metadata-private.json"

EXPECTED = {
    "HT": {
        "links": {"LIMS_CZJKY_HT", "LIMS_CZJKY_HT2"},
        "objects": {"T_CRM_CONTRACT", "T_CRM_INVOICE_PROJ", "T_CRM_INVOICE", "T_CRM_PAY_PROJECT", "T_CRM_PAYMENT"},
    },
    "SP": {
        "links": {"LIMS_CZJKY_SP"},
        "objects": {"T_LIMS_CLEARANCEFORM", "T_LIMS_FOLDERS"},
    },
    "XFP": {
        "links": {"LIMS_CZJKY_XFP"},
        "objects": {"T_XFP_ORDER_TASK", "T_XFP_PROJECT", "T_XFP_REPORT"},
    },
}
SECRET = re.compile(r"(?i)(password|passwd|secret|token|private[_-]?key|wallet|jdbc:|oracle:thin|@//|10\.100\.|218\.93\.)")


def load(path: Path) -> Mapping[str, Any]:
    return json.loads(path.read_text(encoding="utf-8"))


def fail(message: str) -> None:
    raise SystemExit("external-schema-manifest: FAIL: " + message)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", type=Path, default=MANIFEST)
    args = parser.parse_args()
    manifest = load(args.manifest)
    if manifest.get("schema") != "external-schema-clone-manifest-v1":
        fail("unsupported schema")
    if manifest.get("status") != "environment-not-ready-external-schema":
        fail("manifest must remain blocked until isolated links resolve")
    if manifest.get("strict_evidence") is not False or manifest.get("business_rows_read") is not False or manifest.get("writes") is not False:
        fail("manifest claims unsafe or strict activity")
    checked = {key: value for key, value in manifest.items()
               if key not in {"required_before_provisioning", "forbidden_inputs"}}
    text = json.dumps(checked, ensure_ascii=False)
    if SECRET.search(text):
        fail("secret, endpoint or connection material found")
    domains = {str(d.get("logical_domain")): d for d in manifest.get("domains", [])}
    if set(domains) != set(EXPECTED):
        fail("domain set must be exactly HT/SP/XFP")
    for name, expected in EXPECTED.items():
        domain = domains[name]
        links = {str(item.get("name")) for item in domain.get("link_topology", [])}
        objects = set(domain.get("objects", []))
        if links != expected["links"]:
            fail(f"{name} link topology mismatch")
        if objects != expected["objects"]:
            fail(f"{name} remote object closure mismatch")
        counts = domain.get("metadata_counts", {})
        for key in ("objects", "columns", "constraints", "constraint_columns", "indexes", "index_columns", "triggers"):
            if not isinstance(counts.get(key), int) or counts[key] < 0:
                fail(f"{name} metadata count missing: {key}")
        for key in ("owner_objects", "views", "sequences", "dependency_edges"):
            if not isinstance(counts.get(key), int) or counts[key] < 0:
                fail(f"{name} full metadata count missing: {key}")
        if counts.get("procedural_types_grants") != "blocked-remote-dictionary-ora-00904":
            fail(f"{name} procedural/type/grant gap must remain explicit")
        if counts.get("grants") != "blocked-remote-dictionary-ora-00904":
            fail(f"{name} grant gap must remain explicit")
    for receipt in manifest.get("private_receipts", []):
        path = ROOT / str(receipt.get("path", ""))
        if not path.is_file():
            fail("private receipt missing: " + str(path))
        if hashlib.sha256(path.read_bytes()).hexdigest() != receipt.get("sha256"):
            fail("private receipt hash mismatch: " + str(path))
    if not INVENTORY.is_file() or not REMOTE_METADATA.is_file():
        fail("required private inventory/metadata receipt missing")
    print(json.dumps({"status": "pass", "manifest": str(args.manifest.relative_to(ROOT)), "status_class": manifest["status"], "domains": sorted(domains)}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
