#!/usr/bin/env python3
"""Verify a clean, extracted LIMS source tree without building or packaging it.

The verifier deliberately has no Docker, network, Git, or production integration.
It checks only the candidate tree and an optional, separately protected tree
attestation.  ``--emit-attestation`` prints a deterministic receipt to stdout;
it never writes into the candidate tree or creates an archive.
"""

from __future__ import annotations

import argparse
import hashlib
import os
import re
import stat
import sys
from pathlib import Path
from typing import Dict, Iterable, List, NoReturn, Optional, Sequence, Tuple


FORMAT = "lims-source-tree-v1"
SHA256_RE = re.compile(r"^sha256:[0-9a-f]{64}$")

# These are runtime, build, or repository-control inputs.  A clean source
# handoff must receive them through the separately controlled material channel.
FORBIDDEN_COMPONENTS = frozenset(
    {".git", "private", "materials", "build", "target", "logs", ".m2"}
)
FORBIDDEN_SUFFIXES = (".env", ".lic", ".pem", ".key")
REQUIRED_FILES = (
    "README.md",
    "GLOSSARY.md",
    "docs/recovery-handoff.md",
    "project/pom.xml",
    "scripts/build-prep.sh",
    "scripts/build.sh",
    "scripts/verify-materials.sh",
    "tools/class_diff.py",
    "tools/source_inventory.py",
)
REQUIRED_DIRECTORIES = ("project/src",)


class VerificationError(Exception):
    """A user-actionable candidate or receipt failure."""


def fail(message: str) -> NoReturn:
    raise VerificationError(message)


def canonical_root(raw: str) -> Path:
    path = Path(raw)
    if not path.is_absolute():
        path = Path.cwd() / path
    if path.is_symlink():
        fail("candidate root must not be a symlink")
    try:
        path = path.resolve(strict=True)
    except OSError as exc:
        fail(f"cannot resolve candidate root: {exc}")
    if not path.is_dir():
        fail(f"candidate root is not a directory: {path}")
    if stat.S_IMODE(path.stat().st_mode) & (stat.S_IWGRP | stat.S_IWOTH):
        fail("candidate root is group/other writable")
    return path


def check_required_layout(root: Path) -> None:
    for relative in REQUIRED_FILES:
        path = root / relative
        if not path.is_file() or path.is_symlink():
            fail(f"required source file is missing or not regular: {relative}")
    for relative in REQUIRED_DIRECTORIES:
        path = root / relative
        if not path.is_dir() or path.is_symlink():
            fail(f"required source directory is missing or not regular: {relative}")


def check_component_policy(relative: Path) -> None:
    parts = relative.parts
    if any(part in FORBIDDEN_COMPONENTS for part in parts):
        fail(f"forbidden handoff path component: {relative.as_posix()}")
    name = parts[-1]
    lowered = name.lower()
    if lowered == "application.properties":
        fail(f"runtime configuration must not be handed off: {relative.as_posix()}")
    if lowered.startswith("application-") and lowered.endswith(".properties"):
        fail(f"runtime configuration must not be handed off: {relative.as_posix()}")
    if lowered.startswith("bootstrap") and lowered.endswith(".properties"):
        fail(f"runtime configuration must not be handed off: {relative.as_posix()}")
    if lowered.endswith(FORBIDDEN_SUFFIXES):
        fail(f"restricted runtime material must not be handed off: {relative.as_posix()}")
    # Control characters make the canonical line format ambiguous and are not
    # useful in a portable source tree.
    if any(ord(char) < 0x20 or ord(char) == 0x7F for char in relative.as_posix()):
        fail(f"source path contains a control character: {relative.as_posix()!r}")


def iter_regular_files(root: Path) -> Iterable[Tuple[Path, os.stat_result]]:
    """Yield regular files in byte-sort order, rejecting links/special files."""
    paths: List[Path] = []
    for directory, dirnames, filenames in os.walk(root, topdown=True, followlinks=False):
        directory_path = Path(directory)
        # os.walk puts symlinked directories in dirnames.  Inspect and reject
        # them explicitly rather than silently skipping an external dependency.
        for dirname in list(dirnames):
            candidate = directory_path / dirname
            relative = candidate.relative_to(root)
            try:
                metadata = candidate.lstat()
            except OSError as exc:
                fail(f"cannot inspect {relative}: {exc}")
            if stat.S_ISLNK(metadata.st_mode):
                fail(f"symbolic links are not allowed in a source handoff: {relative}")
            if not stat.S_ISDIR(metadata.st_mode):
                fail(f"non-directory walk entry is not allowed: {relative}")
            if metadata.st_mode & (stat.S_IWGRP | stat.S_IWOTH):
                fail(f"source directory is group/other writable: {relative}")
            check_component_policy(relative)
        dirnames[:] = sorted(dirnames)
        for filename in filenames:
            candidate = directory_path / filename
            relative = candidate.relative_to(root)
            check_component_policy(relative)
            try:
                metadata = candidate.lstat()
            except OSError as exc:
                fail(f"cannot inspect {relative}: {exc}")
            if stat.S_ISLNK(metadata.st_mode):
                fail(f"symbolic links are not allowed in a source handoff: {relative}")
            if not stat.S_ISREG(metadata.st_mode):
                fail(f"special files are not allowed in a source handoff: {relative}")
            if metadata.st_mode & (stat.S_IWGRP | stat.S_IWOTH):
                fail(f"source file is group/other writable: {relative}")
            paths.append(candidate)

    def sort_key(path: Path) -> bytes:
        return path.relative_to(root).as_posix().encode("utf-8", "surrogateescape")

    for path in sorted(paths, key=sort_key):
        try:
            yield path, path.lstat()
        except OSError as exc:
            fail(f"cannot inspect {path.relative_to(root)}: {exc}")


