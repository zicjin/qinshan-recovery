#!/usr/bin/env python3
"""Verify the portable Oracle core DDL package without opening private inputs.

The default command is a deployment gate: an intact partial package exits 2.
Use --check-package to check its provenance/integrity while it remains blocked.
This verifier does not connect to Oracle or execute SQL.
"""
from __future__ import annotations

import argparse
from collections import Counter
import csv
import hashlib
import io
import json
from pathlib import Path, PurePosixPath
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_MANIFEST = ROOT / "deployment/oracle/core-schema/manifest.json"
HASH = re.compile(r"[0-9a-f]{64}\Z")
ROLES = {"types", "tables", "indexes", "constraints", "stored-code", "sequences", "comments",
         "bootstrap", "bootstrap-verifier", "source-metadata", "preflight", "verifier", "initializer", "registry", "blockers"}
BOOTSTRAP_TABLES = {"T_CORE_I18N_CONFIG", "T_CORE_I18N", "T_CORE_I18N_MESSAGE", "T_CORE_TABLE",
                    "T_CORE_TABLE_HIERARCHY", "T_CORE_MENU", "T_CORE_SYSTEM_CONFIG",
                    "T_CORE_LOGIN_CONFIG", "T_CORE_PASSWORD_POLICY", "T_CORE_ADMIN_SELECT_CONFIG",
                    "T_CORE_CODE_CLASS", "T_CORE_CODE_CATEGORY", "T_CORE_CODE"}
DICTIONARY_COLUMNS = {
    "T_CORE_CODE_CLASS": {"ID", "CODECLASSNAME", "ORDERNO"},
    "T_CORE_CODE_CATEGORY": {"ID", "CODECATEGORYNAME", "CODECLASSID", "ORDERNO"},
    "T_CORE_CODE": {"ID", "CODE", "CODENAME", "CODECATEGORYID", "ORDERNO", "LASTSUSPENDEDFLAG", "I18NCODE"},
}
LCDP_COLUMNS = {
    "T_LCDP_RESOURCE": {"ID","RESOURCENAME","RESOURCECATEGORY","PARENTID","ORDERNO","CONTENT","EFFECTVERSION","PATH","DELETEFLAG","DATATYPE","CLASSCONTENT","CLASSNAME"},
    "T_LCDP_RESOURCE_HISTORY": {"ID","RESOURCEID","RESOURCENAME","RESOURCECATEGORY","CONTENT","VERSION","SUBMITFLAG","EFFECTFLAG","PATH","MODIFYVERSION","DELETEFLAG","CLASSCONTENT"},
    "T_LCDP_MODULE_PAGE_COMP": {"ID","MODULEPAGEHISTORYID","MODULEPAGEID","MODULEPAGEVERSION","COMPID","TYPE","PARENTID","PREVIOUSID","VISIBLE","EDITABLE","CONFIG","CONFIGEXTEND"},
    "T_LCDP_MODULE_PAGE_I18N": {"ID","MODULEPAGEID","MODULEPAGEHISTORYID","CODE","I18NCONFIGID","MESSAGE"},
    "T_LCDP_PAGE_I18N_CODE": {"ID","CODE","MODULEPAGEID","MODULEPAGEHISTORYID"},
}
BOOTSTRAP_TABLES.update(LCDP_COLUMNS)
MYBATIS_DTD = re.compile(
    r'<!DOCTYPE\s+mapper\s+PUBLIC\s+"-//mybatis\.org//DTD Mapper 3\.0//EN"\s+'
    r'"http://mybatis\.org/dtd/mybatis-3-mapper\.dtd"\s*>', re.I,
)
OBJECT_ROLES = {
    "TABLE": "tables", "INDEX": "indexes", "TYPE": "types", "TYPE BODY": "types",
    "FUNCTION": "stored-code", "PROCEDURE": "stored-code", "PACKAGE": "stored-code",
    "PACKAGE BODY": "stored-code", "TRIGGER": "stored-code", "VIEW": "stored-code",
    "MATERIALIZED VIEW": "stored-code", "SYNONYM": "stored-code", "SEQUENCE": "sequences",
}
TYPE_PATTERN = "|".join(re.escape(item) for item in sorted(OBJECT_ROLES, key=len, reverse=True))
IDENTIFIER = r'(?:"(?:[^"]|"")+"|[A-Za-z_][A-Za-z0-9_$#]*)'
CREATE = re.compile(
    r"\bCREATE\s+(?:OR\s+REPLACE\s+)?"
    r"(?:(?:EDITIONABLE|NONEDITIONABLE|FORCE|NOFORCE|UNIQUE|BITMAP)\s+)*"
    r"(?P<type>" + TYPE_PATTERN + r")\s+(?P<name>" + IDENTIFIER + r")"
    r"(?P<owner>\s*\.\s*" + IDENTIFIER + r")?", re.I,
)
MARKER = re.compile(r"^-- (?P<edge>BEGIN|END) OBJECT (?P<type>" + TYPE_PATTERN
                    + r") (?P<name>\S+)\s*$", re.M)
