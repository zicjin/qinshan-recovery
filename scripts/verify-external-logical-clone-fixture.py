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



ALIGNMENT_SCHEMA = "external-production-structure-alignment-v1"
SOURCE_DIRECTORY = "deployment/oracle/external-schema/"
ALIGNMENT_SOURCES = ("structure.json", "manifest.json", "20-tables.sql", "30-indexes.sql", "35-constraints.sql", "90-verify.sql")


def sql_literal(value):
    return "NULL" if value is None else "'" + str(value).replace("'", "''") + "'"


def source_defaults(structure):
    return [{"table": t["name"], "column": c["name"], "expression": c["default"].strip()}
            for t in structure["tables"] for c in t["columns"] if c["default"] is not None]


def render_alignment(structure):
    """Render a finite defaults-only migration from the formal selected-table source."""
    defaults = source_defaults(structure)
    semantic = {t["name"]: t for t in structure["semantic_contract"]["tables"]}
    sql = ["""-- Stage 1 only: 33 production defaults; NOT NULL/index differences remain unresolved.
-- Source: deployment/oracle/external-schema/{structure.json,20-tables.sql,90-verify.sql}.
-- Keep application/schema writers idle. Oracle DDL implicitly commits; retain all logs.
-- No business DML, constraint replacement, index rebuild, cache or service operation.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT FAILURE ROLLBACK
SET DEFINE OFF
SET SQLBLANKLINES ON
SET SERVEROUTPUT ON
ALTER SESSION SET DDL_LOCK_TIMEOUT=5;
DECLARE
  n NUMBER;
  applied NUMBER := 0;
  skipped NUMBER := 0;
  require_defaults BOOLEAN := FALSE;

  FUNCTION default_missing(p_table VARCHAR2, p_column VARCHAR2, p_expected VARCHAR2)
    RETURN BOOLEAN IS
    default_text LONG;
    default_size NUMBER;
  BEGIN
    SELECT DATA_DEFAULT, DEFAULT_LENGTH INTO default_text, default_size
      FROM USER_TAB_COLS WHERE TABLE_NAME=p_table AND COLUMN_NAME=p_column;
    IF default_text IS NULL AND default_size IS NULL THEN RETURN TRUE; END IF;
    IF p_expected IS NULL OR default_text IS NULL OR default_size IS NULL
        OR TRIM(default_text) IS NULL OR TRIM(default_text) <> p_expected THEN
      RAISE_APPLICATION_ERROR(-20230,'Default conflicts: '||p_table||'.'||p_column);
    END IF;
    RETURN FALSE;
  END;

  PROCEDURE check_column(p_table VARCHAR2, p_column VARCHAR2,
                         p_shape VARCHAR2, p_default VARCHAR2) IS
    actual_shape VARCHAR2(2000);
    missing BOOLEAN;
  BEGIN
    SELECT COLUMN_ID||'|'||DATA_TYPE||'|'||NVL(DATA_TYPE_OWNER,'-')||'|'||
      NVL(TO_CHAR(DATA_PRECISION),'-')||'|'||NVL(TO_CHAR(DATA_SCALE),'-')||'|'||NULLABLE||'|'||
      CASE WHEN DATA_TYPE IN ('VARCHAR2','CHAR','NVARCHAR2','NCHAR','RAW') THEN TO_CHAR(DATA_LENGTH) ELSE '-' END||'|'||
      CASE WHEN DATA_TYPE IN ('VARCHAR2','CHAR','NVARCHAR2','NCHAR') THEN CHAR_USED||'|'||TO_CHAR(CHAR_LENGTH) ELSE '-|-' END||'|'||
      HIDDEN_COLUMN||'|'||VIRTUAL_COLUMN||'|'||IDENTITY_COLUMN||'|'||DEFAULT_ON_NULL||'|'||USER_GENERATED||'|'||NVL(COLLATION,'-')
      INTO actual_shape FROM USER_TAB_COLS WHERE TABLE_NAME=p_table AND COLUMN_NAME=p_column;
    IF actual_shape <> p_shape THEN
      RAISE_APPLICATION_ERROR(-20231,'Column conflicts: '||p_table||'.'||p_column);
    END IF;
    missing := default_missing(p_table,p_column,p_default);
    IF require_defaults AND missing AND p_default IS NOT NULL THEN
      RAISE_APPLICATION_ERROR(-20235,'Default missing after alignment: '||p_table||'.'||p_column);
    END IF;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RAISE_APPLICATION_ERROR(-20231,'Column missing: '||p_table||'.'||p_column);
  END;

  PROCEDURE assert_preflight IS
    n NUMBER;
  BEGIN
    IF SYS_CONTEXT('USERENV','SESSION_USER') <> 'LIMS_RUNTIME_LOCAL'
        OR SYS_CONTEXT('USERENV','CURRENT_SCHEMA') <> 'LIMS_RUNTIME_LOCAL'
        OR SYS_CONTEXT('USERENV','CON_NAME') <> 'FREEPDB1' THEN
      RAISE_APPLICATION_ERROR(-20232,'Wrong local alignment target');
    END IF;
"""]
    nls = structure["semantic_contract"]["domains"]["HT"]
    for view, key in (("NLS_DATABASE_PARAMETERS", "database_nls"), ("NLS_SESSION_PARAMETERS", "link_session_nls")):
        for name, value in nls[key].items():
            sql.extend([f"    SELECT COUNT(*) INTO n FROM {view} WHERE PARAMETER={sql_literal(name)} AND VALUE={sql_literal(value)};\n",
                        f"    IF n <> 1 THEN RAISE_APPLICATION_ERROR(-20233,'NLS conflicts: {name}'); END IF;\n"])
    for table in structure["tables"]:
        name = table["name"]
        attrs = semantic[name]["attributes"]
        predicates = ["TABLE_NAME=" + sql_literal(name)] + [key + (" IS NULL" if value is None else "=" + sql_literal(value)) for key, value in attrs.items()]
        sql.append("    SELECT COUNT(*) INTO n FROM USER_TABLES WHERE " + " AND ".join(predicates) + ";\n")
        sql.append(f"    IF n <> 1 THEN RAISE_APPLICATION_ERROR(-20234,'Table conflicts: {name}'); END IF;\n")
        sql.append(f"    SELECT COUNT(*) INTO n FROM USER_TAB_COLS WHERE TABLE_NAME='{name}';\n")
        sql.append(f"    IF n <> {len(table['columns'])} THEN RAISE_APPLICATION_ERROR(-20234,'Column closure conflicts: {name}'); END IF;\n")
        sql.append(f"    SELECT COUNT(*) INTO n FROM USER_TRIGGERS WHERE TABLE_NAME='{name}';\n")
        sql.append(f"    IF n <> 0 THEN RAISE_APPLICATION_ERROR(-20234,'Unexpected trigger: {name}'); END IF;\n")
        semantics = {c["name"]: c for c in semantic[name]["columns"]}
        for c in table["columns"]:
            cs = semantics[c["name"]]
            parts = [c["position"], c["type"], "-", c["precision"] if c["precision"] is not None else "-", c["scale"] if c["scale"] is not None else "-", c["nullable"],
                     c["length"] if c["type"] in ("VARCHAR2", "CHAR", "NVARCHAR2", "NCHAR", "RAW") else "-",
                     c["char_used"] if c["type"] in ("VARCHAR2", "CHAR", "NVARCHAR2", "NCHAR") else "-",
                     c["char_length"] if c["type"] in ("VARCHAR2", "CHAR", "NVARCHAR2", "NCHAR") else "-",
                     "NO", cs["virtual_column"], cs["identity_column"], cs["default_on_null"], cs["user_generated"], cs["collation"] or "-"]
            shape = "|".join(str(v) for v in parts)
            expected = c["default"].strip() if c["default"] is not None else None
            sql.append(f"    check_column('{name}','{c['name']}',{sql_literal(shape)},{sql_literal(expected)});\n")
    sql.append("""  END;

  PROCEDURE align_default(p_table VARCHAR2, p_column VARCHAR2, p_expected VARCHAR2, p_ddl VARCHAR2) IS
  BEGIN
    -- Re-read immediately before DDL: never overwrite a changed existing default.
    IF default_missing(p_table,p_column,p_expected) THEN
      EXECUTE IMMEDIATE p_ddl;
      applied := applied + 1;
    ELSE
      skipped := skipped + 1;
    END IF;
    IF default_missing(p_table,p_column,p_expected) THEN
      RAISE_APPLICATION_ERROR(-20235,'Default postcheck failed: '||p_table||'.'||p_column);
    END IF;
  END;
BEGIN
  -- ALL table/column/default/NLS conflicts finish before the first ALTER TABLE.
  assert_preflight;
""")
    for item in defaults:
        table, column, expression = item["table"], item["column"], item["expression"]
        ddl = f'ALTER TABLE "{table}" MODIFY ("{column}" DEFAULT {expression})'
        sql.append(f"  align_default('{table}','{column}',{sql_literal(expression)},{sql_literal(ddl)});\n")
    sql.append("""  require_defaults := TRUE;
  assert_preflight;
  DBMS_OUTPUT.PUT_LINE('Defaults aligned: applied='||applied||', equivalent='||skipped||'; NOT NULL/index stages remain unresolved');
END;
/
""")
    return "".join(sql)


