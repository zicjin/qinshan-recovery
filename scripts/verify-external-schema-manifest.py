#!/usr/bin/env python3
"""Validate the source-owned external-schema clone manifest without connecting.

This gate checks topology and provenance only. It deliberately cannot create a
database link, read a link definition, import business rows, or claim that an
isolated logical clone is ready.
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
        fail("manifest must remain blocked until isolated logical clones resolve")
    if manifest.get("strict_evidence") is not False or manifest.get("business_rows_read") is not False or manifest.get("writes") is not False:
        fail("manifest claims unsafe or strict activity")
    production_reference = manifest.get("production_reference")
    if not isinstance(production_reference, Mapping):
        fail("production_reference is required")
    if production_reference.get("access_mechanism") != "oracle-db-link":
        fail("production link transport must remain explicit provenance")
    if production_reference.get("required_for_baseline") is not True:
        fail("production link topology is required as baseline provenance")
    if production_reference.get("required_for_clone") is not False:
        fail("DB-link transport must remain optional for a logical clone")
    clone_contract = manifest.get("clone_contract")
    if not isinstance(clone_contract, Mapping):
        fail("clone_contract is required")
    if clone_contract.get("logical_semantics_required") is not True:
        fail("logical clone semantics must remain required")
    if clone_contract.get("db_link_transport_required") is not False:
        fail("clone contract must not require DB-link transport")
    if clone_contract.get("qualifier_rewrite") != "explicit-allowlist-only":
        fail("SQL qualifier rewrites must use the explicit allowlist")
    if clone_contract.get("same_object_names_required") is not True:
        fail("clone must preserve source object names")
    if set(clone_contract.get("allowed_access_modes", [])) != {"direct-schema", "local-view"}:
        fail("clone access modes must be direct-schema/local-view")
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
        clone_access = domain.get("clone_access")
        if not isinstance(clone_access, Mapping):
            fail(f"{name} clone_access missing")
        if set(clone_access.get("allowed_modes", [])) != {"direct-schema", "local-view"}:
            fail(f"{name} clone access modes mismatch")
        if clone_access.get("selected_mode") not in (None, "direct-schema", "local-view"):
            fail(f"{name} clone selected mode is invalid")
        runtime_schema = clone_access.get("runtime_schema")
        if runtime_schema is not None and not re.fullmatch(r"[A-Z][A-Z0-9_$#]{0,29}", str(runtime_schema)):
            fail(f"{name} runtime schema is invalid")
        if set(clone_access.get("production_qualifiers", [])) != expected["links"]:
            fail(f"{name} production qualifier allowlist mismatch")
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
    resolution = manifest.get("resolution")
    if not isinstance(resolution, Mapping) or resolution.get("status") != "resolved-structure-only":
        fail("resolution must explicitly remain structure-only")
    if resolution.get("access_mode") != "local-view":
        fail("resolved access mode must be local-view")
    for name, schema in (("local_runtime", "LIMS_RUNTIME_LOCAL"), ("supplier_test", "LIMS2_CZJKY_EMC_DEV")):
        item = resolution.get(name)
        if not isinstance(item, Mapping) or item.get("schema") != schema:
            fail(name + " resolution schema mismatch")
        if item.get("business_rows") != 0 or item.get("db_links") != 0:
            fail(name + " resolution must have zero business rows and DB links")
        receipt_path = ROOT / str(item.get("receipt", ""))
        if not receipt_path.is_file():
            fail(name + " resolution receipt missing")
        if not any(r.get("path") == item.get("receipt") and r.get("sha256") == hashlib.sha256(receipt_path.read_bytes()).hexdigest() for r in manifest.get("private_receipts", [])):
            fail(name + " resolution receipt is not registered")
    if not INVENTORY.is_file() or not REMOTE_METADATA.is_file():
        fail("required private inventory/metadata receipt missing")
    print(json.dumps({"status": "pass", "manifest": str(args.manifest.relative_to(ROOT)), "status_class": manifest["status"], "domains": sorted(domains)}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
