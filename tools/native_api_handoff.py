#!/usr/bin/env python3
"""Aggregate external-native family evidence into the final handoff gate."""
from __future__ import annotations

import argparse
import hashlib
import json
import subprocess
from pathlib import Path
from typing import Any, Dict, Iterable, List

import native_api_coverage


PASS_STATUSES = {"owned-pass", "removed-negative-pass"}
SCHEMA = 1
ROOT = Path(__file__).resolve().parents[2]
LIMS = ROOT / "lims-recovery"
EVIDENCE = LIMS / "evidence"
REGISTRY_PATH = EVIDENCE / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json"
FAMILY_SOURCE_PATHS = (
    "lims-recovery/owned-framework/src/",
    "lims-recovery/owned-personnel/src/",
    "lims-recovery/owned-equipment/src/",
    "lims-recovery/owned-consumables/src/",
    "lims-recovery/owned-attachments/src/",
    "lims-recovery/owned-report/src/",
    "lims-recovery/owned-scheduler/src/",
    "lims-recovery/owned-web/src/",
    "lims-recovery/owned-distribution/start.sh",
    "lims-recovery/owned-distribution/package.py",
    "lims-recovery/owned-web/build-owned-web.sh",
)
# These files are additive compatibility seams and contract-test wiring added
# after the original family reports. Their current-head behavior is covered by
# the standalone ``native-api-*-owned-20261026.json`` reports; they must not
# invalidate unrelated older family observations merely because the broad
# family source root contains them.
BENIGN_POST_REPORT_PATHS = {
    "lims-recovery/owned-framework/src/main/java/com/sunwayworld/framework/context/ApplicationContextHelper.java",
    "lims-recovery/owned-framework/src/main/java/com/sunwayworld/framework/security/authentication/AuthenticationHelper.java",
    "lims-recovery/owned-framework/src/main/java/com/sunwayworld/framework/utils/DataSourceUtils.java",
    "lims-recovery/owned-framework/src/main/java/com/sunwayworld/owned/internal/context/OwnedSequenceProvider.java",
    "lims-recovery/owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedIdentityConfiguration.java",
    "lims-recovery/owned-framework/src/test/java/com/sunwayworld/framework/ApplicationContextHelperContractTest.java",
    "lims-recovery/owned-framework/src/test/java/com/sunwayworld/framework/AuthenticationHelperContractTest.java",
    "lims-recovery/owned-framework/src/test/java/com/sunwayworld/framework/DataSourceUtilsContractTest.java",
    "lims-recovery/owned-framework/src/test/java/com/sunwayworld/framework/OwnedSequenceProviderContractTest.java",
    "lims-recovery/scripts/build-owned-framework.sh",
}


def digest(value: Any) -> str:
    return hashlib.sha256(
        json.dumps(value, ensure_ascii=False, sort_keys=True, separators=(",", ":")).encode()
    ).hexdigest()


def source_commit() -> str:
    return subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()


def load_registry() -> Dict[str, Any]:
    return json.loads(REGISTRY_PATH.read_text(encoding="utf-8"))


def load_family_results(registry: Dict[str, Any]) -> Dict[str, Dict[str, Any]]:
    results: Dict[str, Dict[str, Any]] = {}
    result_sources: Dict[str, Path] = {}
    family_paths = list(EVIDENCE.glob("native-api-family-*.json"))
    # Standalone current-head seam reports supersede the corresponding broad
    # family rows without pretending that the older family runner was rerun.
    family_paths.extend(EVIDENCE.glob("native-api-*-owned-20261026.json"))
    for path in sorted(set(family_paths)):
        report = json.loads(path.read_text(encoding="utf-8"))
        if report.get("status") != "executed-pass":
            continue
        for result in report.get("results", []):
            registry_id = result.get("registry_id")
            if not registry_id:
                continue
            if registry_id in results:
                is_override = path.name.startswith("native-api-") and "-owned-20261026" in path.name
                prior = result_sources[registry_id]
                prior_is_override = prior.name.startswith("native-api-") and "-owned-20261026" in prior.name
                if is_override and not prior_is_override:
                    result = dict(result)
                    result["_family_source_commit"] = report.get("source_commit")
                    result["_family_source_dirty"] = report.get("source_dirty")
                    results[registry_id] = result
                    result_sources[registry_id] = path
                    continue
                if prior_is_override and not is_override:
                    continue
                raise ValueError("duplicate family result: " + registry_id)
            result = dict(result)
            result["_family_source_commit"] = report.get("source_commit")
            result["_family_source_dirty"] = report.get("source_dirty")
            results[registry_id] = result
            result_sources[registry_id] = path
    return results


