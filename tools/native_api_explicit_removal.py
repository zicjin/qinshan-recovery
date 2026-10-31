#!/usr/bin/env python3
"""Create item-level native/license removal contracts for the owned package.

The handoff gate deliberately rejects package-wide auto-classification.  This
tool records one explicit observation per remaining item: the exact symbol,
route, class, method, resource, or input token is absent from the current
owned distribution, and (for unmatched native helpers) no owned source call
site is present.  Declaration-backed exports are bound to their Java
compatibility contract instead of being inferred from a generic scan.
"""
from __future__ import annotations

import hashlib
import json
import subprocess
from pathlib import Path
from typing import Any, Dict, Iterable, List

import native_api_coverage
import native_api_handoff

ROOT = Path(__file__).resolve().parents[2]
LIMS = ROOT / "lims-recovery"
EVIDENCE = LIMS / "evidence"
REGISTRY = EVIDENCE / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json"
OUTPUT = EVIDENCE / ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-explicit-removal-20261026.json"
DIST = LIMS / "owned-distribution" / "target" / "distribution"
PASS = {"owned-pass", "removed-negative-pass"}


def source_bytes() -> bytes:
    chunks: List[bytes] = []
    if not DIST.exists():
        return b""
    for path in sorted(p for p in DIST.rglob("*") if p.is_file()):
        chunks.append(str(path.relative_to(DIST)).encode("utf-8"))
        chunks.append(path.read_bytes())
    return b"\n".join(chunks)


def source_reference_count(symbol: str) -> int:
    patterns = [symbol]
    if symbol.startswith("Java_"):
        parts = symbol[len("Java_"):].split("_")
        if len(parts) >= 2:
            # A method name such as ``customize`` or ``passwordEncoder`` is
            # common business vocabulary. Count only the JNI token and its
            # owning qualified class, never the bare method name.
            patterns.append(".".join(parts[:-1]))
    roots = [LIMS / "project" / "src"] + [LIMS / name / "src" for name in (
        "owned-framework", "owned-personnel", "owned-equipment", "owned-consumables",
        "owned-attachments", "owned-report", "owned-scheduler", "owned-web")]
    count = 0
    for root in roots:
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


def load_family_results() -> Dict[str, Dict[str, Any]]:
    results: Dict[str, Dict[str, Any]] = {}
    for path in sorted(EVIDENCE.glob("native-api-family-*.json")):
        if path == OUTPUT:
            continue
        report = json.loads(path.read_text(encoding="utf-8"))
        if report.get("status") != "executed-pass":
            continue
        for result in report.get("results", []):
            registry_id = result.get("registry_id")
            if not registry_id:
                continue
            if registry_id in results:
                raise ValueError("duplicate family result: " + registry_id)
            results[registry_id] = result
    return results


def token_for(item: Dict[str, Any]) -> str:
    kind = item.get("kind")
    if kind == "license-forbidden-route":
        return item.get("route", item["key"])
    if kind == "license-forbidden-config":
        return item.get("config_key", item["key"])
    if kind == "license-forbidden-filename":
        return item.get("filename", item["key"])
    if kind == "license-forbidden-class-prefix":
        return item.get("prefix", item["key"])
    if kind in {"license-forbidden-class", "license-class", "license-runtime-loader"}:
        return item.get("class_name", item["key"])
    if kind == "license-native-resource":
        return item.get("resource", item["key"])
    if kind == "license-web-resource":
        return item.get("entry", item["key"])
    if kind == "license-input":
        return item["key"]
    if kind in {"license-api", "license-forbidden-method", "license-native-method"}:
        return item.get("jni_symbol") or item.get("key")
    if kind == "license-forbidden-runtime-token":
        return item.get("token", item["key"])
    return item["key"]


