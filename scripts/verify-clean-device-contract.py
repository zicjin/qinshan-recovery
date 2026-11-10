#!/usr/bin/env python3
"""Fail-closed verifier for the clean-device deployment contract."""
from __future__ import annotations

import argparse
import importlib.util
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
    "deployment/oracle/core-schema/manifest.json",
    "deployment/oracle/external-schema/manifest.json",
    "scripts/verify-core-schema-package.py",
    "deployment/oracle/installation-manifest.json",
    "deployment/oracle/install.sql",
    "deployment/oracle/00-preflight.sql",
    "deployment/oracle/99-verify.sql",
    "scripts/verify-combined-oracle-installation.py",
    "owned-distribution/initialize-admin.sh",
    "docs/deployment/first-administrator.md",
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
    if (bootstrap.get("status") != "partial" or bootstrap.get("source_bound") is not True
            or bootstrap.get("complete_current_production_ddl") is not False):
        fail("core schema must describe the source-bound partial package, without claiming completeness")
    core = manifest.get("core_schema_package", {})
    if (core.get("path") != "deployment/oracle/core-schema/manifest.json"
            or core.get("status") != "partial"
            or core.get("requires_private_source_at_install") is not False
            or core.get("complete_application_bootstrap") is not False):
        fail("core package must remain partial and independently verifiable")
    combined = manifest.get("combined_oracle_installation", {})
    if (combined.get("path") != "deployment/oracle/installation-manifest.json"
            or combined.get("initializer") != "deployment/oracle/install.sql"
            or combined.get("status") != "partial"
            or combined.get("integrity_verifier") != "scripts/verify-combined-oracle-installation.py --check-plan"
            or combined.get("requires_private_source_at_install") is not False
            or combined.get("complete_external_domains") is not False
            or combined.get("deployment_ready") is not False):
        fail("combined Oracle installation must remain a portable partial plan")
    administrator = manifest.get("first_administrator", {})
    if (administrator.get("status") != "implemented"
            or administrator.get("launcher") != "owned-distribution/initialize-admin.sh"
            or administrator.get("instructions") != "docs/deployment/first-administrator.md"
            or administrator.get("requires_prepared_schema_and_technical_bootstrap") is not True
            or administrator.get("completes_application_bootstrap") is not False):
        fail("first administrator command requires a prepared schema and does not complete bootstrap")
    historical = manifest.get("historical_sql", {})
    if historical.get("replay_allowed") is not False:
        fail("historical project/sql replay must remain forbidden")
    for relative in REQUIRED_FIXTURES:
        if not (ROOT / relative).is_file():
            fail("required fixture/verifier is missing: " + relative)
    try:
        package_manifest = json.loads((ROOT / core["path"]).read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail("core package manifest is unreadable: " + str(exc))
    if (package_manifest.get("status") != core["status"]
            or package_manifest.get("source_class") != "current-production-at-capture"
            or package_manifest.get("deployment_ready") is not False
            or not package_manifest.get("blockers")):
        fail("deployment contract contradicts the partial production-derived core package")
    # Reuse the finite plan verifier so this contract cannot validate missing
    # child SQL, stale hashes, reordered phases or relaxed partial guards.
    try:
        spec = importlib.util.spec_from_file_location(
            "combined_oracle_installation", ROOT / "scripts/verify-combined-oracle-installation.py",
        )
        if not spec or not spec.loader:
            fail("combined Oracle installation verifier is unavailable")
        verifier = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(verifier)
        combined_result = verifier.verify(ROOT / combined["path"])
    except (OSError, UnicodeError, ValueError, TypeError, KeyError, AttributeError, SyntaxError):
        fail("combined Oracle installation integrity check failed")
    if (combined_result.get("plan_valid") is not True
            or combined_result.get("deployment_ready") is not False
            or combined_result.get("complete_external_domains") is not False):
        fail("combined Oracle installation contradicts the blocked deployment contract")
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
        "reason": "source-bound partial DDL and first administrator command exist; complete schema/configuration installation remains blocked",
        "core_schema_status": bootstrap["status"],
        "core_schema_source_bound": bootstrap["source_bound"],
        "first_administrator_status": administrator["status"],
        "historical_sql_replay_allowed": False,
        "external_logical_clone_fixture": True,
        "combined_oracle_installation_valid": combined_result["plan_valid"],
    }, ensure_ascii=False))
    return 0 if args.check_contract else 2


if __name__ == "__main__":
    raise SystemExit(main())
