#!/usr/bin/env python3
"""Validate the redacted, prepared sss2 rollback replay contract."""
from __future__ import annotations

import json
import re
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[2]
BUNDLE = ROOT / "evidence" / "sss2-rollback-bundle-20261025.json"
HEX40 = re.compile(r"^[0-9a-f]{40}$")
HEX64 = re.compile(r"^[0-9a-f]{64}$")
FORBIDDEN = re.compile(
    r"(?i)(password|passwd|token|secret|authorization:|bearer\s|10\.100\.|218\.93\.|192\.168\.|production\s+(host|endpoint)|jdbc:oracle:thin:@)"
)


def fail(message: str) -> None:
    raise SystemExit(f"sss2 rollback bundle: FAIL: {message}")


def git_ref_exists(ref: str, cwd: Path) -> bool:
    return subprocess.run(
        ["git", "cat-file", "-e", f"{ref}^{{commit}}"], cwd=cwd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL
    ).returncode == 0


def main() -> int:
    if not BUNDLE.is_file():
        fail(f"missing {BUNDLE}")
    try:
        data = json.loads(BUNDLE.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"invalid JSON: {exc}")

    required = {
        "schema", "bundle_id", "status", "sss2_write_status", "execution", "production_access",
        "source_commit", "source_dirty", "nested_project_commit", "images", "fixture", "change_record",
        "baseline", "commands", "expected_evidence", "limitations", "cleanup",
    }
    missing = sorted(required - data.keys())
    if missing:
        fail(f"missing keys: {', '.join(missing)}")
    if data["schema"] != 1 or data["status"] != "prepared" or data["sss2_write_status"] != "unknown":
        fail("bundle must remain prepared with sss2_write_status=unknown")
    if data["execution"] != "not-executed" or data["production_access"] is not False:
        fail("prepared bundle has unsafe execution or production flags")
    if data["source_dirty"] is not False or not HEX40.fullmatch(data["source_commit"]):
        fail("source binding must be a clean 40-character commit")
    if not HEX40.fullmatch(data["nested_project_commit"]):
        fail("nested project binding must be a 40-character commit")
    if not git_ref_exists(data["source_commit"], ROOT.parent):
        fail("source commit is not present in this checkout")
    nested = ROOT / "project"
    if nested.is_dir() and not git_ref_exists(data["nested_project_commit"], nested):
        fail("nested project commit is not present in the project checkout")
    if data["fixture"].get("role") != "manager" or data["fixture"].get("organization") != "org-a" or data["fixture"].get("mode") != "isolated":
        fail("fixture must use the declared isolated manager/org-a boundary")
    if data["fixture"].get("write_gate") != "owned.sss2.write.mode=isolated":
        fail("isolated write gate is missing")
    fields = set(data["change_record"].get("required_fields", []))
    required_fields = {"change_id", "fixture_id", "pre_write_projection", "write_observation", "rollback_observation", "cleanup_observation"}
    if not required_fields <= fields:
        fail("change record omits required rollback fields")
    commands = data["commands"]
    for name in ("prepare", "start", "baseline", "write", "observe", "rollback", "verify", "cleanup"):
        if not isinstance(commands.get(name), str) or not commands[name].strip():
            fail(f"missing command: {name}")
    serialized = json.dumps(data, ensure_ascii=True)
    if FORBIDDEN.search(serialized):
        fail("credential-like or production endpoint material found")
    if not data["limitations"] or data["cleanup"].get("executed") is not False:
        fail("limitations and unexecuted cleanup boundary must be explicit")
    print("sss2 rollback bundle: PASS (prepared, redacted, unexecuted boundary)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
