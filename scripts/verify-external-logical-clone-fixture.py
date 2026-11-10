#!/usr/bin/env python3
"""Verify the source-bound local-view external-schema fixture offline."""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "api-fixtures/schema/external-logical-clone.json"
TABLES = {
    "T_CRM_CONTRACT", "T_CRM_INVOICE_PROJ", "T_CRM_INVOICE",
    "T_CRM_PAY_PROJECT", "T_CRM_PAYMENT", "T_LIMS_CLEARANCEFORM",
    "T_LIMS_FOLDERS", "T_XFP_ORDER_TASK", "T_XFP_PROJECT", "T_XFP_REPORT",
}
FORBIDDEN = re.compile(r"(?i)\b(?:create\s+user|alter\s+user|database\s+link|insert\s+into|update\s+|delete\s+from|merge\s+into)\b")


def fail(message: str) -> None:
    raise SystemExit("external-logical-clone-fixture: FAIL: " + message)


def digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", type=Path, default=MANIFEST)
    args = parser.parse_args()
    manifest_path = args.manifest.resolve()
    try:
        manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(str(exc))
    if manifest.get("schema") != "external-logical-clone-fixture-v1":
        fail("unsupported fixture schema")
    if manifest.get("target_owner") != "LIMS_RUNTIME_LOCAL" or manifest.get("container") != "FREEPDB1":
        fail("fixture target must be LIMS_RUNTIME_LOCAL/FREEPDB1")
    if manifest.get("access_mode") != "local-view":
        fail("fixture must use local-view mode")
    if manifest.get("production_business_rows_read") is not False or manifest.get("fixture_business_rows") is not False:
        fail("fixture must not claim business rows")
    if manifest.get("db_link_created") is not False:
        fail("fixture must not create a DB link")
    listed = set(manifest.get("tables", []))
    if listed != TABLES:
        fail("table closure differs from approved ten-table set")
    for key in ("setup", "restore", "source_receipt"):
        entry = manifest.get(key)
        if not isinstance(entry, dict):
            fail(key + " receipt missing")
        path = ROOT / str(entry.get("path", ""))
        if not path.is_file():
            fail(key + " file missing: " + str(path))
        if digest(path) != entry.get("sha256"):
            fail(key + " sha256 mismatch")
    setup = (ROOT / manifest["setup"]["path"]).read_text(encoding="utf-8")
    restore = (ROOT / manifest["restore"]["path"]).read_text(encoding="utf-8")
    if FORBIDDEN.search(setup) or FORBIDDEN.search(restore):
        fail("fixture contains business DML, user, or DB-link statement")
    created = set(re.findall(r"(?im)^\s*create\s+table\s+\"?([A-Z][A-Z0-9_$#]*)\"?", setup))
    if created != TABLES:
        fail("setup CREATE TABLE closure differs from approved set")
    dropped = set(re.findall(r"(?im)^\s*drop\s+table\s+\"?([A-Z][A-Z0-9_$#]*)\"?", restore))
    if dropped != TABLES:
        fail("restore DROP TABLE closure differs from approved set")
    print(json.dumps({"status": "pass", "manifest": str(manifest_path.relative_to(ROOT)), "tables": len(TABLES), "mode": manifest["access_mode"]}))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
