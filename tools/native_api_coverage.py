#!/usr/bin/env python3
"""Validate exhaustive execution coverage for the external-native registry."""
from __future__ import annotations

import argparse
import hashlib
import json
import subprocess
from pathlib import Path
from typing import Any, Dict, Iterable, List

import native_api_registry

ROOT = Path(__file__).resolve().parents[2]
SCHEMA = 1
REQUIRED_SCOPES = {"external-native", "sunway-license"}
PASS_STATUSES = {"owned-pass", "removed-negative-pass"}
EXECUTIONS = {"http-contract", "java-contract", "resource-negative", "runtime-scan"}
REQUIRED_CLEANUP = (
    "application_stopped",
    "oracle_container_removed",
    "redis_container_removed",
    "network_removed",
    "private_temporary_directory_removed",
    "temporary_files_removed",
)
SOURCE_PATHS = (
    "lims-recovery/owned-framework/src",
    "lims-recovery/owned-personnel/src",
    "lims-recovery/owned-equipment/src",
    "lims-recovery/owned-consumables/src",
    "lims-recovery/owned-attachments/src",
    "lims-recovery/owned-report/src",
    "lims-recovery/owned-scheduler/src",
    "lims-recovery/owned-web/src",
    "lims-recovery/owned-distribution/start.sh",
    "lims-recovery/owned-distribution/package.py",
    "lims-recovery/owned-distribution/tests",
    "lims-recovery/scripts",
    "lims-recovery/tools",
    "lims-recovery/project",
)


def git_source_state(repo: Path | None = None) -> tuple[str, bool]:
    """Return HEAD and dirty state for implementation inputs only.

    Evidence files and generated targets are intentionally excluded. This lets
    a report be written after the source run without making its own evidence
    file appear as a source change.
    """
    repo = repo or ROOT
    commit = subprocess.check_output(
        ["git", "rev-parse", "HEAD"], cwd=repo, text=True
    ).strip()
    status = subprocess.run(
        ["git", "status", "--porcelain", "--untracked-files=all", "--", *SOURCE_PATHS],
        cwd=repo, text=True, capture_output=True, check=True,
    )
    return commit, bool(status.stdout.strip())