def verify_alignment(manifest_path):
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    if manifest.get("schema") != ALIGNMENT_SCHEMA or manifest.get("stage") != "defaults-only":
        fail("unsupported alignment stage")
    if manifest.get("target_owner") != "LIMS_RUNTIME_LOCAL" or manifest.get("target_container") != "FREEPDB1":
        fail("alignment target must be LIMS_RUNTIME_LOCAL/FREEPDB1")
    if manifest.get("business_rows_written") is not False or manifest.get("production_writes") is not False:
        fail("alignment must not claim business or production writes")
    sources = manifest.get("sources", [])
    if [s.get("path") for s in sources] != [SOURCE_DIRECTORY + p for p in ALIGNMENT_SOURCES]:
        fail("alignment formal source set differs")
    for source in sources:
        if digest(ROOT / source["path"]) != source.get("sha256"):
            fail("alignment source hash mismatch: " + source["path"])
    structure = json.loads((ROOT / (SOURCE_DIRECTORY + "structure.json")).read_text(encoding="utf-8"))
    expected = source_defaults(structure)
    if len(expected) != 33 or manifest.get("defaults") != expected:
        fail("alignment defaults differ from all 33 formal source expressions")
    if manifest.get("remaining") != ["13 redundant generated NOT NULL checks require separate reviewed alignment", "4 primary/unique backing index definitions and links require separate reviewed alignment"]:
        fail("unresolved alignment stages must remain explicit")
    sql_entry = manifest.get("sql", {})
    path = Path(str(sql_entry.get("path", "")))
    if path.is_absolute() or ".." in path.parts or path.parts[:2] != ("api-fixtures", "schema"):
        fail("alignment SQL must be a portable schema fixture")
    raw = (ROOT / path).read_bytes()
    if hashlib.sha256(raw).hexdigest() != sql_entry.get("sha256"):
        fail("alignment SQL hash mismatch")
    if raw.decode("utf-8") != render_alignment(structure):
        fail("alignment SQL differs from guarded formal source rendering")
    print(json.dumps({"status": "pass", "stage": "defaults-only", "defaults": len(expected),
                      "columns_checked": sum(len(t["columns"]) for t in structure["tables"]), "strict_evidence": False}))
    return 0


