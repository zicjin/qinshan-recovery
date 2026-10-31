#!/usr/bin/env python3
"""Build a redacted registry of external-native APIs and deletion paths.

The registry is derived only from committed contract inventories and the scoped
Sunway license-removal manifest. It never reads license bodies, credentials, or
production configuration. Runtime reachability is deliberately represented as
an explicit status instead of being inferred from a static symbol.
"""
from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
from typing import Any, Dict, Iterable, List

SCHEMA = 1
NATIVE_STATUSES = {"owned-pass", "removed-negative-pass", "unknown"}
BOUNDARY_STATUSES = {"deferred", "unknown"}


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            h.update(block)
    return h.hexdigest()


def load_jsonl(path: Path) -> List[Dict[str, Any]]:
    return [json.loads(line) for line in path.read_text(encoding="utf-8").splitlines() if line.strip()]


def family(owner: str) -> str:
    owner = owner.replace("/", ".")
    if ".boot." in owner:
        return "bootstrap-web"
    if ".security." in owner or ".context." in owner:
        return "security-identity"
    if ".tenant." in owner:
        return "tenant-organization"
    if ".mybatis." in owner or ".framework.utils." in owner:
        return "data-access-conversion"
    if ".schedule" in owner or ".task" in owner:
        return "scheduler-integration"
    return "native-framework"


def add_item(items: List[Dict[str, Any]], *, kind: str, scope: str, status: str,
             outcome: str, key: str, source: Dict[str, Any], **fields: Any) -> None:
    canonical = json.dumps({"kind": kind, "scope": scope, "key": key}, sort_keys=True, ensure_ascii=False)
    item = {
        "id": f"{kind}:{hashlib.sha256(canonical.encode('utf-8')).hexdigest()[:16]}",
        "kind": kind,
        "scope": scope,
        "status": status,
        "expected_outcome": outcome,
        "key": key,
        "source": source,
    }
    item.update(fields)
    items.append(item)


def manifest_source(manifest_path: str, section: str, index: Any = None) -> Dict[str, Any]:
    value = {"manifest": manifest_path, "section": section}
    if index is not None:
        value["index"] = index
    return value