SENSITIVE = re.compile(
    r"(?:\b(?:https?|ftp|jdbc):|oracle:thin|@//|-----BEGIN\s|"
    r"\b(?:PASSWORD|TOKEN|SECRET)\s*[=:]\s*['\"][^'\"]+['\"])", re.I,
)
IPV4_OCTET = r"(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])"
IPV4 = re.compile(r"(?<![\w.])" + IPV4_OCTET + r"(?:\." + IPV4_OCTET + r"){3}(?![\w.])")
FORBIDDEN_SQL = re.compile(
    r"\b(?:CREATE\s+(?:PUBLIC\s+)?(?:DATABASE\s+LINK|USER)|"
    r"ALTER\s+(?:SYSTEM|USER)|IDENTIFIED\s+BY)\b", re.I,
)


class InvalidPackage(ValueError):
    """An integrity error with a safe, non-SQL diagnostic."""


def require(condition: bool, message: str) -> None:
    if not condition:
        raise InvalidPackage(message)


def digest(raw: bytes) -> str:
    return hashlib.sha256(raw).hexdigest()


def unsafe_material(text: str) -> bool:
    # The exact public MyBatis DTD is source syntax, not an environment endpoint.
    # A bare URL, changed host/path, or unrelated endpoint still fails closed.
    reviewed = MYBATIS_DTD.sub("", text)
    return "\ufffd" in reviewed or SENSITIVE.search(reviewed) is not None or IPV4.search(reviewed) is not None


def check_hash(value: object, label: str) -> None:
    require(isinstance(value, str) and HASH.fullmatch(value) is not None,
            label + " must be a SHA-256 digest")


def relative_path(value: object) -> str:
    require(isinstance(value, str) and bool(value) and "\\" not in value,
            "package path must be a nonempty portable relative path")
    path = PurePosixPath(value)
    require(not path.is_absolute() and path.as_posix() == value
            and all(part not in ("..", ".", ".scratch", "private") for part in path.parts),
            "package path escapes the public package")
    return value


def package_file(root: Path, value: object) -> Path:
    path = root / relative_path(value)
    require(all(not part.is_symlink() for part in (path, *path.parents) if part != root.parent),
            "package files may not be symbolic links")
    require(path.is_file(), "a listed package file is missing")
    require(path.resolve().is_relative_to(root.resolve()), "package path escapes its root")
    return path


def masked_sql(text: str) -> str:
    """Blank comments/string literals, preserving offsets and quoted identifiers.

    This is a lexer for integrity checks, not an Oracle SQL rewriter. In
    particular, a CREATE TABLE inside a COMMENT value is not a DDL statement.
    """
    out = list(text)
    pos = 0
    length = len(text)

    def blank(start: int, end: int) -> None:
        for index in range(start, end):
            if out[index] not in "\r\n":
                out[index] = " "

    while pos < length:
        start = pos
        if text.startswith("--", pos):
            end = text.find("\n", pos)
            pos = length if end < 0 else end
            blank(start, pos)
        elif text.startswith("/*", pos):
            end = text.find("*/", pos + 2)
            require(end >= 0, "unterminated SQL block comment")
            pos = end + 2
            blank(start, pos)
        elif text[pos:pos + 2].lower() == "q'" and pos + 2 < length:
            opening = text[pos + 2]
            closing = {"[": "]", "(": ")", "{": "}", "<": ">"}.get(opening, opening)
            end = text.find(closing + "'", pos + 3)
            require(end >= 0, "unterminated Oracle alternative string literal")
            pos = end + 2
            blank(start, pos)
        elif text[pos] == "'":
            pos += 1
            while pos < length:
                if text[pos] != "'":
                    pos += 1
                elif text[pos:pos + 2] == "''":
                    pos += 2
                else:
                    pos += 1
                    break
            else:
                raise InvalidPackage("unterminated SQL string literal")
            blank(start, pos)
        elif text[pos] == '"':
            pos += 1
            while pos < length:
                if text[pos] != '"':
                    pos += 1
                elif text[pos:pos + 2] == '""':
                    pos += 2
                else:
                    pos += 1
                    break
            else:
                raise InvalidPackage("unterminated SQL quoted identifier")
        else:
            pos += 1
    return "".join(out)


