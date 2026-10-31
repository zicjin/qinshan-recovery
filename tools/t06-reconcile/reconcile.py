#!/usr/bin/env python3
"""Reconcile local T06 attachment metadata inventories.

This tool deliberately accepts *only* already-captured local inventory files.
It does not open a network socket, invoke ssh/docker, read application
configuration, or inspect a live filesystem tree.  Both inputs and all output
must be below this checkout's private/ directory.  The output is raw business
metadata and is therefore mode 0700/0600 material.

The database producer writes Java-escaped TSV files.  The filesystem producer
writes three NUL-delimited fields per record (bytes, mtime, absolute path).
Only T_CORE_FILE.expected_relative_path is treated as a safe DB-to-filesystem
reference.  T_EMC_EQUIPT_ACQ_FILE has no storage locator in the current
inventory contract and is reported as unresolved; this tool intentionally does
not guess an acquisition-file path.
"""

from __future__ import annotations

import argparse
import datetime as _dt
import math
import os
import posixpath
import re
import stat
import sys
from dataclasses import dataclass
from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Dict, Iterable, Iterator, List, Mapping, Optional, Sequence, Tuple


FORMAT = "lims-t06-reconciliation-v1"
FS_FORMAT = "lims-t06-filesystem-inventory-v1"
FS_RECORD_FORMAT = "bytes-nul-mtime_epoch-nul-absolute_path-nul"
JAVA_ESCAPES = {"\\": "\\", "t": "\t", "r": "\r", "n": "\n"}
SAFE_KEY = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*$")
INTEGER = re.compile(r"^[0-9]+$")
MTIME = re.compile(r"^[+-]?[0-9]+(?:\.[0-9]+)?$")
SNAPSHOT_ID = re.compile(r"^[A-Za-z0-9][A-Za-z0-9._:-]{2,127}$")


class InputError(Exception):
    """An invalid or unsafe local inventory input."""


@dataclass(frozen=True)
class DbRow:
    table: str
    row_number: int
    fields: Mapping[str, str]

    @property
    def row_id(self) -> str:
        return self.fields.get("ID", "")

    @property
    def target_id(self) -> str:
        return self.fields.get("TARGETID", "")

    @property
    def target_class(self) -> str:
        return self.fields.get("TARGET_CLASS", "")

    @property
    def expected(self) -> str:
        return self.fields.get("EXPECTED_RELATIVE_PATH", "")

    @property
    def db_size(self) -> Optional[int]:
        value = self.fields.get("SIZE", "")
        if value == "":
            return None
        try:
            number = Decimal(value)
        except InvalidOperation:
            return None
        if not number.is_finite() or number != number.to_integral_value() or number < 0:
            return None
        return int(number)


@dataclass(frozen=True)
class FsRecord:
    tree: str
    relative_path: str
    absolute_path: str
    bytes: int
    mtime: str


@dataclass(frozen=True)
class DbReference:
    row: DbRow
    tree: str
    relative_path: str


def _repo_root() -> Path:
    # tools/t06-reconcile/reconcile.py -> checkout root
    return Path(__file__).resolve().parents[2]


def _private_root() -> Path:
    return _repo_root() / "private"


def _mode_is_private(st_mode: int) -> bool:
    return (st_mode & 0o077) == 0


def _lexical_private_path(raw: str, *, allow_missing: bool) -> Path:
    """Resolve a user path lexically, reject symlinks, and keep it private."""

    if not raw:
        raise InputError("path is empty")
    candidate = Path(raw)
    if not candidate.is_absolute():
        candidate = _repo_root() / candidate
    # abspath normalizes '.' and '..' without following symlinks.
    candidate = Path(os.path.abspath(os.fspath(candidate)))
    private = Path(os.path.abspath(os.fspath(_private_root())))
    try:
        candidate.relative_to(private)
    except ValueError as exc:
        raise InputError("path must be below private/") from exc

    # A symlink anywhere in the local path would make the provenance boundary
    # ambiguous.  Check existing components before opening anything.
    current = Path(candidate.anchor)
    for component in candidate.parts[1:]:
        current = current / component
        try:
            st = os.lstat(current)
        except FileNotFoundError:
            if allow_missing:
                break
            raise InputError("path does not exist: %s" % raw)
        if stat.S_ISLNK(st.st_mode):
            raise InputError("symlink is not allowed: %s" % raw)

    if not allow_missing and not candidate.exists():
        raise InputError("path does not exist: %s" % raw)
    return candidate