def file_digest(path: Path) -> str:
    digest = hashlib.sha256()
    try:
        with path.open("rb") as stream:
            for chunk in iter(lambda: stream.read(1024 * 1024), b""):
                digest.update(chunk)
    except OSError as exc:
        fail(f"cannot read {path}: {exc}")
    return digest.hexdigest()


def tree_digest(root: Path) -> Tuple[str, int]:
    """Return a digest independent of mtimes, owners, and absolute path.

    Each record is ``F<TAB>relative-path<TAB>executable-bit<TAB>content-sha``
    followed by LF.  Records are byte-sorted by relative POSIX path.  The
    algorithm intentionally excludes directory mtimes and archive metadata.
    """
    canonical = hashlib.sha256()
    count = 0
    for path, metadata in iter_regular_files(root):
        relative = path.relative_to(root).as_posix()
        executable = "1" if metadata.st_mode & (stat.S_IXUSR | stat.S_IXGRP | stat.S_IXOTH) else "0"
        record = f"F\t{relative}\t{executable}\t{file_digest(path)}\n"
        canonical.update(record.encode("utf-8", "surrogateescape"))
        count += 1
    return canonical.hexdigest(), count


def parse_attestation(path: Path, root: Path) -> Dict[str, str]:
    if path.is_symlink() or not path.is_file():
        fail("attestation must be a regular, non-symlink file")
    try:
        resolved = path.resolve(strict=True)
        if os.path.commonpath((str(root), str(resolved))) == str(root):
            fail("attestation must be outside the candidate source tree")
    except ValueError:
        pass
    except OSError as exc:
        fail(f"cannot resolve attestation: {exc}")
    try:
        mode = stat.S_IMODE(path.stat().st_mode)
        if mode != 0o600:
            fail("attestation must have mode 0600")
        if path.stat().st_uid != os.getuid():
            fail("attestation must be owned by the invoking user")
    except OSError as exc:
        fail(f"cannot inspect attestation: {exc}")

    values: Dict[str, str] = {}
    allowed = {"FORMAT", "ROOT_TREE_SHA256", "PROJECT_TREE_SHA256"}
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except (OSError, UnicodeError) as exc:
        fail(f"cannot read attestation: {exc}")
    for line_number, line in enumerate(lines, 1):
        stripped = line.strip()
        if not stripped or stripped.startswith("#"):
            continue
        if "=" not in stripped:
            fail(f"attestation line {line_number} is not KEY=VALUE")
        key, value = stripped.split("=", 1)
        if key not in allowed or key in values or not value:
            fail(f"attestation line {line_number} has an unknown or duplicate key")
        values[key] = value
    if set(values) != allowed:
        fail("attestation must contain exactly FORMAT, ROOT_TREE_SHA256, and PROJECT_TREE_SHA256")
    if values["FORMAT"] != FORMAT:
        fail(f"unsupported attestation format: {values['FORMAT']}")
    for key in ("ROOT_TREE_SHA256", "PROJECT_TREE_SHA256"):
        if not SHA256_RE.fullmatch(values[key]):
            fail(f"attestation value is not a lowercase SHA-256 identity: {key}")
    return values


def verify(root: Path, attestation: Optional[Path], emit: bool) -> int:
    check_required_layout(root)
    root_digest, root_count = tree_digest(root)
    project = root / "project"
    project_digest, project_count = tree_digest(project)
    root_identity = f"sha256:{root_digest}"
    project_identity = f"sha256:{project_digest}"

    if attestation is not None:
        values = parse_attestation(attestation, root)
        if values["ROOT_TREE_SHA256"] != root_identity:
            fail("root tree digest does not match the attestation")
        if values["PROJECT_TREE_SHA256"] != project_identity:
            fail("project tree digest does not match the attestation")

    if emit:
        print(f"FORMAT={FORMAT}")
        print(f"ROOT_TREE_SHA256={root_identity}")
        print(f"PROJECT_TREE_SHA256={project_identity}")
    else:
        print(
            f"source handoff readiness PASS: root files={root_count}, "
            f"project files={project_count}, format={FORMAT}"
        )
    return 0


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Read-only clean source-tree readiness and attestation verifier"
    )
    parser.add_argument("--root", required=True, help="extracted source-tree directory")
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--attestation", help="mode-0600 restricted tree attestation")
    group.add_argument(
        "--emit-attestation",
        action="store_true",
        help="print a deterministic attestation; never writes a package or file",
    )
    return parser


def main(argv: Sequence[str]) -> int:
    args = build_parser().parse_args(argv)
    try:
        root = canonical_root(args.root)
        attestation = None if args.emit_attestation else canonical_root_file(args.attestation)
        return verify(root, attestation, args.emit_attestation)
    except VerificationError as exc:
        print(f"source handoff readiness FAIL: {exc}", file=sys.stderr)
        return 1


def canonical_root_file(raw: str) -> Path:
    path = Path(raw)
    if not path.is_absolute():
        path = Path.cwd() / path
    if path.is_symlink():
        fail("attestation path must not be a symlink")
    try:
        return path.resolve(strict=True)
    except OSError as exc:
        fail(f"cannot resolve attestation: {exc}")


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
