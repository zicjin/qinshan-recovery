#!/usr/bin/env python3
"""Validate and execute isolated SQL fixture controls without exposing secrets."""
from __future__ import annotations

import hashlib
from pathlib import Path
import re
import subprocess
import sys
from typing import Any, Dict

FIXTURE_ROOT = Path(__file__).resolve().parents[1] / "api-fixtures"
FORBIDDEN = re.compile(r"(?i)(10\.100\.|218\.93\.|password\s*=|authorization\s*:|sunway-app|license-machine|mac_address)")


class FixtureError(ValueError):
    pass


def validate_sql_fixture(path: Path, root: Path = FIXTURE_ROOT) -> Path:
    path = path.resolve()
    root = root.resolve()
    try:
        path.relative_to(root)
    except ValueError as error:
        raise FixtureError("fixture escapes fixture root: " + str(path)) from error
    if not path.is_file() or path.is_symlink() or path.suffix.lower() != ".sql":
        raise FixtureError("fixture must be a regular .sql file: " + str(path))
    data = path.read_bytes()
    text = data.decode("utf-8", errors="strict")
    hit = FORBIDDEN.search(text)
    if hit:
        raise FixtureError("fixture contains forbidden marker: " + hit.group(0))
    return path


def fixture_digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def execute_fixture(executor: Path, fixture: Path, phase: str, *, root: Path = FIXTURE_ROOT) -> Dict[str, Any]:
    if phase not in {"setup", "restore"}:
        raise FixtureError("fixture phase must be setup or restore")
    fixture = validate_sql_fixture(fixture, root)
    executor = executor.resolve()
    if not executor.is_file() or executor.is_symlink():
        raise FixtureError("fixture executor is not a regular file: " + str(executor))
    if executor.suffix == ".py":
        command = [sys.executable, str(executor)]
    else:
        if not (executor.stat().st_mode & 0o111):
            raise FixtureError("fixture executor is not executable: " + str(executor))
        command = [str(executor)]
    result = subprocess.run(
        command + ["--fixture", str(fixture), "--phase", phase],
        capture_output=True, text=True, timeout=300,
        env=None,
    )
    if result.returncode:
        raise FixtureError("fixture executor failed for " + phase + " (exit=%d)" % result.returncode)
    output = (result.stdout + result.stderr).encode("utf-8", errors="replace")
    return {
        "phase": phase,
        "path": str(fixture),
        "sha256": fixture_digest(fixture),
        "status": "executed",
        "executor": str(executor),
        "output_sha256": hashlib.sha256(output).hexdigest(),
        "output_bytes": len(output),
    }