def _assert_private_dir(path: Path, *, create: bool = False) -> None:
    if create:
        path.mkdir(mode=0o700, parents=True, exist_ok=True)
    try:
        st = os.lstat(path)
    except FileNotFoundError as exc:
        raise InputError("directory does not exist: %s" % path) from exc
    if not stat.S_ISDIR(st.st_mode):
        raise InputError("not a directory: %s" % path)
    if st.st_uid != os.getuid():
        raise InputError("directory owner does not match invoking user: %s" % path)
    if not _mode_is_private(st.st_mode):
        raise InputError("directory permissions are too broad: %s" % path)


def _assert_private_file(path: Path) -> None:
    try:
        st = os.lstat(path)
    except FileNotFoundError as exc:
        raise InputError("file does not exist: %s" % path) from exc
    if not stat.S_ISREG(st.st_mode):
        raise InputError("not a regular file: %s" % path)
    if st.st_uid != os.getuid():
        raise InputError("file owner does not match invoking user: %s" % path)
    if not _mode_is_private(st.st_mode):
        raise InputError("file permissions are too broad: %s" % path)


def _open_private_text(path: Path):
    _assert_private_file(path)
    flags = os.O_RDONLY | getattr(os, "O_NOFOLLOW", 0)
    fd = os.open(os.fspath(path), flags)
    return os.fdopen(fd, "r", encoding="utf-8", errors="strict", newline="")


def _read_env(path: Path) -> Dict[str, str]:
    values: Dict[str, str] = {}
    with _open_private_text(path) as handle:
        for line_number, raw in enumerate(handle, 1):
            line = raw.rstrip("\n")
            if line.endswith("\r"):
                line = line[:-1]
            if not line or line.lstrip().startswith("#"):
                continue
            if "=" not in line:
                raise InputError("malformed key/value line %s:%d" % (path, line_number))
            key, value = line.split("=", 1)
            if not SAFE_KEY.fullmatch(key) or key in values:
                raise InputError("duplicate or unsafe key %s:%d" % (path, line_number))
            values[key] = value
    return values


def _unescape_java(value: str, *, source: str) -> str:
    output: List[str] = []
    index = 0
    while index < len(value):
        char = value[index]
        if char != "\\":
            if char == "\x00":
                raise InputError("NUL in TSV field: %s" % source)
            output.append(char)
            index += 1
            continue
        index += 1
        if index >= len(value) or value[index] not in JAVA_ESCAPES:
            raise InputError("invalid Java TSV escape: %s" % source)
        output.append(JAVA_ESCAPES[value[index]])
        index += 1
    return "".join(output)


def _read_tsv(path: Path, table: str) -> List[DbRow]:
    rows: List[DbRow] = []
    with _open_private_text(path) as handle:
        try:
            first = next(handle)
        except StopIteration as exc:
            raise InputError("empty TSV: %s" % path) from exc
        first = first.rstrip("\n")
        if first.endswith("\r"):
            first = first[:-1]
        headers = [_unescape_java(value, source="%s:1" % path) for value in first.split("\t")]
        if headers and headers[0].startswith("\ufeff"):
            headers[0] = headers[0][1:]
        normalized = [header.upper() for header in headers]
        if len(set(normalized)) != len(normalized) or "ID" not in normalized:
            raise InputError("TSV headers are missing ID or are duplicated: %s" % path)
        for line_number, raw in enumerate(handle, 2):
            line = raw.rstrip("\n")
            if line.endswith("\r"):
                line = line[:-1]
            values = line.split("\t")
            if len(values) != len(headers):
                raise InputError("TSV field count mismatch %s:%d" % (path, line_number))
            decoded = [_unescape_java(value, source="%s:%d" % (path, line_number)) for value in values]
            fields = {normalized[i]: decoded[i] for i in range(len(headers))}
            rows.append(DbRow(table=table, row_number=line_number, fields=fields))
    return rows