# A finite reviewed SQL pin protects guards as well as the two formal ALTERs.
INTEGER_SQL_SHA256 = "36c48b41efc6edc5279f81e9873413298fc743c85f6e2d50bd1f2b3cf0385ac7"


def verify_integer_alignment(manifest_path):
    plan = json.loads(manifest_path.read_text(encoding="utf-8"))
    if plan.get("schema") != "external-clearance-integer-alignment-v1":
        fail("unsupported integer alignment")
    if plan.get("target_owner") != "LIMS_RUNTIME_LOCAL" or plan.get("target_container") != "FREEPDB1":
        fail("integer alignment target must be LIMS_RUNTIME_LOCAL/FREEPDB1")
    expected_sources = [SOURCE_DIRECTORY + p for p in ("structure.json", "20-tables.sql")]
    if [entry.get("path") for entry in plan.get("sources", [])] != expected_sources:
        fail("integer alignment source closure differs")
    for entry in plan["sources"]:
        if digest(ROOT / entry["path"]) != entry.get("sha256"):
            fail("integer alignment source hash mismatch")
    structure = json.loads((ROOT / expected_sources[0]).read_text(encoding="utf-8"))
    table = next(t for t in structure["tables"] if t["name"] == "T_LIMS_CLEARANCEFORM")
    expected = [c for c in table["columns"] if c["name"] in ("CCID", "VERSION")]
    if plan.get("columns") != expected or plan.get("required_rows") != 0:
        fail("integer alignment differs from exact two source columns/empty table")
    if plan.get("business_rows_written") is not False or plan.get("production_writes") is not False:
        fail("integer alignment cannot claim business or production writes")
    lines = (ROOT / expected_sources[1]).read_text(encoding="utf-8").splitlines()
    if plan.get("source_lines") != [405, 406] or lines[404:406] != ['    "CCID" NUMBER(*,0),', '    "VERSION" NUMBER(*,0),']:
        fail("integer alignment source statement lines drifted")
    entry = plan["sql"]
    path = Path(entry["path"])
    if path.is_absolute() or ".." in path.parts or path.parts[:2] != ("api-fixtures", "schema"):
        fail("integer alignment SQL path must be a portable fixture")
    if entry.get("sha256") != INTEGER_SQL_SHA256 or digest(ROOT / path) != INTEGER_SQL_SHA256:
        fail("integer alignment SQL differs from reviewed guarded source transformation")
    print(json.dumps({"status": "pass", "stage": "empty-clearance-integer", "columns": 2, "required_rows": 0, "strict_evidence": False}))
    return 0

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--manifest", type=Path, default=MANIFEST)
    parser.add_argument("--alignment-manifest", type=Path, help="verify the finite local production-default migration")
    parser.add_argument("--integer-alignment-manifest", type=Path, help="verify the two empty clearance integer-column corrections")
    parser.add_argument("--notnull-alignment-manifest", type=Path, help="verify the finite local thirteen-NOT-NULL migration")
    args = parser.parse_args()
    if args.integer_alignment_manifest is not None:
        try:
            return verify_integer_alignment(args.integer_alignment_manifest.resolve())
        except (OSError, ValueError, KeyError, TypeError, StopIteration) as exc:
            fail("invalid integer alignment inputs: " + str(exc))
    if args.notnull_alignment_manifest is not None:
        from external_notnull_alignment import verify
        try:
            return verify(args.notnull_alignment_manifest.resolve(), ROOT)
        except (OSError, ValueError, KeyError, TypeError) as exc:
            fail("invalid NOT NULL alignment inputs: " + str(exc))
    if args.alignment_manifest is not None:
        try:
            return verify_alignment(args.alignment_manifest.resolve())
        except (OSError, ValueError, KeyError, TypeError) as exc:
            fail("invalid alignment inputs: " + str(exc))
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