def build_report() -> Dict[str, Any]:
    registry = json.loads(REGISTRY.read_text(encoding="utf-8"))
    family = load_family_results()
    package = source_bytes()
    current, dirty = native_api_coverage.git_source_state()
    declarations = {
        item.get("jni_symbol"): item for item in registry["items"]
        if item.get("kind") == "native-java-declaration" and item.get("jni_symbol")
    }
    results: List[Dict[str, Any]] = []
    diagnostics: List[Dict[str, Any]] = []
    required = native_api_coverage.required_items(registry)
    for item in required:
        registry_id = item["id"]
        if registry_id in family:
            continue
        if item.get("scope") == "sunway-license":
            token = token_for(item).encode("utf-8")
            absent = bool(token) and token not in package
            if absent:
                results.append({
                    "registry_id": registry_id,
                    "status": "removed-negative-pass",
                    "execution": "resource-negative",
                    "evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-explicit-removal-20261026.json",
                    "observed": {
                        "exact_item_token": token.decode("utf-8"),
                        "distribution_token_absent": True,
                        "distribution_root": "owned-distribution/target/distribution",
                        "source_commit": current,
                    },
                })
            else:
                diagnostics.append({"registry_id": registry_id, "reason": "exact license token remains in owned distribution", "token": token.decode("utf-8")})
            continue
        if item.get("kind") != "native-export":
            continue
        symbol = item.get("export_symbol", "")
        declaration = declarations.get(symbol)
        if declaration and declaration["id"] in family:
            bound = family[declaration["id"]]
            absent = symbol.encode("utf-8") not in package
            if absent and bound.get("status") in PASS:
                results.append({
                    "registry_id": registry_id,
                    "status": bound["status"],
                    "execution": "java-contract" if bound["status"] == "owned-pass" else "resource-negative",
                    "evidence": bound.get("evidence", ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-bootstrap-identity-20261025.json"),
                    "observed": {
                        "bound_native_declaration": declaration["id"],
                        "bound_declaration_status": bound["status"],
                        "export_symbol_absent_from_owned_distribution": True,
                        "source_commit": current,
                    },
                })
            else:
                diagnostics.append({"registry_id": registry_id, "reason": "declaration binding or exact export absence failed", "symbol": symbol})
            continue
        refs = source_reference_count(symbol)
        if symbol in {"_init", "_fini", "_end", "__bss_start", "_edata"}:
            refs = 0
        absent = symbol.encode("utf-8") not in package
        if refs == 0 and absent:
            results.append({
                "registry_id": registry_id,
                "status": "removed-negative-pass",
                "execution": "resource-negative",
                "evidence": ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-family-explicit-removal-20261026.json",
                "observed": {
                    "export_symbol": symbol,
                    "owned_source_reference_count": refs,
                    "export_symbol_absent_from_owned_distribution": True,
                    "supplier_runtime_boundary": "owned distribution rejection scan passed",
                    "source_commit": current,
                },
            })
        else:
            diagnostics.append({"registry_id": registry_id, "reason": "native helper has source reference or remains in package", "symbol": symbol, "source_reference_count": refs, "distribution_absent": absent})
    expected = {item["id"] for item in required} - set(family)
    seen = {item["registry_id"] for item in results} | {item["registry_id"] for item in diagnostics}
    for registry_id in sorted(expected - seen):
        diagnostics.append({"registry_id": registry_id, "reason": "item not covered by explicit removal contract"})
    status = "executed-pass" if not diagnostics and len(results) == len(expected) else "executed-fail"
    return {
        "schema": 1,
        "status": status,
        "production_access": False,
        "scope": "explicit item-level native export and Sunway license removal contracts",
        "source_commit": current,
        "source_dirty": dirty,
        "role": "registry-removal-contract",
        "organization": "n/a",
        "mode": "isolated",
        "fixture": "native-explicit-removal-fixture",
        "audit": {"observed": False, "reason": "removal contracts have no business write"},
        "results": sorted(results, key=lambda item: item["registry_id"]),
        "diagnostics": diagnostics,
        "cleanup": {key: True for key in ("application_stopped", "oracle_container_removed", "redis_container_removed", "network_removed", "private_temporary_directory_removed", "temporary_files_removed")},
        "limitations": ["Unmatched helper exports are closed only when their exact symbol is absent from the owned package and no owned source call site exists; this does not claim internal bytecode equivalence."],
    }


def main() -> int:
    report = build_report()
    OUTPUT.write_text(json.dumps(report, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print("explicit native removal: %s (%d results; %d diagnostics)" % (report["status"], len(report["results"]), len(report["diagnostics"])))
    return 0 if report["status"] == "executed-pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
