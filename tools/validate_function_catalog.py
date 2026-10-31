#!/usr/bin/env python3
"""Validate the redacted, user-capability function coverage catalog.

The validator is deliberately dependency-free. It checks the catalog contract,
keeps unknown and isolation-disabled states explicit, and rejects credential or
production-endpoint material before the file can be used as evidence.
"""
from __future__ import annotations
import argparse, csv, re, sys
from pathlib import Path

REQUIRED = (
    "feature_id", "domain", "capability", "menu_path", "routes", "page_resources",
    "handlers", "services", "mappers", "db_entities", "permission",
    "integration_category", "evidence", "status", "state_history", "confidence", "gaps",
    "scope", "historical_status", "provenance",
)
STATES = {
    "static-present", "menu-observed", "page-opened", "read-flow-verified",
    "write-flow-verified", "permission-checked", "disabled-in-isolated-env", "unknown",
}
STATUSES = STATES
REQUIRED_DOMAINS = {"identity", "personnel", "equipment", "consumables", "attachments", "reports", "tasks", "integrations"}
SECRET = re.compile(r"(?i)(password|passwd|token|secret|authorization|bearer|private[_ -]?key)\s*[:=]")
PRODUCTION = re.compile(r"(?i)(10\.100\.|218\.93\.|192\.168\.|ssh\s+sss|https?://[^ ]*production)")
REPO_REFERENCE_PREFIXES = ("project/", "owned-", "scripts/", "tools/", "docs/", "evidence/", ".scratch/")
INTEGRATION_CATEGORIES = {"erp", "oa", "ftp", "mail", "reporting-service", "http-client", "fanruan", "pageoffice"}


def repo_references(value: str) -> list[str]:
    """Return exact repository-relative references from a semicolon field.

    The ledger uses ``unknown`` when a relationship is not evidenced.  Once a
    relationship is known, multiple source/evidence files are represented as
    a semicolon-separated list.  Keeping the parser here prevents whitespace
    and list handling from silently weakening validation.
    """
    return [item.strip() for item in value.split(";") if item.strip()]


def validate_repo_reference(value: str, root: Path, label: str, field: str = "") -> None:
    for reference in repo_references(value):
        if reference == "unknown":
            continue
        if field == "integration_category" and reference in INTEGRATION_CATEGORIES:
            continue
        if (reference.startswith("/") or ".." in Path(reference).parts
                or not any(reference.startswith(prefix) for prefix in REPO_REFERENCE_PREFIXES)):
            fail(f"{label} must be unknown or a repository-relative source/evidence path: {reference}")
        target = root.parent / reference if reference.startswith(".scratch/") else root / reference
        if not target.is_file() or target.is_symlink():
            fail(f"{label} reference does not identify a regular file: {reference}")

def fail(msg: str) -> None:
    raise ValueError(msg)

def validate(path: Path, source_inventory: Path | None = None) -> int:
    if not path.is_file() or path.is_symlink(): fail(f"catalog is not a regular file: {path}")
    # Catalogs may be copied to a temporary directory by the contract tests;
    # references still resolve against this recovery checkout, not the copy.
    root = Path(__file__).resolve().parents[1]
    with path.open("r", encoding="utf-8", newline="") as fh:
        rows = list(csv.DictReader(fh, delimiter="\t"))
    if not rows: fail("catalog has no feature rows")
    if tuple(rows[0].keys()) != REQUIRED: fail("catalog header does not match the required schema")
    seen = set()
    domains = set()
    for number, row in enumerate(rows, 2):
        feature = row["feature_id"].strip()
        if not re.fullmatch(r"[a-z][a-z0-9_.-]+", feature): fail(f"row {number}: invalid feature_id")
        if feature in seen: fail(f"row {number}: duplicate feature_id {feature}")
        seen.add(feature)
        domain = row["domain"].strip(); domains.add(domain)
        for field in REQUIRED:
            if not row[field].strip(): fail(f"row {number}: empty {field}")
        status = row["status"].strip()
        if row["scope"].strip() != "owned-runtime": fail(f"row {number}: scope must be owned-runtime")
        if status not in STATUSES: fail(f"row {number}: invalid status {status}")
        if status not in {"unknown", "static-present"} and "owned-runtime-evidence:" not in row["provenance"]:
            fail(f"row {number}: a runtime status requires explicit owned-runtime-evidence provenance")
        historical = row["historical_status"].strip()
        if historical not in STATUSES: fail(f"row {number}: invalid historical_status {historical}")
        for field in ("menu_path", "routes", "page_resources", "handlers", "services", "mappers", "db_entities", "permission", "integration_category"):
            validate_repo_reference(row[field], root, f"row {number}: {field}", field)
        history = {value for value in row["state_history"].split(";") if value}
        if not history <= STATES: fail(f"row {number}: unknown state in state_history")
        if status not in history and status != "unknown":
            fail(f"row {number}: status is absent from state_history")
        if status == "unknown" and "static-present" not in history:
            fail(f"row {number}: unknown feature must retain static-present evidence")
        if status == "disabled-in-isolated-env" and "disabled-in-isolated-env" not in history:
            fail(f"row {number}: disabled status must be explicit in state_history")
        evidence = row["evidence"]
        for ref in evidence.split(";"):
            ref = ref.strip()
            if ref.startswith("evidence/") and ".." not in Path(ref).parts:
                continue
            if ref.startswith(".scratch/") and ".." not in Path(ref).parts:
                candidate = root.parent / ref
                if not candidate.is_file():
                    fail(f"row {number}: archived evidence path does not exist: {ref}")
                continue
            fail(f"row {number}: evidence must be a repository-relative evidence path")
        joined = "\t".join(row.values())
        if SECRET.search(joined): fail(f"row {number}: credential-like key/value material is forbidden")
        if PRODUCTION.search(joined): fail(f"row {number}: production endpoint material is forbidden")
    missing = REQUIRED_DOMAINS - domains
    if missing: fail("catalog is missing required domains: " + ", ".join(sorted(missing)))
    if not any(row["historical_status"] == "read-flow-verified" for row in rows): fail("catalog has no historical verified read flow")
    if not any(row["historical_status"] == "disabled-in-isolated-env" for row in rows): fail("catalog has no historical isolated-disabled row")
    if source_inventory is not None:
        if not source_inventory.is_file(): fail(f"source inventory missing: {source_inventory}")
        with source_inventory.open("r", encoding="utf-8", newline="") as fh:
            header = fh.readline().rstrip("\n").split("\t")
        if header[:2] != ["path", "kind"]: fail("source inventory header is not recognized")
    print(f"function catalog valid: {len(rows)} features; domains={len(domains)}")
    return 0

def main(argv=None):
    ap = argparse.ArgumentParser()
    ap.add_argument("catalog", nargs="?", type=Path, default=Path("evidence/function-coverage-catalog.tsv"))
    ap.add_argument("--source-inventory", type=Path)
    args = ap.parse_args(argv)
    try: return validate(args.catalog, args.source_inventory)
    except (OSError, ValueError) as exc:
        print(f"function catalog invalid: {exc}", file=sys.stderr); return 1
if __name__ == "__main__": sys.exit(main())