def _validate_manifest(manifest: Mapping[str, str]) -> Tuple[str, str]:
    required = {
        "format": FS_FORMAT,
        "record_format": FS_RECORD_FORMAT,
        "production_read_only": "true",
        "find_options": "-P -xdev -type f",
        "content_copy": "false",
        "content_hash": "false",
    }
    for key, expected in required.items():
        if manifest.get(key) != expected:
            raise InputError("filesystem manifest %s must be %s" % (key, expected))
    if not manifest.get("completed_utc"):
        raise InputError("filesystem manifest has no completed_utc marker")
    if not SNAPSHOT_ID.fullmatch(manifest.get("storage_snapshot_id", "")):
        raise InputError("filesystem manifest has an invalid storage_snapshot_id")
    roots = []
    for key in ("files_root", "equipt_root"):
        value = manifest.get(key, "")
        if not value.startswith("/") or any(char in value for char in "\x00\t\r\n"):
            raise InputError("filesystem manifest has unsafe %s" % key)
        if posixpath.normpath(value) != value or any(part == ".." for part in value.split("/")):
            raise InputError("filesystem manifest has non-canonical %s" % key)
        roots.append(value.rstrip("/") or "/")
    return roots[0], roots[1]


def _iter_nul_triplets(path: Path) -> Iterator[Tuple[bytes, bytes, bytes]]:
    """Yield the producer's bytes/NUL/mtime/NUL/path/NUL records."""
    _assert_private_file(path)
    fields: List[bytes] = []
    pending = b""
    flags = os.O_RDONLY | getattr(os, "O_NOFOLLOW", 0)
    fd = os.open(os.fspath(path), flags)
    try:
        with os.fdopen(fd, "rb") as handle:
            while True:
                chunk = handle.read(1024 * 1024)
                if not chunk:
                    break
                pending += chunk
                pieces = pending.split(b"\0")
                pending = pieces.pop()
                for piece in pieces:
                    fields.append(piece)
                    if len(fields) == 3:
                        yield fields[0], fields[1], fields[2]
                        fields = []
            if pending or fields:
                raise InputError("truncated NUL record stream: %s" % path)
    except UnicodeError as exc:
        raise InputError("invalid filesystem metadata encoding: %s" % path) from exc


def _decode_fs_field(raw: bytes, *, field: str, source: Path) -> str:
    try:
        return raw.decode("utf-8", "surrogateescape")
    except UnicodeDecodeError as exc:
        raise InputError("invalid UTF-8 in %s field: %s" % (field, source)) from exc


def _read_fs_records(path: Path, tree: str, root: str) -> List[FsRecord]:
    records: List[FsRecord] = []
    for raw_bytes, raw_mtime, raw_path in _iter_nul_triplets(path):
        bytes_value = _decode_fs_field(raw_bytes, field="bytes", source=path)
        mtime = _decode_fs_field(raw_mtime, field="mtime", source=path)
        absolute = _decode_fs_field(raw_path, field="path", source=path)
        if not INTEGER.fullmatch(bytes_value):
            raise InputError("invalid byte count in %s" % path)
        if not MTIME.fullmatch(mtime) or not math.isfinite(float(mtime)):
            raise InputError("invalid mtime in %s" % path)
        if not absolute.startswith("/") or "\x00" in absolute:
            raise InputError("filesystem path is not absolute in %s" % path)
        canonical_root = root.rstrip("/") or "/"
        prefix = canonical_root if canonical_root == "/" else canonical_root + "/"
        if not absolute.startswith(prefix) or absolute == canonical_root:
            raise InputError("filesystem path is outside declared %s root: %s" % (tree, path))
        relative = absolute[len(prefix):]
        if not relative or posixpath.normpath(relative) != relative:
            raise InputError("filesystem path is not canonical in %s" % path)
        if any(part in ("", ".", "..") for part in relative.split("/")):
            raise InputError("filesystem path contains unsafe components in %s" % path)
        records.append(FsRecord(tree, relative, absolute, int(bytes_value), mtime))
    return records