def identifier(value: str) -> str:
    return value[1:-1].replace('""', '"') if value.startswith('"') else value.upper()


def create_identities(text: str) -> list[tuple[str, str]]:
    masked = masked_sql(text)
    require(FORBIDDEN_SQL.search(masked) is None,
            "connection, credential, user or database-link DDL is forbidden")
    matches = list(CREATE.finditer(masked))
    # Every CREATE must be understood; a new SQL object category cannot silently
    # bypass the registry just because this verifier does not recognize it.
    require(len(matches) == len(re.findall(r"\bCREATE\b", masked, re.I)),
            "unrecognized or unsupported CREATE statement")
    result = []
    for match in matches:
        require(not match.group("owner"), "created objects must use the current session owner")
        result.append((re.sub(r"\s+", " ", match.group("type").upper()),
                       identifier(match.group("name"))))
    return result


def object_blocks(text: str) -> dict[tuple[str, str], str]:
    blocks: dict[tuple[str, str], str] = {}
    opened = None
    for match in MARKER.finditer(text):
        identity = (match.group("type"), match.group("name"))
        if match.group("edge") == "BEGIN":
            require(opened is None, "nested object blocks are forbidden")
            require(identity not in blocks, "duplicate object block")
            opened = (identity, match.end())
        else:
            require(opened is not None and opened[0] == identity,
                    "object block end does not match its beginning")
            body = text[opened[1]:match.start()].strip() + "\n"
            require(create_identities(body) == [identity],
                    "object block does not contain exactly its declared CREATE")
            blocks[identity] = body
            opened = None
    require(opened is None, "unterminated object block")
    return blocks


def check_bootstrap(text: str) -> None:
    """Only the reviewed technical configuration tables may receive seed rows."""
    sql = masked_sql(text)
    require(re.search(r"^\s*WHENEVER\s+SQLERROR\s+EXIT\s+FAILURE\s+ROLLBACK\s*$", sql, re.I | re.M) is not None,
            "bootstrap must stop and roll back on SQL errors")
    require(re.search(r"^\s*SET\s+DEFINE\s+OFF\s*$", sql, re.I | re.M) is not None,
            "bootstrap must disable SQLPlus substitution")
    sql = re.sub(r"^\s*(?:WHENEVER\s+(?:SQLERROR|OSERROR)\s+EXIT\s+FAILURE\s+ROLLBACK|"
                 r"SET\s+(?:DEFINE\s+OFF|SQLBLANKLINES\s+ON))\s*$", "", sql, flags=re.I | re.M)
    sql = sql.strip()
    if re.match(r"DECLARE\b", sql, re.I):
        guard = re.match(r"DECLARE\s+n\s+NUMBER\s*;\s*BEGIN\s*SELECT\s+"
                         r"(?P<counts>[\s\S]+?)\s+INTO\s+n\s+FROM\s+DUAL\s*;\s*"
                         r"IF\s+n\s*<>\s*0\s+THEN\s+RAISE_APPLICATION_ERROR\s*"
                         r"\(\s*-20120\s*,\s*\)\s*;\s*END\s+IF\s*;\s*END\s*;\s*/", sql, re.I)
        require(guard is not None, "bootstrap has an unapproved empty-target guard")
        term = r"\(\s*SELECT\s+COUNT\s*\(\s*\*\s*\)\s+FROM\s+(" + IDENTIFIER + r")\s*\)"
        counts = guard.group("counts").strip()
        require(re.fullmatch(term + r"(?:\s*\+\s*" + term + r")*", counts, re.I) is not None,
                "bootstrap guard may only sum whole-table counts")
        tables = [identifier(value) for value in re.findall(term, counts, re.I)]
        require(len(tables) == len(set(tables)) and set(tables).issubset(BOOTSTRAP_TABLES),
                "bootstrap guard counts unapproved or repeated tables")
        remainder = sql[guard.end():]
        targets = [identifier(value) for value in re.findall(r"\bINSERT\s+INTO\s+(" + IDENTIFIER + r")", remainder, re.I)]
        require(set(targets).issubset(set(tables)), "bootstrap empty-target guard omits a written table")
        sql = remainder
    else:
        raise InvalidPackage("bootstrap must reject nonempty target configuration before INSERT")
    statements = [statement.strip() for statement in sql.split(";") if statement.strip()]
    require(bool(statements) and statements[-1].upper() == "COMMIT"
            and all(statement.upper() != "COMMIT" for statement in statements[:-1]),
            "bootstrap must commit exactly once after all INSERT statements")
    for statement in statements[:-1]:
        statement = statement.strip()
        insert = re.fullmatch(r"INSERT\s+INTO\s+(" + IDENTIFIER + r")\s*"
                              r"\(\s*(?P<columns>(?:" + IDENTIFIER + r"\s*,\s*)*" + IDENTIFIER
                              + r")\s*\)\s+VALUES\s*\((?P<values>[\s\S]*)\)", statement, re.I)
        require(insert is not None, "bootstrap must contain only explicit INSERT VALUES and COMMIT")
        table = identifier(insert.group(1))
        require(table in BOOTSTRAP_TABLES,
                "bootstrap inserts into a table outside the technical configuration allowlist")
        projections = {**DICTIONARY_COLUMNS, **LCDP_COLUMNS}
        if table in projections:
            columns = [identifier(name.strip()) for name in insert.group("columns").split(",")]
            require(len(columns) == len(set(columns)) and set(columns) == projections[table],
                    "bootstrap columns differ from the reviewed technical projection")
        expression = insert.group("values")
        words = re.findall(r"[A-Za-z_$#][A-Za-z0-9_$#]*", expression)
        require(all(word.upper() in {"NULL", "TO_CLOB", "CHR", "TO_DATE", "TO_TIMESTAMP"}
                    for word in words), "bootstrap VALUES contains an unapproved SQL expression")
        require(re.fullmatch(r"[\w\s(),+.|-]*", expression) is not None,
                "bootstrap VALUES contains an unsupported expression")