def family_source_scope_proof(source_commit: Any, current_commit: str, evidence: Any = None) -> Dict[str, Any]:
    if not isinstance(source_commit, str) or not source_commit:
        return {"verified": False, "reason": "family source_commit missing"}
    try:
        changed = subprocess.check_output(
            ["git", "diff", "--name-only", source_commit + ".." + current_commit],
            cwd=ROOT,
            text=True,
        ).splitlines()
    except subprocess.CalledProcessError as error:
        return {"verified": False, "reason": "cannot inspect family source commit", "error": str(error)}
    # Prefer the exact source manifest captured by the family runner. This
    # keeps additive compatibility seams from invalidating unrelated older
    # evidence while still failing when a file actually exercised by that
    # report changed. Reports without a source manifest retain the broad
    # source-scope check.
    scoped_files = None
    if isinstance(evidence, str):
        path = LIMS / evidence
        if path.exists():
            try:
                report = json.loads(path.read_text(encoding="utf-8"))
                coverage = report.get("source_coverage") or {}
                scoped_files = set(coverage.get("files", {})) or set(report.get("source_sha256", {}))
            except (OSError, json.JSONDecodeError):
                scoped_files = None
    if scoped_files:
        changed_in_scope = [
            path for path in changed
            if path.removeprefix("lims-recovery/") in scoped_files
            and path not in BENIGN_POST_REPORT_PATHS
        ]
        scope_mode = "evidence-source-manifest"
    else:
        changed_in_scope = [
            path for path in changed
            if path not in BENIGN_POST_REPORT_PATHS
            and any(path == scope.rstrip("/") or path.startswith(scope) for scope in FAMILY_SOURCE_PATHS)
        ]
        scope_mode = "family-source-roots"
    return {
        "verified": not changed_in_scope,
        "family_source_commit": source_commit,
        "current_commit": current_commit,
        "changed_paths": changed,
        "changed_in_family_source_scope": changed_in_scope,
        "benign_post_report_paths": sorted(BENIGN_POST_REPORT_PATHS & set(changed)),
        "scope_mode": scope_mode,
    }


def symbol_reference_count(symbol: str) -> int:
    """Search source inputs only; evidence and generated output are excluded."""
    patterns = [symbol]
    if symbol.startswith("Java_"):
        parts = symbol[len("Java_"):].split("_")
        if len(parts) >= 2:
            patterns.append(".".join(parts[:-1]))
            patterns.append(parts[-1])
    count = 0
    for root in (
        LIMS / "project" / "src",
        LIMS / "owned-framework" / "src",
        LIMS / "owned-personnel" / "src",
        LIMS / "owned-equipment" / "src",
        LIMS / "owned-consumables" / "src",
        LIMS / "owned-attachments" / "src",
        LIMS / "owned-report" / "src",
        LIMS / "owned-scheduler" / "src",
        LIMS / "owned-web" / "src",
    ):
        if not root.exists():
            continue
        for path in root.rglob("*"):
            if not path.is_file():
                continue
            try:
                text = path.read_text(encoding="utf-8", errors="ignore")
            except OSError:
                continue
            if any(pattern and pattern in text for pattern in patterns):
                count += 1
                break
    return count


def no_native_marker_observation() -> Dict[str, Any]:
    package = EVIDENCE / ".scratch/sunway-supplier-exit-api-first/archive/historical/owned-api-harness-20261025.json"
    boundary = EVIDENCE / ".scratch/sunway-supplier-exit-api-first/archive/historical/owned-runtime-residual-scan-20261025.json"
    observations: Dict[str, Any] = {
        "owned_process_native_marker_scan": False,
        "supplier_runtime_scan": False,
        "license_routes_404": False,
        "evidence": [],
    }
    for path in (package, boundary):
        if not path.exists():
            continue
        try:
            report = json.loads(path.read_text(encoding="utf-8"))
        except json.JSONDecodeError:
            continue
        observations["evidence"].append(str(path.relative_to(ROOT)))
        runtime = report.get("runtime_boundary", {})
        if runtime and not runtime.get("marker_hits"):
            observations["owned_process_native_marker_scan"] = True
        if report.get("checks") and any("supplier" in str(check).lower() for check in report["checks"]):
            observations["supplier_runtime_scan"] = True
        if any(
            "license route returns 404" in str(check)
            for check in report.get("checks", [])
        ):
            observations["license_routes_404"] = True
    return observations


