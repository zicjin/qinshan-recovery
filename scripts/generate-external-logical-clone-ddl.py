#!/usr/bin/env python3
"""Render metadata-only DDL for the approved external logical-clone tables.

This command is deliberately offline.  It reads a JSON metadata receipt (the
receipt produced from a read-only production dictionary query) and writes
deterministic SQL plus a small JSON report.  It never opens a database
connection, reads business rows, creates schemas, or emits DB-link statements.

The source receipt currently contains three logical domains.  Only the tables
listed in ``APPROVED_OBJECTS`` and the production owner selected below are
eligible for rendering.  Missing Oracle features are reported as blocked gaps
instead of being guessed.
"""
from __future__ import annotations

import argparse
from collections import defaultdict
import json
from pathlib import Path
import re
import sys
from typing import Any, Iterable, Mapping, Sequence


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_RECEIPT = ROOT / "private/production-baseline-20261110/external-schema-remote-full-metadata-private.json"
DEFAULT_SQL = ROOT / "private/external-schema-logical-clone-ddl-dry-run.sql"
DEFAULT_REPORT = ROOT / "private/external-schema-logical-clone-ddl-dry-run.json"

APPROVED_OBJECTS: Mapping[str, tuple[str, ...]] = {
    "HT": (
        "T_CRM_CONTRACT",
        "T_CRM_INVOICE_PROJ",
        "T_CRM_INVOICE",
        "T_CRM_PAY_PROJECT",
        "T_CRM_PAYMENT",
    ),
    "SP": ("T_LIMS_CLEARANCEFORM", "T_LIMS_FOLDERS"),
    "XFP": ("T_XFP_ORDER_TASK", "T_XFP_PROJECT", "T_XFP_REPORT"),
}

# SP receipts include archival/test owners in addition to the production
# owner.  Selecting an owner is required; merging columns from different
# owners would produce a schema that never existed.
APPROVED_OWNERS: Mapping[str, str] = {
    "HT": "LIMS_CZJKY_HT",
    "SP": "LIMS_CZJKY_SP",
    "XFP": "LIMS_CZJKY_XF",
}

# Current Oracle deployments may use 30-character identifiers while newer
# compatible databases allow 128.  Metadata names are accepted up to the
# latter limit; the target runtime still decides whether a name is legal.
IDENTIFIER = re.compile(r"^[A-Z][A-Z0-9_$#]{0,127}$")
SAFE_TYPE = re.compile(r"^[A-Z][A-Z0-9]*(?:\([0-9]+\))?(?:\s+[A-Z]+)?$")


class DdlError(ValueError):
    pass


def load_json(path: Path) -> Mapping[str, Any]:
    value = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(value, Mapping):
        raise DdlError("receipt root must be an object")
    return value


def qident(value: str) -> str:
    """Quote an Oracle identifier after rejecting malformed metadata."""
    value = str(value).upper()
    if not IDENTIFIER.fullmatch(value):
        raise DdlError(f"unsafe identifier in receipt: {value!r}")
    return f'"{value}"'


def rows_for(section: Any) -> list[list[Any]]:
    if not isinstance(section, Mapping):
        return []
    rows = section.get("rows", [])
    if not isinstance(rows, list):
        return []
    return [row for row in rows if isinstance(row, list)]


def section_status(metadata: Mapping[str, Any], name: str) -> str:
    section = metadata.get(name)
    return str(section.get("status")) if isinstance(section, Mapping) else "missing"