def _expected_mapping(expected: str) -> Tuple[Optional[str], Optional[str]]:
    """Return (tree, relative path), or (None, reason)."""
    if not expected:
        return None, "missing-expected-relative-path"
    if "\x00" in expected or expected.startswith("/"):
        return None, "absolute-or-NUL-relative-path"
    if posixpath.normpath(expected) != expected:
        return None, "non-canonical-relative-path"
    if expected.startswith("files/"):
        tree = "files"
    elif expected.startswith("equipt-acq/"):
        # Accepted only as an explicit future producer contract.  The current
        # AttachmentInventory emits no such path for T_EMC rows.
        tree = "equipt-acq"
    else:
        return None, "unsupported-relative-path-prefix"
    relative = expected.split("/", 1)[1]
    if not relative or any(part in ("", ".", "..") for part in relative.split("/")):
        return None, "unsafe-relative-path-components"
    return tree, relative


def _tsv_escape(value: object) -> str:
    text = "" if value is None else str(value)
    return text.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n")


def _write_tsv(path: Path, headers: Sequence[str], rows: Iterable[Sequence[object]]) -> None:
    flags = os.O_WRONLY | os.O_CREAT | os.O_EXCL | getattr(os, "O_NOFOLLOW", 0)
    fd = os.open(os.fspath(path), flags, 0o600)
    try:
        with os.fdopen(fd, "w", encoding="utf-8", errors="surrogateescape", newline="\n") as handle:
            handle.write("\t".join(headers) + "\n")
            for row in rows:
                handle.write("\t".join(_tsv_escape(value) for value in row) + "\n")
    except Exception:
        try:
            os.unlink(path)
        except OSError:
            pass
        raise


def _write_env(path: Path, values: Mapping[str, object]) -> None:
    flags = os.O_WRONLY | os.O_CREAT | os.O_EXCL | getattr(os, "O_NOFOLLOW", 0)
    fd = os.open(os.fspath(path), flags, 0o600)
    try:
        with os.fdopen(fd, "w", encoding="utf-8", errors="surrogateescape", newline="\n") as handle:
            for key, value in values.items():
                handle.write("%s=%s\n" % (key, _tsv_escape(value)))
    except Exception:
        try:
            os.unlink(path)
        except OSError:
            pass
        raise


def _relative_private(path: Path) -> str:
    try:
        return os.fspath(path.relative_to(_repo_root()))
    except ValueError:
        return os.fspath(path)


def _parse_args(argv: Optional[Sequence[str]]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Reconcile local, restricted T06 DB and filesystem metadata inventories."
    )
    parser.add_argument("--db-inventory", required=True, help="private DB inventory directory")
    parser.add_argument("--filesystem-inventory", required=True, help="private filesystem inventory directory")
    parser.add_argument("--output", required=True, help="new private output directory")
    return parser.parse_args(argv)