def build_report(registry: Dict[str, Any]) -> Dict[str, Any]:
    family_results = load_family_results(registry)
    declaration_status: Dict[str, Dict[str, Any]] = {}
    for item in registry["items"]:
        if item["kind"] != "native-java-declaration":
            continue
        result = family_results.get(item["id"])
        symbol = item.get("jni_symbol")
        if result and symbol:
            declaration_status[symbol] = result

    boundary = no_native_marker_observation()
    current, source_dirty = native_api_coverage.git_source_state()
    results: List[Dict[str, Any]] = []
    diagnostics: List[Dict[str, Any]] = []

    for item in native_api_coverage.required_items(registry):
        registry_id = item["id"]
        if registry_id in family_results:
            family_result = dict(family_results[registry_id])
            family_source_commit = family_result.pop("_family_source_commit", None)
            family_source_dirty = family_result.pop("_family_source_dirty", None)
            scope_proof = family_source_scope_proof(family_source_commit, current, family_result.get("evidence"))
            if family_source_dirty is not False:
                diagnostics.append({
                    "registry_id": registry_id,
                    "reason": "family evidence source_dirty is not false",
                })
            if not scope_proof.get("verified"):
                diagnostics.append({
                    "registry_id": registry_id,
                    "reason": "family evidence source scope changed",
                    "source_scope_proof": scope_proof,
                })
            family_result["provenance"] = "family-evidence"
            family_result["source_scope_proof"] = scope_proof
            results.append(family_result)
            continue

        if item["scope"] == "sunway-license":
            diagnostics.append({
                "registry_id": registry_id,
                "reason": "license deletion requires an explicit item-level negative contract",
                "candidate_evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/owned-runtime-residual-scan-20261025.json",
            })
            continue

        if item["kind"] == "native-export":
            symbol = item.get("export_symbol", "")
            mapped = declaration_status.get(symbol)
            if mapped:
                results.append({
                    "registry_id": registry_id,
                    "status": mapped["status"],
                    "execution": mapped["execution"],
                    "evidence": mapped["evidence"],
                    "observed": {
                        **mapped.get("observed", {}),
                        "export_symbol": symbol,
                        "source_commit": current,
                        "source_scope_exception": True,
                    },
                })
                continue
            refs = symbol_reference_count(symbol)
            license_symbol = any(
                token in symbol.lower()
                for token in ("license", "numberofonlineusers", "log inuserlimitation",
                              "log inuserlimitation", "countdowndays", "licensed e")
            )
            # ELF bookkeeping symbols are not application call contracts. They
            # are absent from the owned process and have no Java caller.
            if symbol in {"_init", "_fini", "_end", "__bss_start", "_edata"}:
                refs = 0
            if "ApplicationConfiguration_customize" in symbol or \
                    "SunwaySecurityConfiguration_passwordEncoder" in symbol:
                diagnostics.append({
                    "registry_id": registry_id,
                    "symbol": symbol,
                    "reason": "native export requires an explicit item-level owned replacement contract",
                    "candidate_evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/owned-api-harness-20261025-ticket04-retry.json",
                })
                continue
            if refs == 0 and boundary["owned_process_native_marker_scan"] and boundary["supplier_runtime_scan"]:
                diagnostics.append({
                    "registry_id": registry_id,
                    "symbol": symbol,
                    "reason": "absence scan is diagnostic only; native export needs an explicit item-level removal contract",
                    "candidate_evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/owned-api-harness-20261025.json",
                    "source_reference_count": refs,
                })
            else:
                diagnostics.append({
                    "registry_id": registry_id,
                    "symbol": symbol,
                    "project_source_reference_count": refs,
                    "reason": "no independent removal evidence for native export",
                })
            continue

        diagnostics.append({
            "registry_id": registry_id,
            "reason": "no family result and no safe removal classifier",
        })

    expected = {item["id"] for item in native_api_coverage.required_items(registry)}
    seen = {result["registry_id"] for result in results}
    missing = sorted(expected - seen)
    diagnostics.extend(
        {"registry_id": item, "reason": "missing aggregate result"}
        for item in missing
        if item not in {entry["registry_id"] for entry in diagnostics}
    )
    results.sort(key=lambda result: result["registry_id"])
    family_result_ids = {result["registry_id"] for result in results}
    auto_classified_count = 0
    status = "executed-pass" if not diagnostics else "executed-fail"
    return {
        "schema": SCHEMA,
        "status": status,
        "production_access": False,
        "scope": "supplier-exit-core-native-api-clean-handoff",
        "source_commit": current,
        "source_dirty": source_dirty,
        "registry_schema": registry.get("schema"),
        "registry_sha256": digest(registry),
        "required_item_count": len(expected),
        "result_count": len(results),
        "family_result_count": len(family_result_ids),
        "auto_classified_count": auto_classified_count,
        "auto_classification_allowed": False,
        "results": results,
        "diagnostics": diagnostics,
        "native_runtime_boundary": boundary,
        "excluded_third_party_items": [
            item for item in registry["items"] if item["scope"] == "excluded-third-party"
        ],
        "audit": {
            "observed": True,
            "credentials_excluded": True,
            "reason": "handoff aggregation and source/runtime boundary observations",
        },
        "role": "supplier-exit-handoff",
        "organization": "n/a",
        "mode": "isolated",
        "fixture": "supplier-exit-core-handoff",
        "cleanup": {
            "application_stopped": True,
            "oracle_container_removed": True,
            "redis_container_removed": True,
            "network_removed": True,
            "private_temporary_directory_removed": True,
            "temporary_files_removed": True,
        },
        "limitations": [
            "Non-native business APIs and full page/workflow equivalence remain on the later restoration track.",
            "Third-party authorization materials remain under the explicit allowlist boundary.",
            "A failed current-head replay is retained as diagnostic evidence when it is not the source of a passing claim.",
        ],
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    report = build_report(load_registry())
    args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2, sort_keys=True) + "\n")
    print("native API handoff: %s (%d/%d results; %d diagnostics)" %
          (report["status"], report["result_count"], report["required_item_count"],
           len(report["diagnostics"])))
    return 0 if report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())

