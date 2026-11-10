#!/usr/bin/env python3
"""Fail-closed verifier for the clean-device deployment contract."""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "docs/deployment/clean-device-schema-manifest.json"
REQUIRED_FIXTURES = (
    "api-fixtures/schema/external-logical-clone.sql",
    "api-fixtures/schema/external-logical-clone-restore.sql",
    "scripts/verify-external-logical-clone-fixture.py",
    "scripts/verify-external-logical-clone-runtime.py",
)
SECRET_MATERIAL = re.compile(r"(?i)(jdbc:|oracle:thin|@//|BEGIN\s+(?:RSA|OPENSSH)|-----BEGIN)")


def fail(message: str) -> None:
    raise SystemExit("clean-device-contract: FAIL: " + message)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--check-contract", action="store_true",
        help="check the offline contract only; exit 0 does not mean deployment ready",
    )
    parser.add_argument("--distribution", type=Path)
    parser.add_argument("--runtime-config", type=Path)
    args = parser.parse_args()
    try:
        manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(str(exc))
    if manifest.get("schema") != "clean-device-schema-contract-v1":
        fail("unsupported schema")
    if manifest.get("status") != "blocked-incomplete-core-schema":
        fail("contract status must remain blocked until an approved core package exists")
    bootstrap = manifest.get("production_schema_bootstrap", {})
    if bootstrap.get("status") != "unavailable" or bootstrap.get("complete_current_production_ddl") is not False:
        fail("core schema availability must be explicit and false")
    historical = manifest.get("historical_sql", {})
    if historical.get("replay_allowed") is not False:
        fail("historical project/sql replay must remain forbidden")
    for relative in REQUIRED_FIXTURES:
        if not (ROOT / relative).is_file():
            fail("required fixture/verifier is missing: " + relative)
    text = json.dumps(manifest, ensure_ascii=False)
    if SECRET_MATERIAL.search(text):
        fail("manifest contains secret or connection material")
    if args.runtime_config:
        config = args.runtime_config.resolve()
        if not config.is_file():
            fail("runtime config is missing")
        config_text = config.read_text(encoding="utf-8")
        for key in ("spring.datasource.url", "spring.datasource.username", "spring.datasource.password"):
            if not re.search(r"(?m)^" + re.escape(key) + r"[=:]", config_text):
                fail("runtime config missing " + key)
    if args.distribution:
        distribution = args.distribution.resolve()
        manifest_path = distribution / "manifest.json"
        start = distribution / "start.sh"
        if not manifest_path.is_file() or not start.is_file():
            fail("distribution is missing manifest.json or start.sh")
        package = json.loads(manifest_path.read_text(encoding="utf-8"))
        if package.get("distribution_mode") != "source-only-reconstituted":
            fail("distribution is not source-only-reconstituted")
        app = distribution / "app/lims-owned-application.jar"
        if not app.is_file():
            fail("distribution application JAR is missing")
        with zipfile.ZipFile(app) as jar:
            if any("gikam-core-" in name or "gikam-third-" in name for name in jar.namelist()):
                fail("supplier/private runtime JAR content is present")
    print(json.dumps({
        "status": "blocked",
        "contract_valid": True,
        "contract": str(MANIFEST.relative_to(ROOT)),
        "deployment_ready": False,
        "reason": "complete current-production core schema package is unavailable",
        "historical_sql_replay_allowed": False,
        "external_logical_clone_fixture": True,
    }, ensure_ascii=False))
    return 0 if args.check_contract else 2


if __name__ == "__main__":
    raise SystemExit(main())