def check_bootstrap_verifier(text: str) -> None:
    sql = masked_sql(text)
    require(re.search(r"\b(?:INSERT|UPDATE|DELETE|MERGE|CREATE|ALTER|DROP|TRUNCATE|GRANT|"
                      r"REVOKE|COMMIT|ROLLBACK|EXECUTE|CALL)\b", re.sub(
                          r"^\s*WHENEVER\s+(?:SQLERROR|OSERROR)\s+EXIT\s+FAILURE\s+ROLLBACK\s*$",
                          "", sql, flags=re.I | re.M), re.I) is None,
            "bootstrap verifier must not execute DDL, DML or transaction changes")
    require(re.search(r"^\s*@@?", sql, re.M) is None,
            "bootstrap verifier must not include child SQL")


def check_initializer(text: str, status: str, sql_files: set[str], preflight: str) -> None:
    masked = masked_sql(text)
    includes = list(re.finditer(r"^\s*@@?([^\s]+)\s*$", masked, re.M))
    paths = [relative_path(match.group(1)) for match in includes]
    require(len(paths) == len(set(paths)), "initializer repeats a child SQL file")
    require(set(paths) == sql_files, "initializer must include exactly the child SQL files")
    require(bool(paths) and paths[0] == preflight,
            "initializer must check preflight before any other child SQL")
    require(re.search(r"^\s*SET\s+DEFINE\s+OFF\s*$", masked, re.I | re.M) is not None,
            "initializer must disable SQLPlus substitution")
    if status != "partial":
        require(not re.search(r"RAISE_APPLICATION_ERROR\s*\(\s*-20100\b", masked, re.I),
                "ready package still contains the partial-package rejection")
        return
    guard = re.search(
        r"\bBEGIN\s+RAISE_APPLICATION_ERROR\s*\(\s*-20100\s*,\s*\)\s*;\s*END\s*;\s*/",
        masked, re.I,
    )
    require(guard is not None, "partial initializer must unconditionally reject with -20100")
    before = masked[:guard.start()]
    directives = before.splitlines()
    require(re.search(r"^\s*SET\s+DEFINE\s+OFF\s*$", before, re.I | re.M) is not None,
            "partial initializer must disable substitution before its rejection guard")
    require(any(re.fullmatch(r"\s*WHENEVER\s+SQLERROR\s+EXIT\s+FAILURE\s+ROLLBACK\s*",
                             line, re.I) for line in directives),
            "partial initializer must exit on SQL errors before its guard")
    allowed = re.compile(
        r"\s*(?:WHENEVER\s+(?:SQLERROR|OSERROR)\s+EXIT\s+FAILURE\s+ROLLBACK|"
        r"SET\s+(?:DEFINE\s+OFF|ECHO\s+OFF|VERIFY\s+OFF|FEEDBACK\s+ON|"
        r"SQLBLANKLINES\s+ON|SERVEROUTPUT\s+ON))?\s*", re.I,
    )
    require(all(allowed.fullmatch(line) is not None for line in directives),
            "partial initializer contains work before its rejection guard")
    require(all(match.start() > guard.end() for match in includes),
            "partial initializer includes a child before its rejection guard")
    require(not create_identities(masked[:guard.end()]),
            "partial initializer contains CREATE before its rejection guard")