def registry_digest(registry: Dict[str, Any]) -> str:
    payload = json.dumps(registry, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(payload.encode("utf-8")).hexdigest()


def required_items(registry: Dict[str, Any]) -> List[Dict[str, Any]]:
    return [
        item for item in registry.get("items", [])
        if item.get("scope") in REQUIRED_SCOPES
        and not (
            item.get("scope") == "sunway-license"
            and item.get("kind") == "license-input"
            and item.get("classification") == "retain-for-PageOffice-until-authority-review"
        )
    ]


def new_report(
    registry: Dict[str, Any],
    *,
    source_commit: str,
    source_dirty: bool,
) -> Dict[str, Any]:
    items = required_items(registry)
    return {
        "schema": SCHEMA,
        "scope": "external-native-api-coverage",
        "status": "executed-fail",
        "production_access": False,
        "source_commit": source_commit,
        "source_dirty": source_dirty,
        "role": "registry-gate",
        "organization": "n/a",
        "mode": "isolated",
        "fixture": "registry-only",
        "audit": {"observed": False, "reason": "coverage gate has no business request"},
        "registry_schema": registry.get("schema"),
        "registry_sha256": registry_digest(registry),
        "required_item_count": len(items),
        "results": [],
        "cleanup": {key: True for key in REQUIRED_CLEANUP},
    }


def _item_map(registry: Dict[str, Any]) -> Dict[str, Dict[str, Any]]:
    return {item["id"]: item for item in required_items(registry)}


def validate_coverage(registry: Dict[str, Any], report: Dict[str, Any]) -> List[str]:
    errors: List[str] = []
    registry_errors = native_api_registry.validate_registry(registry)
    errors.extend("registry: " + error for error in registry_errors)
    if report.get("schema") != SCHEMA:
        errors.append("coverage schema must be 1")
    if report.get("scope") != "external-native-api-coverage":
        errors.append("coverage scope is not external-native-api-coverage")
    if report.get("production_access") is not False:
        errors.append("production_access must be false")
    if not isinstance(report.get("source_commit"), str) or not report["source_commit"]:
        errors.append("source_commit is required")
    if report.get("source_dirty") is not False:
        errors.append("source_dirty must be false")
    for key in ("role", "organization", "mode", "fixture"):
        if not isinstance(report.get(key), str) or not report[key]:
            errors.append(key + " is required")
    if not isinstance(report.get("audit"), dict):
        errors.append("audit is required")
    if report.get("registry_schema") != registry.get("schema"):
        errors.append("registry_schema does not match")
    if report.get("registry_sha256") != registry_digest(registry):
        errors.append("registry_sha256 does not match")

    expected = _item_map(registry)
    results = report.get("results")
    if not isinstance(results, list):
        return errors + ["results must be a list"]
    seen: Dict[str, Dict[str, Any]] = {}
    for result in results:
        if not isinstance(result, dict):
            errors.append("each result must be an object")
            continue
        registry_id = result.get("registry_id")
        if not isinstance(registry_id, str) or not registry_id:
            errors.append("result registry_id is required")
            continue
        if registry_id not in expected:
            errors.append("unregistered result: " + registry_id)
            continue
        if registry_id in seen:
            errors.append("duplicate result: " + registry_id)
            continue
        seen[registry_id] = result
        status = result.get("status")
        item = expected[registry_id]
        if status == "unknown":
            errors.append("unknown status: " + registry_id)
        elif status not in PASS_STATUSES:
            errors.append("unexecuted status: %s=%s" % (registry_id, status))
        elif item.get("scope") == "sunway-license" and status != "removed-negative-pass":
            errors.append("license item must be removed-negative-pass: " + registry_id)
        if result.get("execution") not in EXECUTIONS:
            errors.append("static-only or missing execution seam: " + registry_id)
        if not isinstance(result.get("evidence"), str) or not result["evidence"]:
            errors.append("evidence pointer missing: " + registry_id)
        if not isinstance(result.get("observed"), dict) or not result["observed"]:
            errors.append("independent observation missing: " + registry_id)
        # A generic source-reference count or package-wide residual scan is not
        # an executable contract for an external-native API.  Every required
        # item must be tied to an explicit family result (or an explicit
        # item-level deletion contract).  The previous handoff allowed
        # auto_classified results and consequently turned 128 unresolved native
        # exports into a green aggregate without exercising each item.
        if result.get("auto_classified"):
            errors.append("auto-classified result is not an explicit item contract: " + registry_id)

    for registry_id in expected:
        if registry_id not in seen:
            errors.append("missing result: " + registry_id)

    cleanup = report.get("cleanup")
    if not isinstance(cleanup, dict):
        errors.append("cleanup must be an object")
    else:
        for key in REQUIRED_CLEANUP:
            if cleanup.get(key) is not True:
                errors.append("cleanup must be true: " + key)
    if report.get("status") == "executed-pass" and errors:
        errors.append("executed-pass report contains coverage errors")
    return errors


def initial_report(registry: Dict[str, Any], source_commit: str) -> Dict[str, Any]:
    current_commit, source_dirty = git_source_state()
    if source_commit and source_commit != "unknown" and source_commit != current_commit:
        source_dirty = True
    return new_report(registry, source_commit=current_commit, source_dirty=source_dirty)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--registry", type=Path, required=True)
    parser.add_argument("--coverage", type=Path)
    parser.add_argument("--output", type=Path)
    parser.add_argument("--source-commit", default="unknown")
    args = parser.parse_args()
    registry = json.loads(args.registry.read_text(encoding="utf-8"))
    report = (
        json.loads(args.coverage.read_text(encoding="utf-8"))
        if args.coverage else initial_report(registry, args.source_commit)
    )
    errors = validate_coverage(registry, report)
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps({
            "status": "executed-pass" if not errors else "executed-fail",
            "errors": errors,
            "report": report,
        }, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    if errors:
        print("native API coverage: FAIL (%d errors)" % len(errors))
        for error in errors[:20]:
            print(error)
        return 1
    print("native API coverage: PASS (%d items)" % len(required_items(registry)))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