def data_type(row: Sequence[Any]) -> str:
    # ALL_TAB_COLUMNS fields in the receipt:
    # owner, table, column_id, column, data_type, data_length,
    # data_precision, data_scale, nullable, char_used, char_length, default_length
    if len(row) < 12:
        raise DdlError("column row has fewer than 12 fields")
    kind = str(row[4] or "").upper()
    length = row[5]
    precision = row[6]
    scale = row[7]
    char_used = str(row[9] or "").upper()
    char_length = row[10]
    if kind in {"VARCHAR2", "NVARCHAR2", "CHAR", "NCHAR", "RAW"}:
        amount = char_length if char_used == "C" and char_length not in (None, "") else length
        if amount in (None, ""):
            raise DdlError(f"length missing for {kind} column {row[3]}")
        suffix = " CHAR" if char_used == "C" else " BYTE"
        return f"{kind}({int(amount)}{suffix})"
    if kind == "NUMBER":
        if precision in (None, ""):
            return "NUMBER"
        return f"NUMBER({int(precision)},{int(scale or 0)})"
    if kind.startswith("TIMESTAMP"):
        # DATA_TYPE is already e.g. TIMESTAMP(6) in Oracle dictionary views.
        return kind
    if kind in {"DATE", "CLOB", "BLOB", "NCLOB", "FLOAT", "LONG", "LONG RAW"}:
        return kind
    raise DdlError(f"unsupported Oracle data type {kind!r} for {row[1]}.{row[3]}")


def constraint_rows(metadata: Mapping[str, Any], owner: str, table: str) -> tuple[list[list[Any]], list[list[Any]]]:
    constraints = [r for r in rows_for(metadata.get("constraints")) if len(r) >= 12 and str(r[0]).upper() == owner and str(r[1]).upper() == table]
    columns = [r for r in rows_for(metadata.get("constraint_columns")) if len(r) >= 5 and str(r[0]).upper() == owner and str(r[1]).upper() == table]
    return constraints, columns


def index_rows(metadata: Mapping[str, Any], owner: str, table: str) -> tuple[list[list[Any]], list[list[Any]]]:
    indexes = [r for r in rows_for(metadata.get("indexes")) if len(r) >= 6 and str(r[0]).upper() == owner and str(r[5]).upper() == table]
    columns = [r for r in rows_for(metadata.get("index_columns")) if len(r) >= 7 and str(r[2]).upper() == owner and str(r[3]).upper() == table]
    return indexes, columns