def verify(manifest_path: Path) -> dict:
    require(not manifest_path.is_symlink(), "manifest may not be a symbolic link")
    root = manifest_path.parent.resolve()
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    require(isinstance(manifest, dict), "manifest must be an object")
    require(manifest.get("schema") == "core-schema-package-v1", "unsupported manifest schema")
    status = manifest.get("status")
    require(status in ("partial", "ready"), "unsupported package status")
    require(manifest.get("deployment_ready") is (status == "ready"),
            "manifest readiness contradicts package status")
    target = manifest.get("target", {})
    require(isinstance(target, dict) and target.get("owner") == "current-session-user"
            and target.get("requires_empty_schema") is True,
            "package must target the current user in an empty schema")
    require(manifest.get("source_class") == "current-production-at-capture",
            "core package must be bound to the captured production source")
    require(not unsafe_material(json.dumps(manifest, ensure_ascii=False)),
            "manifest contains corrupt Unicode, an endpoint or credential material")
    artifacts = manifest.get("source_artifacts")
    require(isinstance(artifacts, list) and bool(artifacts), "source provenance is missing")
    for artifact in artifacts:
        require(isinstance(artifact, dict) and isinstance(artifact.get("path"), str)
                and bool(artifact["path"]), "invalid provenance reference")
        reference = PurePosixPath(artifact["path"])
        require(not reference.is_absolute() and "\\" not in artifact["path"]
                and ".." not in reference.parts and reference.as_posix() == artifact["path"],
                "provenance reference must be a portable relative identifier")
        check_hash(artifact.get("sha256"), "source artifact hash")
        # These are provenance references, intentionally not installation inputs.
        # Never resolve or read a path in private or in the parent checkout.
    blockers = manifest.get("blockers")
    require(isinstance(blockers, list), "blockers must be a list")
    require(bool(blockers) == (status == "partial"), "package status contradicts its blockers")

    listed = manifest.get("files")
    require(isinstance(listed, list) and bool(listed), "package file list is missing")
    files: dict[str, dict] = {}
    texts: dict[str, str] = {}
    for entry in listed:
        require(isinstance(entry, dict), "invalid file entry")
        name = relative_path(entry.get("path"))
        require(name not in files, "duplicate listed package file")
        require(entry.get("role") in ROLES, "unknown package file role")
        path = package_file(root, name)
        raw = path.read_bytes()
        check_hash(entry.get("sha256"), "package file hash")
        require(digest(raw) == entry["sha256"], "package file hash mismatch: " + name)
        require(type(entry.get("bytes")) is int and len(raw) == entry["bytes"],
                "package file byte count mismatch: " + name)
        text = raw.decode("utf-8")
        require(not unsafe_material(text),
                "package file contains corrupt Unicode, an endpoint or credential: " + name)
        files[name] = entry
        texts[name] = text
    for path in root.rglob("*"):
        require(not path.is_symlink(), "package may not contain symbolic links")
        if path.is_file() and path.suffix.lower() in (".sql", ".tsv"):
            require(path.relative_to(root).as_posix() in files, "unlisted SQL or TSV package file")
    for role in ("initializer", "preflight", "registry", "verifier"):
        require(sum(entry["role"] == role for entry in files.values()) == 1,
                "package must contain exactly one " + role)
    require(sum(entry["role"] == "bootstrap" for entry in files.values()) <= 1,
            "package may contain at most one bootstrap phase")
    require(sum(entry["role"] == "bootstrap-verifier" for entry in files.values()) <= 1,
            "package may contain at most one bootstrap verification phase")
    for name, entry in files.items():
        if entry["role"] == "bootstrap":
            require(name.endswith(".sql"), "bootstrap must be SQL")
            check_bootstrap(texts[name])
        elif entry["role"] == "bootstrap-verifier":
            require(name.endswith(".sql"), "bootstrap verifier must be SQL")
            check_bootstrap_verifier(texts[name])
    if "technical_bootstrap" in manifest:
        reference = manifest["technical_bootstrap"]
        require(isinstance(reference, dict), "technical bootstrap reference must be an object")
        provenance_path = relative_path(reference.get("path"))
        require(provenance_path in files and files[provenance_path]["role"] == "source-metadata",
                "technical bootstrap provenance must be a listed source-metadata file")
        provenance = json.loads(texts[provenance_path])
        require(provenance.get("schema") == "core-technical-bootstrap-v1"
                and provenance.get("source_class") == manifest["source_class"]
                and provenance.get("business_rows") is False
                and provenance.get("identities_or_permissions_copied") is False,
                "technical bootstrap provenance has an invalid source or scope")
        bootstrap_files = [name for name, entry in files.items() if entry["role"] == "bootstrap"]
        require(len(bootstrap_files) == 1, "technical bootstrap provenance requires one bootstrap phase")
        sql = texts[bootstrap_files[0]]
        actual_rows = Counter()
        for match in re.finditer(r"\bINSERT\s+INTO\s+(" + IDENTIFIER + r")[^;]*;", masked_sql(sql), re.I):
            statement = sql[match.start():match.end()].strip() + "\n"
            actual_rows[(identifier(match.group(1)), digest(statement.encode("utf-8")))] += 1
        rows = provenance.get("rows")
        require(isinstance(rows, list), "technical bootstrap row provenance is missing")
        declared_rows, row_counts, seen = Counter(), Counter(), set()
        for row in rows:
            require(isinstance(row, dict) and row.get("table") in BOOTSTRAP_TABLES
                    and isinstance(row.get("id"), str) and bool(row["id"]),
                    "invalid technical bootstrap row provenance")
            identity = (row["table"], row["id"])
            require(identity not in seen, "duplicate technical bootstrap row identity")
            seen.add(identity)
            check_hash(row.get("source_row_sha256"), "bootstrap source row hash")
            check_hash(row.get("statement_sha256"), "bootstrap SQL statement hash")
            declared_rows[(row["table"], row["statement_sha256"])] += 1
            row_counts[row["table"]] += 1
        require(actual_rows == declared_rows, "bootstrap statement hashes do not match row provenance")
        require(dict(row_counts) == provenance.get("row_counts") == reference.get("row_counts"),
                "technical bootstrap row counts differ")
        for artifact in provenance.get("source_artifacts", []):
            require(isinstance(artifact, dict) and isinstance(artifact.get("path"), str),
                    "invalid bootstrap source artifact provenance")
            check_hash(artifact.get("sha256"), "bootstrap source artifact hash")

    objects = manifest.get("objects")
    require(isinstance(objects, list), "object list is missing")
    expected: dict[tuple[str, str], dict] = {}
    for entry in objects:
        require(isinstance(entry, dict), "invalid object entry")
        kind, name = entry.get("object_type"), entry.get("name")
        require(kind in OBJECT_ROLES and isinstance(name, str) and bool(name),
                "invalid object identity")
        identity = (kind, name)
        require(identity not in expected, "duplicate manifest object")
        path = relative_path(entry.get("path"))
        require(path in files and files[path]["role"] == OBJECT_ROLES[kind],
                "object file is missing or has the wrong role")
        check_hash(entry.get("source_sha256"), "object source hash")
        check_hash(entry.get("rendered_sha256"), "rendered object hash")
        require(isinstance(entry.get("transformations"), list)
                and all(isinstance(item, str) and item for item in entry["transformations"]),
                "object transformations must be an explicit list")
        expected[identity] = entry

    actual: dict[tuple[str, str], str] = {}
    for name, text in texts.items():
        if not name.endswith(".sql"):
            continue
        blocks = object_blocks(text)
        creates = create_identities(text)
        require(len(creates) == len(set(creates)), "duplicate CREATE in a package SQL file")
        require(set(creates) == set(blocks), "CREATE outside a registered object block")
        for identity, body in blocks.items():
            require(identity not in actual, "object is created in multiple package files")
            require(identity in expected and expected[identity]["path"] == name,
                    "object block is not registered in its manifest file")
            require(digest(body.encode("utf-8")) == expected[identity]["rendered_sha256"],
                    "rendered object hash mismatch")
            actual[identity] = name
    require(set(actual) == set(expected), "manifest object has no corresponding CREATE block")

    registry = manifest.get("registry")
    require(isinstance(registry, dict), "registry reference is missing")
    registry_path = relative_path(registry.get("path"))
    require(registry_path in files and files[registry_path]["role"] == "registry",
            "registry is not listed with the registry role")
    check_hash(registry.get("sha256"), "registry hash")
    require(registry["sha256"] == files[registry_path]["sha256"], "registry hash mismatch")
    reader = csv.DictReader(io.StringIO(texts[registry_path]), delimiter="\t")
    require(reader.fieldnames == ["object_type", "name", "path", "source_class"],
            "registry columns do not match the package contract")
    registered = {}
    for row in reader:
        require(None not in row and all(isinstance(value, str) and value for value in row.values()),
                "malformed registry row")
        identity = (row["object_type"], row["name"])
        require(identity not in registered, "duplicate registry object")
        require(row["source_class"] == manifest["source_class"], "registry source class mismatch")
        registered[identity] = relative_path(row["path"])
    require(registered == actual, "registry does not exactly cover the created objects")

    initializer = next(name for name, entry in files.items() if entry["role"] == "initializer")
    require(initializer.endswith(".sql"), "initializer must be SQL")
    preflight = next(name for name, entry in files.items() if entry["role"] == "preflight")
    require(preflight.endswith(".sql"), "preflight must be SQL")
    preflight_sql = masked_sql(texts[preflight])
    require(re.search(r"\b(?:CREATE|ALTER|INSERT|UPDATE|DELETE|MERGE|DROP|TRUNCATE|GRANT|REVOKE|"
                      r"EXECUTE\s+IMMEDIATE|COMMIT)\b", preflight_sql, re.I) is None,
            "preflight must not change schema, data or session settings")
    require(re.search(r"^\s*@@?", preflight_sql, re.M) is None,
            "preflight must not include child SQL")
    for metadata in ("USER_OBJECTS", "NLS_DATABASE_PARAMETERS"):
        require(re.search(r"\b" + metadata + r"\b", preflight_sql, re.I) is not None,
                "preflight is missing required target metadata check: " + metadata)
    contexts = set()
    for match in re.finditer(r"\bSYS_CONTEXT\s*\(\s*'USERENV'\s*,\s*"
                             r"'(SESSION_USER|CURRENT_SCHEMA)'\s*\)", texts[preflight], re.I):
        if preflight_sql[match.start():match.start() + len("SYS_CONTEXT")].upper() == "SYS_CONTEXT":
            contexts.add(match.group(1).upper())
    require(contexts == {"SESSION_USER", "CURRENT_SCHEMA"},
            "preflight must read the session user and current schema")
    sql_children = {name for name in files if name.endswith(".sql") and name != initializer}
    check_initializer(texts[initializer], status, sql_children, preflight)
    return {
        "status": "pass" if status == "ready" else "blocked",
        "package_valid": True, "schema_ready": status == "ready",
        "deployment_ready": status == "ready", "package_status": status,
        "source_class": manifest["source_class"], "files": len(files), "objects": len(objects),
        "blockers": blockers,
        "scope": "core-schema package only; full-system deployment has separate prerequisites",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--manifest", type=Path, default=DEFAULT_MANIFEST)
    parser.add_argument("--check-package", action="store_true",
                        help="check integrity only; an intact partial package remains deployment-blocked")
    args = parser.parse_args()
    try:
        result = verify(args.manifest)
    except (InvalidPackage, OSError, UnicodeError, ValueError, TypeError, KeyError) as exc:
        # OSError and decoder details may include private path/content. Return a
        # safe category; explicit InvalidPackage messages never contain raw SQL.
        reason = str(exc) if isinstance(exc, InvalidPackage) else type(exc).__name__
        print(json.dumps({"status": "failed", "package_valid": False,
                          "deployment_ready": False, "reason": reason}, ensure_ascii=False))
        return 1
    print(json.dumps(result, ensure_ascii=False))
    return 0 if args.check_package or result["deployment_ready"] else 2


if __name__ == "__main__":
    sys.exit(main())