def run(argv: Optional[Sequence[str]] = None) -> int:
    args = _parse_args(argv)
    _assert_private_dir(_private_root())
    db_dir = _lexical_private_path(args.db_inventory, allow_missing=False)
    fs_dir = _lexical_private_path(args.filesystem_inventory, allow_missing=False)
    output = _lexical_private_path(args.output, allow_missing=True)
    _assert_private_dir(db_dir)
    _assert_private_dir(fs_dir)
    if output.exists():
        raise InputError("output directory already exists: %s" % output)
    _assert_private_dir(output.parent)

    db_summary = _read_env(db_dir / "summary.txt")
    if db_summary.get("database_writes") != "false" or db_summary.get("content_lobs_selected") != "false":
        raise InputError("DB inventory summary is not a restricted metadata-only run")
    if not db_summary.get("inventory_completed_utc"):
        raise InputError("DB inventory summary has no inventory_completed_utc marker")
    db_consistency_mode = db_summary.get("consistency_mode", "")
    if db_consistency_mode not in ("flashback-scn", "best-effort"):
        raise InputError("DB inventory summary has an invalid consistency_mode")
    if db_consistency_mode == "flashback-scn" and not INTEGER.fullmatch(db_summary.get("flashback_scn", "")):
        raise InputError("flashback-scn DB inventory has no numeric flashback_scn")
    manifest = _read_env(fs_dir / "manifest.env")
    files_root, equipt_root = _validate_manifest(manifest)
    db_consistency = db_summary.get("consistency_reference", "")
    fs_snapshot = manifest.get("storage_snapshot_id", "")
    if not db_consistency or not fs_snapshot or db_consistency != fs_snapshot:
        raise InputError("DB consistency_reference and filesystem storage_snapshot_id must match")

    core_rows = _read_tsv(db_dir / "t_core_file.tsv", "T_CORE_FILE")
    acq_rows = _read_tsv(db_dir / "t_emc_equipt_acq_file.tsv", "T_EMC_EQUIPT_ACQ_FILE")
    fs_records = _read_fs_records(fs_dir / "files.nul", "files", files_root)
    fs_records += _read_fs_records(fs_dir / "equipt-acq.nul", "equipt-acq", equipt_root)

    refs: List[DbReference] = []
    excluded_refs: Dict[Tuple[str, str], str] = {}
    unresolved: List[List[object]] = []
    for row in core_rows:
        if not row.row_id:
            unresolved.append(["database", row.table, row.row_number, row.row_id, row.target_id, "", "", "", row.db_size, "", "missing-id", "T_CORE_FILE row has no ID"])
            continue
        if row.target_class != "known-table-prefix" or not row.target_id.startswith("T_"):
            reason = "missing-target-class" if not row.target_class else "excluded-target-class"
            detail = "T_CORE_FILE target class is not a live known-table reference"
            if row.target_class in ("soft-delete-marker", "unknown-or-legacy-prefix"):
                reason = row.target_class
                detail = "T_CORE_FILE target is explicitly non-live or legacy"
            if row.target_class == "known-table-prefix":
                reason = "target-class-inconsistent"
                detail = "known-table-prefix does not agree with TARGETID"
            tree_for_exclusion, relative_for_exclusion = _expected_mapping(row.expected)
            if tree_for_exclusion is not None:
                excluded_refs[(tree_for_exclusion, relative_for_exclusion)] = reason
            unresolved.append(["database", row.table, row.row_number, row.row_id, row.target_id, tree_for_exclusion or "", row.expected, "", row.db_size, "", reason, detail])
            continue
        tree, relative = _expected_mapping(row.expected)
        if tree is None:
            unresolved.append(["database", row.table, row.row_number, row.row_id, row.target_id, "", row.expected, "", row.db_size, "", relative, "T_CORE_FILE row cannot be mapped safely"])
            continue
        refs.append(DbReference(row, tree, relative))

    # The current producer intentionally leaves the acquisition table without
    # a storage locator.  Record every such row as unresolved rather than
    # joining on source/target names or guessing the intake-tree layout.
    for row in acq_rows:
        reason = "missing-expected-relative-path" if not row.expected else "unsupported-acquisition-mapping"
        unresolved.append(["database", row.table, row.row_number, row.row_id, row.target_id, "", row.expected, "", row.db_size, "", reason, "T_EMC_EQUIPT_ACQ_FILE has no safe storage locator in v1"])

    fs_by_key: Dict[Tuple[str, str], List[FsRecord]] = {}
    for record in fs_records:
        fs_by_key.setdefault((record.tree, record.relative_path), []).append(record)
    refs_by_key: Dict[Tuple[str, str], List[DbReference]] = {}
    for reference in refs:
        refs_by_key.setdefault((reference.tree, reference.relative_path), []).append(reference)

    duplicate_db_count = sum(1 for values in refs_by_key.values() if len(values) > 1)
    duplicate_fs_count = sum(1 for values in fs_by_key.values() if len(values) > 1)
    for key, values in refs_by_key.items():
        if len(values) > 1:
            for reference in values:
                row = reference.row
                unresolved.append(["database", row.table, row.row_number, row.row_id, row.target_id, reference.tree, reference.relative_path, "", row.db_size, "", "duplicate-db-reference", "multiple T_CORE_FILE rows reference one path"])
    for key, values in fs_by_key.items():
        if len(values) > 1:
            for record in values:
                unresolved.append(["filesystem", "", "", "", "", record.tree, record.relative_path, record.absolute_path, "", record.bytes, "duplicate-filesystem-path", "multiple filesystem records reference one path"])

    matches: List[List[object]] = []
    missing: List[List[object]] = []
    mismatches: List[List[object]] = []
    for reference in refs:
        row = reference.row
        key = (reference.tree, reference.relative_path)
        records = fs_by_key.get(key, [])
        if not records:
            missing.append([reference.tree, reference.relative_path, row.table, row.row_number, row.row_id, row.target_id, row.db_size, "filesystem-path-not-found"])
            continue
        if len(records) != 1:
            continue
        record = records[0]
        if row.db_size is None:
            size_status = "unavailable"
        elif row.db_size == record.bytes:
            size_status = "equal"
        else:
            size_status = "mismatch"
            mismatches.append([reference.tree, reference.relative_path, record.absolute_path, row.table, row.row_number, row.row_id, row.target_id, row.db_size, record.bytes, record.bytes - row.db_size])
        matches.append([reference.tree, reference.relative_path, record.absolute_path, row.table, row.row_number, row.row_id, row.target_id, row.db_size, record.bytes, size_status])

    possible_orphans: List[List[object]] = []
    for record in fs_records:
        key = (record.tree, record.relative_path)
        if key in refs_by_key:
            continue
        if key in excluded_refs:
            unresolved.append(["filesystem", "", "", "", "", record.tree, record.relative_path, record.absolute_path, "", record.bytes, "excluded-target-class", "filesystem path is referenced only by a non-live or legacy DB target"])
            continue
        if record.tree == "files":
            possible_orphans.append([record.tree, record.relative_path, record.absolute_path, record.bytes, record.mtime, "no-valid-T_CORE_FILE-reference"])
        else:
            unresolved.append(["filesystem", "", "", "", "", record.tree, record.relative_path, record.absolute_path, "", record.bytes, "no-storage-mapping-contract", "equipt-acq intake tree is not joined to T_EMC rows"])

    output.mkdir(mode=0o700)
    _assert_private_dir(output)
    output_files = {
        "matches.tsv": ([("tree", "relative_path", "absolute_path", "db_table", "db_row", "db_id", "target_id", "db_size", "fs_bytes", "size_status")], matches),
        "missing.tsv": ([("tree", "relative_path", "db_table", "db_row", "db_id", "target_id", "db_size", "reason")], missing),
        "possible-orphans.tsv": ([("tree", "relative_path", "absolute_path", "fs_bytes", "mtime_epoch", "reason")], possible_orphans),
        "size-mismatches.tsv": ([("tree", "relative_path", "absolute_path", "db_table", "db_row", "db_id", "target_id", "db_size", "fs_bytes", "delta_bytes")], mismatches),
        "unresolved.tsv": ([("kind", "db_table", "db_row", "db_id", "target_id", "tree", "relative_path", "absolute_path", "db_size", "fs_bytes", "reason", "detail")], unresolved),
    }
    for filename, (header_tuple, rows) in output_files.items():
        _write_tsv(output / filename, header_tuple[0], rows)

    findings = len(missing) + len(possible_orphans) + len(mismatches) + len(unresolved)
    summary = {
        "format": FORMAT,
        "generated_utc": _dt.datetime.now(_dt.timezone.utc).replace(microsecond=0).isoformat().replace("+00:00", "Z"),
        "db_inventory": _relative_private(db_dir),
        "filesystem_inventory": _relative_private(fs_dir),
        "files_root": files_root,
        "equipt_root": equipt_root,
        "consistency_reference": db_consistency,
        "consistency_mode": db_consistency_mode,
        "db_core_rows": len(core_rows),
        "db_equipt_acq_rows": len(acq_rows),
        "filesystem_files_records": sum(1 for record in fs_records if record.tree == "files"),
        "filesystem_equipt_acq_records": sum(1 for record in fs_records if record.tree == "equipt-acq"),
        "valid_db_references": len(refs),
        "matched_references": len(matches),
        "missing_db_references": len(missing),
        "possible_orphan_files": len(possible_orphans),
        "size_mismatches": len(mismatches),
        "unresolved_mappings": len(unresolved),
        "duplicate_db_paths": duplicate_db_count,
        "duplicate_filesystem_paths": duplicate_fs_count,
        "findings": findings,
        "raw_output_private": "true",
        "production_access": "false",
        "status": "FINDINGS" if findings else "CLEAN",
    }
    _write_env(output / "summary.env", summary)
    print("[t06-reconcile] PASS output=%s status=%s" % (output, summary["status"]))
    return 0


def main() -> int:
    try:
        return run()
    except (InputError, OSError) as exc:
        print("[t06-reconcile] ERROR: %s" % exc, file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