def render_table(
    schema: str,
    owner: str,
    table: str,
    metadata: Mapping[str, Any],
    blocked: list[dict[str, Any]],
) -> tuple[list[str], dict[str, int]]:
    column_rows = [
        r for r in rows_for(metadata.get("columns"))
        if len(r) >= 12 and str(r[0]).upper() == owner and str(r[1]).upper() == table
    ]
    column_rows.sort(key=lambda r: int(r[2]))
    if not column_rows:
        raise DdlError(f"approved table has no columns in owner {owner}: {table}")

    lines = [f"CREATE TABLE {qident(schema)}.{qident(table)} ("]
    definitions: list[str] = []
    for row in column_rows:
        definition = f"    {qident(str(row[3]))} {data_type(row)}"
        if str(row[8]).upper() == "N":
            definition += " NOT NULL"
        definitions.append(definition)
        if row[11] not in (None, "", 0, "0"):
            blocked.append({
                "kind": "column-default",
                "domain_owner": owner,
                "table": table,
                "column": str(row[3]).upper(),
                "reason": "default_length is present but the default expression is not in the receipt",
            })

    constraints, constraint_columns = constraint_rows(metadata, owner, table)
    by_constraint: defaultdict[str, list[tuple[int, str]]] = defaultdict(list)
    for row in constraint_columns:
        position = int(row[4]) if row[4] not in (None, "") else 10**9
        by_constraint[str(row[2]).upper()].append((position, str(row[3]).upper()))

    emitted_constraints: list[str] = []
    constraint_names: set[str] = set()
    for row in sorted(constraints, key=lambda r: str(r[2]).upper()):
        name = str(row[2]).upper()
        kind = str(row[3]).upper()
        status = str(row[4]).upper()
        validated = str(row[5]).upper()
        if kind not in {"P", "U", "C"}:
            blocked.append({"kind": "constraint", "table": table, "name": name, "reason": f"constraint type {kind} is not rendered"})
            continue
        if status != "ENABLED" or validated != "VALIDATED":
            blocked.append({"kind": "constraint", "table": table, "name": name, "reason": f"constraint status/validation {status}/{validated} is not rendered"})
            continue
        cols = [column for _, column in sorted(by_constraint.get(name, []))]
        if kind in {"P", "U"}:
            if not cols:
                blocked.append({"kind": "constraint", "table": table, "name": name, "reason": "constraint columns missing"})
                continue
            keyword = "PRIMARY KEY" if kind == "P" else "UNIQUE"
            emitted_constraints.append(f"    CONSTRAINT {qident(name)} {keyword} ({', '.join(qident(c) for c in cols)})")
            constraint_names.add(name)
        else:
            condition = row[11]
            if not condition:
                blocked.append({"kind": "constraint", "table": table, "name": name, "reason": "check expression missing"})
                continue
            # SEARCH_CONDITION_VC is sourced from the dictionary.  Reject
            # statement-like values so a receipt cannot turn this renderer into
            # an arbitrary SQL emitter.
            condition_text = str(condition).strip()
            if ";" in condition_text or "--" in condition_text or "/*" in condition_text:
                raise DdlError(f"unsafe check expression for {table}.{name}")
            emitted_constraints.append(f"    CONSTRAINT {qident(name)} CHECK ({condition_text})")
            constraint_names.add(name)

    # Commas are inserted after every column definition except the final
    # definition when no table-level constraint follows; this keeps the output
    # valid Oracle DDL while preserving deterministic ordering.
    all_definitions = definitions + emitted_constraints
    lines.extend(item + ("," if index < len(all_definitions) - 1 else "")
                 for index, item in enumerate(all_definitions))
    lines.append(");")

    indexes, index_columns = index_rows(metadata, owner, table)
    by_index: defaultdict[str, list[tuple[int, str, str]]] = defaultdict(list)
    for row in index_columns:
        position = int(row[5]) if row[5] not in (None, "") else 10**9
        by_index[str(row[1]).upper()].append((position, str(row[4]).upper(), str(row[6]).upper()))
    index_count = 0
    for row in sorted(indexes, key=lambda r: str(r[1]).upper()):
        name = str(row[1]).upper()
        if name in constraint_names:
            blocked.append({"kind": "index", "table": table, "name": name, "reason": "constraint-backed index is represented by the table constraint"})
            continue
        if str(row[4]).upper() != "VALID":
            blocked.append({"kind": "index", "table": table, "name": name, "reason": f"index status {row[4]} is not rendered"})
            continue
        cols = sorted(by_index.get(name, []))
        if not cols:
            blocked.append({"kind": "index", "table": table, "name": name, "reason": "index columns missing"})
            continue
        if str(row[2]).upper() != "NORMAL":
            blocked.append({"kind": "index", "table": table, "name": name, "reason": f"index type {row[2]} is not rendered"})
            continue
        uniqueness = "UNIQUE " if str(row[3]).upper() == "UNIQUE" else ""
        rendered_cols = []
        for _, column, descend in cols:
            rendered_cols.append(qident(column) + (" DESC" if descend == "DESC" else ""))
        lines.append(f"CREATE {uniqueness}INDEX {qident(name)} ON {qident(schema)}.{qident(table)} ({', '.join(rendered_cols)});")
        index_count += 1

    return lines, {"columns": len(column_rows), "constraints": len(constraint_names), "indexes": index_count}