def build_registry(root: Path) -> Dict[str, Any]:
    evidence = root / "lims-recovery" / "evidence"
    contract = evidence / "framework-contract"
    manifest_path = evidence / "license-removal" / "manifest.json"
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    declarations = load_jsonl(contract / "native-declarations.jsonl")
    correspondence = load_jsonl(contract / "native-correspondence.jsonl")
    exports = load_jsonl(contract / "native-exports.jsonl")
    items: List[Dict[str, Any]] = []

    correspondence_by_symbol = {row["symbol"]: row for row in correspondence}
    for row in declarations:
        symbol = next((s for s in correspondence_by_symbol if s.endswith("_" + row["name"]) and row["class"].replace("/", "_") in s), None)
        add_item(
            items,
            kind="native-java-declaration",
            scope="external-native",
            status="unknown",
            outcome="owned implementation or explicit removal",
            key=f"{row['class']}#{row['name']}{row['descriptor']}",
            source={"evidence": "evidence/framework-contract/native-declarations.jsonl", "jar": row["jar"], "entry": row["entry"]},
            owner=row["class"],
            name=row["name"],
            descriptor=row["descriptor"],
            family=family(row["class"]),
            jni_symbol=symbol,
            runtime_observation=(correspondence_by_symbol.get(symbol) or {}).get("runtime_observation", "unknown"),
        )

    java_exports = [row for row in exports if row["symbol"].startswith("Java_")]
    for row in exports:
        corr = correspondence_by_symbol.get(row["symbol"], {})
        add_item(
            items,
            kind="native-export",
            scope="external-native",
            status="unknown",
            outcome="owned implementation or explicit removal",
            key=row["symbol"],
            source={"evidence": "evidence/framework-contract/native-exports.jsonl"},
            export_symbol=row["symbol"],
            export_family="java-jni" if row["symbol"].startswith("Java_") else "native-helper",
            declaration_status=corr.get("status", "unmatched"),
            runtime_observation=corr.get("runtime_observation", "unknown"),
        )

    deletion = manifest["sunway_license_deletion"]
    for index, row in enumerate(deletion["classes"]):
        add_item(items, kind="license-class", scope="sunway-license", status="removed-negative-pass",
                 outcome="removed from owned runtime", key=row["name"], source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.classes", index),
                 class_name=row["name"], entry=row["entry"])
    for index, row in enumerate(deletion["services_and_routes"]):
        if "route" in row:
            routes = [row["route"]] + [row["route"] + suffix for suffix in row.get("route_suffixes", [])]
            for route in routes:
                add_item(items, kind="license-route", scope="sunway-license", status="removed-negative-pass",
                         outcome="route returns removed negative result", key=route, source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.services_and_routes", index),
                         owner=row["class"], methods=row.get("methods", []), route=route)
        for method_key in ("method",):
            if method_key in row:
                add_item(items, kind="license-api", scope="sunway-license", status="removed-negative-pass",
                         outcome="API removed from owned runtime", key=f"{row['class']}#{row[method_key]}", source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.services_and_routes", index),
                         owner=row["class"], method=row[method_key])
        for method in row.get("methods", []):
            add_item(items, kind="license-api", scope="sunway-license", status="removed-negative-pass",
                     outcome="API removed from owned runtime", key=f"{row['class']}#{method}", source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.services_and_routes", index),
                     owner=row["class"], method=method)
    for index, row in enumerate(deletion["native_methods"]):
        add_item(items, kind="license-native-method", scope="sunway-license", status="removed-negative-pass",
                 outcome="native license path has no owned runtime call", key=f"{row['owner']}#{row['name']}{row['descriptor']}", source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.native_methods", index),
                 owner=row["owner"], name=row["name"], descriptor=row["descriptor"], jni_symbol=row["jni_symbol"])
    runtime = deletion["runtime_loading"]
    add_item(items, kind="license-runtime-loader", scope="sunway-license", status="removed-negative-pass",
             outcome="supplier native loader absent from owned runtime", key=runtime["class"], source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.runtime_loading"),
             class_name=runtime["class"], resources=runtime["resources"])
    for resource in runtime["resources"]:
        add_item(items, kind="license-native-resource", scope="sunway-license", status="removed-negative-pass",
                 outcome="resource absent from owned runtime", key=resource, source=manifest_source("evidence/license-removal/manifest.json", "sunway_license_deletion.runtime_loading"),
                 resource=resource)

    for index, row in enumerate(manifest["sunway_config_and_host_inputs"]):
        key = row.get("key") or row.get("path")
        add_item(items, kind="license-input", scope="sunway-license", status="removed-negative-pass" if row["classification"] == "delete" else "deferred",
                 outcome="deleted when Sunway is the consumer" if row["classification"] != "retain-for-PageOffice-until-authority-review" else "retained only for third-party boundary review",
                 key=key, source=manifest_source("evidence/license-removal/manifest.json", "sunway_config_and_host_inputs", index),
                 classification=row["classification"], source_reference=row.get("source"), reason=row.get("reason"))

    policy = manifest["scanner_policy"]
    for filename in policy["forbidden_filenames"]:
        add_item(items, kind="license-forbidden-filename", scope="sunway-license", status="removed-negative-pass",
                 outcome="filename absent from owned runtime", key=filename, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_filenames"), filename=filename)
    for prefix in policy["forbidden_class_prefixes"]:
        add_item(items, kind="license-forbidden-class-prefix", scope="sunway-license", status="removed-negative-pass",
                 outcome="class prefix absent from owned runtime", key=prefix, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_class_prefixes"), prefix=prefix)
    for row in policy["forbidden_classes"]:
        add_item(items, kind="license-forbidden-class", scope="sunway-license", status="removed-negative-pass",
                 outcome="class absent from owned runtime", key=row, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_classes"), class_name=row)
    for row in policy["forbidden_methods"]:
        key = f"{row['owner']}#{row['name']}{row['descriptor']}"
        add_item(items, kind="license-forbidden-method", scope="sunway-license", status="removed-negative-pass",
                 outcome="method absent from owned runtime", key=key, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_methods"), **row)
    for route in policy["forbidden_routes"]:
        add_item(items, kind="license-forbidden-route", scope="sunway-license", status="removed-negative-pass",
                 outcome="route returns removed negative result", key=route, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_routes"), route=route)
    for key_name in policy["forbidden_config_keys"]:
        add_item(items, kind="license-forbidden-config", scope="sunway-license", status="removed-negative-pass",
                 outcome="config key absent from owned runtime", key=key_name, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_config_keys"), config_key=key_name)
    for token in policy["forbidden_runtime_tokens"]:
        add_item(items, kind="license-forbidden-runtime-token", scope="sunway-license", status="removed-negative-pass",
                 outcome="token absent from owned process and distribution", key=token, source=manifest_source("evidence/license-removal/manifest.json", "scanner_policy.forbidden_runtime_tokens"), token=token)
    for index, row in enumerate(manifest["web_resource_branches"]):
        add_item(items, kind="license-web-resource", scope="sunway-license", status="removed-negative-pass",
                 outcome="license-related resource branch removed while retained resource remains scoped", key=f"{row['jar']}!{row['entry']}", source=manifest_source("evidence/license-removal/manifest.json", "web_resource_branches", index),
                 jar=row["jar"], entry=row["entry"], action=row["action"])

    # Third-party consumer observations are kept as explicit boundary references,
    # but are not counted as Sunway API closure items.
    for index, row in enumerate(manifest["static_consumer_evidence"]):
        add_item(items, kind="third-party-boundary", scope="excluded-third-party", status="deferred",
                 outcome="retain and review under third-party authorization boundary", key=f"{row.get('jar')}!{row['entry']}", source=manifest_source("evidence/license-removal/manifest.json", "static_consumer_evidence", index),
                 observation=row["observation"])
    for index, row in enumerate(manifest["retained_third_party_or_business"]):
        key = row.get("path") or row.get("class") or row.get("consumer") or str(index)
        add_item(items, kind="retained-boundary", scope="excluded-third-party", status="deferred",
                 outcome="retain under scoped allowlist", key=key, source=manifest_source("evidence/license-removal/manifest.json", "retained_third_party_or_business", index),
                 classification=row.get("classification"))

    items.sort(key=lambda row: row["id"])
    source_files = [contract / "native-declarations.jsonl", contract / "native-correspondence.jsonl", contract / "native-exports.jsonl", manifest_path]
    recovered = evidence / "function-catalog" / "recovered-source.json"
    recovered_data = json.loads(recovered.read_text(encoding="utf-8"))
    registry = {
        "schema": SCHEMA,
        "scope": "external-native-api-registry",
        "source": {
            "contract_inventory": "evidence/framework-contract",
            "license_manifest": "evidence/license-removal/manifest.json",
            "source_index": "evidence/function-catalog/recovered-source.json",
            "nested_project_commit": recovered_data.get("source_commit"),
            "nested_project_tree_sha256": recovered_data.get("source_tree_sha256"),
            "nested_project_dirty": recovered_data.get("source_worktree_dirty"),
            "inputs": [{"path": str(path.relative_to(root)), "sha256": sha256(path)} for path in source_files + [recovered]],
        },
        "policy": {
            "native_statuses": sorted(NATIVE_STATUSES),
            "boundary_statuses": sorted(BOUNDARY_STATUSES),
            "unknown_blocks_external_native_closure": True,
            "credentials_and_license_bodies_retained": False,
        },
        "counts": {
            "native_declarations": len(declarations),
            "java_native_exports": len(java_exports),
            "native_exports": len(exports),
            "license_deletion_items": sum(1 for item in items if item["scope"] == "sunway-license"),
            "registry_items": len(items),
            "external_native_items": sum(1 for item in items if item["scope"] == "external-native"),
            "boundary_items": sum(1 for item in items if item["scope"] == "excluded-third-party"),
        },
        "items": items,
        "limitations": [
            "Static native declarations and exports do not prove runtime reachability.",
            "The nested project source index is used as a pinned source identity when the nested checkout is unavailable in this worktree.",
            "Third-party license consumers are retained as boundary references and do not count as Sunway license closure items.",
        ],
    }
    return registry


def validate_registry(registry: Dict[str, Any]) -> List[str]:
    errors: List[str] = []
    if registry.get("schema") != SCHEMA:
        errors.append("schema must be 1")
    items = registry.get("items")
    if not isinstance(items, list):
        return ["items must be a list"]
    ids = [item.get("id") for item in items]
    if any(not item_id for item_id in ids):
        errors.append("every item requires id")
    if len(ids) != len(set(ids)):
        errors.append("item ids must be unique")
    if ids != sorted(ids):
        errors.append("items must be sorted by id")
    native_count = sum(1 for item in items if item.get("scope") == "external-native")
    boundary_count = sum(1 for item in items if item.get("scope") == "excluded-third-party")
    if registry.get("counts", {}).get("external_native_items") != native_count:
        errors.append("external_native_items count mismatch")
    if registry.get("counts", {}).get("boundary_items") != boundary_count:
        errors.append("boundary_items count mismatch")
    for item in items:
        status = item.get("status")
        if item.get("scope") == "external-native" and status not in NATIVE_STATUSES:
            errors.append(f"invalid native status for {item.get('id')}")
        if item.get("scope") == "excluded-third-party" and status not in BOUNDARY_STATUSES:
            errors.append(f"invalid boundary status for {item.get('id')}")
        serialized = json.dumps(item, ensure_ascii=False).lower()
        if "oracle_sys_password" in serialized or "password=" in serialized:
            errors.append(f"credential marker in {item.get('id')}")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[2]
    registry = build_registry(root)
    errors = validate_registry(registry)
    if errors:
        raise SystemExit("; ".join(errors))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(registry, ensure_ascii=False, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print("native API registry: PASS", registry["counts"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