def generate(receipt: Mapping[str, Any], schema_prefix: str) -> tuple[str, dict[str, Any]]:
    if receipt.get("schema") != "external-schema-remote-full-metadata-v1":
        # The current receipt has no explicit versioned schema key; accept the
        # known metadata shape while rejecting unrelated JSON inputs.
        if not isinstance(receipt.get("domains"), Mapping):
            raise DdlError("unsupported metadata receipt")
    domains = receipt.get("domains")
    if not isinstance(domains, Mapping):
        raise DdlError("receipt domains must be an object")
    blocked: list[dict[str, Any]] = [
        {"kind": "transport", "reason": "Oracle DB-link transport is not emitted; production link names remain provenance only"},
        {"kind": "business-rows", "reason": "business rows are never read or emitted by this metadata-only renderer"},
        {"kind": "ddl-package", "reason": "DBMS_METADATA DDL acquisition is blocked in the production receipt (ORA-31603)"},
        {"kind": "procedural-metadata", "reason": "types/procedures/arguments/grants metadata is blocked in the production receipt (ORA-00904)"},
        {"kind": "non-table-objects", "reason": "views, triggers, packages, functions, procedures, and grants are not rendered by this table-only generator"},
    ]
    sql: list[str] = [
        "-- GENERATED METADATA-ONLY DRY RUN; DO NOT EXECUTE WITHOUT REVIEW",
        "-- No Oracle connection, DB link, business row, credential, or endpoint was used.",
        "-- Table owners/columns/constraints/indexes come only from the approved private metadata receipt.",
        "",
    ]
    rendered: list[dict[str, Any]] = []
    for domain in APPROVED_OBJECTS:
        value = domains.get(domain)
        if not isinstance(value, Mapping):
            raise DdlError(f"missing approved domain {domain}")
        metadata = value.get("metadata")
        if not isinstance(metadata, Mapping):
            raise DdlError(f"{domain} metadata is missing")
        owner = APPROVED_OWNERS[domain]
        schema = f"{schema_prefix}_{domain}"
        if not IDENTIFIER.fullmatch(schema):
            raise DdlError(f"invalid generated schema name {schema!r}")
        objects = set(str(item).upper() for item in value.get("target_objects", []))
        if objects != set(APPROVED_OBJECTS[domain]):
            raise DdlError(f"{domain} target object closure differs from approved set")
        for table in APPROVED_OBJECTS[domain]:
            table_lines, counts = render_table(schema, owner, table, metadata, blocked)
            sql.extend(table_lines)
            sql.append("")
            rendered.append({"domain": domain, "source_owner": owner, "schema": schema, "table": table, **counts})
        for section in ("types", "procedures", "arguments", "grants"):
            status = section_status(metadata, section)
            if status not in {"blocked", "missing"}:
                blocked.append({"kind": "metadata-status", "domain": domain, "section": section, "reason": f"receipt status {status!r} is not a blocked gap"})

    report = {
        "schema": "external-schema-logical-clone-ddl-dry-run-v1",
        "source_receipt_schema": receipt.get("schema"),
        "mode": "metadata-only",
        "database_connection": False,
        "business_rows_read": False,
        "db_link_ddl_emitted": False,
        "schema_prefix": schema_prefix,
        "approved_domains": list(APPROVED_OBJECTS),
        "approved_tables": rendered,
        "blocked_gaps": blocked,
    }
    return "\n".join(sql).rstrip() + "\n", report


def main(argv: Iterable[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--receipt", type=Path, default=DEFAULT_RECEIPT)
    parser.add_argument("--output", type=Path, default=DEFAULT_SQL)
    parser.add_argument("--manifest-output", type=Path, default=DEFAULT_REPORT)
    parser.add_argument("--schema-prefix", default="LIMS_EXT")
    args = parser.parse_args(list(argv) if argv is not None else None)
    try:
        receipt = load_json(args.receipt)
        sql, report = generate(receipt, str(args.schema_prefix).upper())
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.manifest_output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(sql, encoding="utf-8")
        args.manifest_output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    except (OSError, json.JSONDecodeError, DdlError, TypeError, ValueError) as exc:
        print(f"external-logical-clone-ddl: FAIL: {exc}", file=sys.stderr)
        return 2
    print(json.dumps({"status": "pass", "sql": str(args.output), "manifest": str(args.manifest_output), "tables": len(report["approved_tables"]), "blocked_gaps": len(report["blocked_gaps"])}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
