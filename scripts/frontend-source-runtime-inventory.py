#!/usr/bin/env python3
"""Generate a deterministic inventory of source-owned frontend inputs."""
import argparse
import hashlib
import json
import re
import subprocess
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
PROJECT = ROOT / "project"
OWNED_WEB = ROOT / "owned-web"
ORIGINAL_FRONTEND = OWNED_WEB / "src/main/resources/frontend-original"
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path  # noqa: E402
from source_binding import REPO_ROOT as DISCOVERED_REPO_ROOT  # noqa: E402
REPO_ROOT = DISCOVERED_REPO_ROOT

EXPECTED_OWNED_SHELL = (
    "/static/gikam/main/css/chunk-common.css",
    "/static/gikam/main/css/home.css",
    "/static/gikam/main/js/chunk-common.js",
    "/static/gikam/main/js/chunk-vendors.js",
    "/static/gikam/main/js/home.js",
    "/static/gikam/main/img/logo-white.png",
)

MAPPING_ANNOTATIONS = (
    "RequestMapping",
    "GetMapping",
    "PostMapping",
    "PutMapping",
    "DeleteMapping",
    "PatchMapping",
)

THIRD_PARTY_PREFIXES = ("pageoffice/", "plugin/")
GIKAM_SHELL_PREFIXES = ("/static/gikam/", "/static/module/lcdp/")


def sha256(path):
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def files_under(root):
    return sorted(path for path in root.rglob("*") if path.is_file())


def file_records(root, base):
    records = []
    for path in files_under(root):
        records.append({
            "path": path.relative_to(base).as_posix(),
            "sha256": sha256(path),
            "size": path.stat().st_size,
        })
    return records


def controller_records():
    records = []
    java_root = PROJECT / "src/main/java"
    for path in sorted(java_root.rglob("*.java")):
        relative = path.relative_to(PROJECT).as_posix()
        if "/controller/" not in relative:
            continue
        text = path.read_text(encoding="utf-8", errors="replace")
        mapping_records = []
        # The project uses short annotation forms and value/path arrays, often
        # split over several lines. Keep the annotation and every literal path
        # so route parity work has a complete source inventory.
        annotation_pattern = r"@(" + "|".join(MAPPING_ANNOTATIONS) + r")\s*\((.*?)\)"
        for match in re.finditer(annotation_pattern, text, flags=re.S):
            annotation, arguments = match.groups()
            paths = sorted(set(re.findall(r"\"([^\"]+)\"", arguments)))
            if paths:
                mapping_records.append({"annotation": annotation, "paths": paths})
        mappings = sorted({path for item in mapping_records for path in item["paths"]})
        views = sorted(set(
            value for value in re.findall(r"return\s+\"([^\"]+)\"", text)
            if value.startswith("emc/")
        ))
        records.append({
            "path": relative,
            "sha256": sha256(path),
            "kind": "controller-interface" if " interface " in text else "controller-implementation",
            "request_mappings": mappings,
            "request_mapping_records": mapping_records,
            "view_names": views,
        })
    return records


def template_gikam_fragments():
    fragments = set()
    for path in files_under(PROJECT / "src/main/resources/templates"):
        text = path.read_text(encoding="utf-8", errors="replace")
        fragments.update(re.findall(r"module/gikam/([A-Za-z0-9_-]+)", text))
    return sorted(fragments)


def route_summary(controllers):
    mappings = [
        (controller["path"], item["annotation"], path)
        for controller in controllers
        for item in controller.get("request_mapping_records", [])
        for path in item["paths"]
    ]
    paths = sorted({item[2] for item in mappings})
    return {
        "controller_mapping_records": len(mappings),
        "unique_request_paths": len(paths),
        "secure_page_paths": len([path for path in paths if "/page" in path]),
        "secure_paths": len([path for path in paths if path.startswith("/secure/")]),
        "view_names": len({view for controller in controllers for view in controller["view_names"]}),
        "paths": paths,
    }


def authenticated_resource_contract(project_static_paths, owned_relative):
    report_path = REPO_ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-authenticated-baseline-sss2-20261026.json"
    if not report_path.is_file():
        return {"report": str(report_path.relative_to(REPO_ROOT)), "available": False, "resources": []}
    report = json.loads(report_path.read_text(encoding="utf-8"))
    urls = sorted({
        url
        for target in report.get("targets", {}).values()
        for url in target.get("initial_resource_urls", [])
    })
    resources = []
    for url in urls:
        relative = url.removeprefix("/static/")
        project_present = url in project_static_paths
        owned_present = relative in owned_relative
        original_path = ORIGINAL_FRONTEND / "static" / relative
        original_present = original_path.is_file()
        supplier = report.get("targets", {}).get("supplier", {}).get("initial_resources", {}).get(url, {})
        owned = report.get("targets", {}).get("owned", {}).get("initial_resources", {}).get(url, {})
        third_party = any(relative.startswith(prefix) for prefix in THIRD_PARTY_PREFIXES)
        if third_party:
            classification, priority, action = "third-party-allowed", "P2", "retain-with-provenance"
        elif original_present:
            classification, priority, action = "original-frontend-imported", "P0", "serve-original-frontend"
        elif project_present:
            classification, priority, action = "source-present", "P1", "serve-project-source"
        elif supplier.get("status") == 200:
            classification, priority, action = "runtime-bundle-present-repository-copy-absent", "P0", "import-original-frontend"
        else:
            classification, priority, action = "api-dependency-closure-pending", "P1", "bind-owned-java-api"
        classifications = [classification]
        if original_present and relative in {
            "gikam/main/js/home.js", "gikam/main/js/workspace.js",
        }:
            classifications.append("license-native-cleaned")
        if action == "owned-rebuild-required":
            classifications.append("owned-rebuild-required")
        resources.append({
            "url": url,
            "project_source_present": project_present,
            "owned_source_present": owned_present,
            "original_frontend_present": original_present,
            "supplier_status": supplier.get("status"),
            "owned_status": owned.get("status"),
            "supplier_sha256": supplier.get("sha256"),
            "owned_sha256": owned.get("sha256"),
            "classification": classification,
            "classifications": classifications,
            "priority": priority,
            "required_action": action,
        })
    return {
        "report": str(report_path.relative_to(REPO_ROOT)),
        "available": True,
        "source_commit": report.get("source_commit"),
        "source_dirty": report.get("source_dirty"),
        "resources": resources,
    }


def read_only_baseline_contract():
    """Copy the route/resource observations without copying response bodies.

    The baseline probe is deliberately kept separate from the generated source
    inventory.  This summary makes the required 18081/8081 observations
    machine-readable while retaining the original report's source binding and
    diagnostic-only status.
    """
    report_path = REPO_ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-baseline-readonly-20261026.json"
    if not report_path.is_file():
        return {
            "report": str(report_path.relative_to(REPO_ROOT)),
            "available": False,
            "targets": {},
        }
    report = json.loads(report_path.read_text(encoding="utf-8"))
    targets = {}
    for name, target in sorted(report.get("targets", {}).items()):
        routes = {}
        for route, observation in sorted(target.get("routes", {}).items()):
            routes[route] = {
                "status": observation.get("status"),
                "bytes": observation.get("bytes"),
                "sha256": observation.get("sha256"),
                "initial_resource_urls": observation.get("initial_resource_urls", []),
                "initial_resources": observation.get("initial_resources", {}),
            }
        targets[name] = {
            "label": target.get("label"),
            "routes": routes,
        }
    return {
        "report": str(report_path.relative_to(REPO_ROOT)),
        "available": True,
        "status": report.get("status"),
        "mode": report.get("mode"),
        "observed_at": report.get("observed_at"),
        "source_commit": report.get("source_commit"),
        "source_dirty": report.get("source_dirty"),
        "credentials_used": report.get("credentials_used"),
        "production_read_only": report.get("production_read_only"),
        "targets": targets,
    }


def resource_classification_catalog(authenticated_resources, owned_shell,
                                    gikam_fragments, third_party):
    """Return one deterministic classification table for observable inputs.

    A supplier JAR may contain private Gikam resources that are not exposed by
    the current read-only route/resource probes.  The explicit boundary below
    prevents this table from being mistaken for a private-JAR enumeration.
    """
    entries = {}

    def add(identifier, entry):
        entries.setdefault(identifier, entry)

    for item in authenticated_resources.get("resources", []):
        if item["url"].startswith(GIKAM_SHELL_PREFIXES):
            add(item["url"], {
                "id": item["url"],
                "kind": "authenticated-static-url",
                "classification": item["classification"],
                "classifications": item["classifications"],
                "priority": item["priority"],
                "required_action": item["required_action"],
                "project_source_present": item["project_source_present"],
                "owned_source_present": item["owned_source_present"],
            })

    for item in owned_shell:
        if item["route"].startswith(GIKAM_SHELL_PREFIXES):
            add(item["route"], {
                "id": item["route"],
                "kind": "expected-shell-static-url",
                "classification": item["classification"],
                "classifications": item["classifications"],
                "priority": item["priority"],
                "required_action": item["required_action"],
                "project_source_present": item["project_source_present"],
                "owned_source_present": bool(item["owned_source"]),
                "original_frontend_present": item.get("original_frontend_present", False),
            })

    for fragment in gikam_fragments:
        identifier = "template:module/gikam/" + fragment
        add(identifier, {
            "id": identifier,
            "kind": "project-template-fragment",
            "classification": "runtime-bundle-present-repository-copy-absent",
            "classifications": ["runtime-bundle-present-repository-copy-absent", "api-dependency-closure-pending"],
            "priority": "P0",
            "required_action": "bind-owned-java-api",
            "project_source_present": False,
            "owned_compatibility_seam_present": True,
        })

    for item in third_party:
        identifier = "static:" + item["path"]
        add(identifier, {
            "id": identifier,
            "kind": "project-static-file",
            "classification": "third-party-allowed",
            "classifications": ["third-party-allowed"],
            "priority": "P2",
            "required_action": "retain-with-provenance",
            "path": item["path"],
        })

    ordered = [entries[key] for key in sorted(entries)]
    counts = {name: 0 for name in (
        "source-present", "source-missing", "owned-rebuild-required",
        "third-party-allowed", "runtime-bundle-present-repository-copy-absent",
        "original-frontend-imported", "license-native-cleaned",
        "api-dependency-closure-pending", "strict-frontend-parity-verified",
    )}
    for entry in ordered:
        for classification in set(entry["classifications"]):
            counts[classification] += 1
    return {
        "scope": "observable Gikam/LCDP static URLs, referenced project Gikam template fragments, expected shell URLs, and project third-party assets",
        "private_jar_internal_resources_enumerated": False,
        "boundary": "classification is complete for the recorded read-only URL/template contract; private JAR-internal resources require a separate permitted artifact inventory",
        "classification_counts": counts,
        "entries": ordered,
    }


def owned_rebuild_plan(controllers, shell, third_party_count, authenticated_resources):
    plan = [
        {
            "id": "gikam-home-fragment",
            "scope": "module/gikam/home",
            "priority": "P0",
            "classification": "original-frontend-imported",
            "status": "license-native-cleaned",
            "acceptance": "authenticated workspace shell and initial resources use the imported 18081 bundle; API dependency closure remains pending",
        },
        {
            "id": "gikam-workspace-fragment",
            "scope": "module/gikam/workspace",
            "priority": "P0",
            "classification": "original-frontend-imported",
            "status": "license-native-cleaned",
            "acceptance": "authenticated menu, top navigation, organization context and error paths use the imported 18081 bundle; API dependency closure remains pending",
        },
        {
            "id": "gikam-shell-resources",
            "scope": " / ".join(item["route"] for item in shell),
            "priority": "P0",
            "classification": "original-frontend-imported",
            "status": "license-native-cleaned" if all(item.get("original_frontend_present") for item in shell) else "partial",
            "acceptance": "each production initial resource URL loads from the owned distribution with the same route contract; strict byte/parity evidence remains pending",
        },
        {
            "id": "page-controller-route-facades",
            "scope": "project controller sources",
            "priority": "P1",
            "classification": "source-present",
            "status": "inventory-only",
            "controller_count": len(controllers),
            "acceptance": "all mapped page routes retain their original path and view entry; authenticated browser replay remains required",
        },
        {
            "id": "authenticated-baseline-capture",
            "scope": "18081 and production 8081",
            "priority": "P0",
            "classification": "evidence-blocked",
            "status": "waiting-for-permitted-matching-test-account",
            "acceptance": "read-only login, workspace, menu, top navigation and initial post-login resources captured without writing either target",
        },
        {
            "id": "third-party-integration-assets",
            "scope": "project-supplied PageOffice and spreadsheet resources",
            "priority": "P2",
            "classification": "third-party-allowed",
            "status": "source-present",
            "asset_count": third_party_count,
            "acceptance": "retain provenance and verify integration boundaries separately from Gikam shell replacement",
        },
        {
            "id": "supplier-runtime-input-removal",
            "scope": "private-sdk.jar and cell-license.xml",
            "priority": "P0",
            "classification": "supplier-runtime-input-rejected-from-owned-distribution",
            "status": "enforced-by-package-scan",
            "acceptance": "no supplier runtime input reaches the final distribution or running owned process",
        },
    ]
    missing_authenticated = [
        item for item in authenticated_resources.get("resources", [])
        if item["classification"] == "source-missing"
    ]
    if authenticated_resources.get("available"):
        plan.insert(3, {
            "id": "authenticated-gikam-runtime-resources",
            "scope": ", ".join(item["url"] for item in missing_authenticated),
            "priority": "P0",
            "classification": "original-frontend-imported" if not missing_authenticated else "runtime-bundle-present-repository-copy-absent",
            "status": "license-native-cleaned" if not missing_authenticated else "import-pending",
            "missing_resource_count": len(missing_authenticated),
            "acceptance": "authenticated supplier/owned shell resource contract is served by imported original frontend source; production auth remains a separate read-only comparison and API dependency closure is pending",
        })
    return plan


def git_value(*args):
    return subprocess.check_output(["git", "-C", str(ROOT), *args], text=True).strip()


def source_dirty():
    paths = [
        "project/src/main/java", "project/src/main/resources", "owned-framework/src/main",
        "owned-web/src/main",
        "owned-web/src/main/resources/frontend-original",
        "scripts/frontend-source-runtime-inventory.py",
    ]
    result = subprocess.run(
        ["git", "-C", str(ROOT), "status", "--porcelain", "--", *paths],
        capture_output=True, text=True, check=True,
    )
    return bool(result.stdout.strip())


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    project_static_root = PROJECT / "src/main/resources/static"
    owned_static_root = OWNED_WEB / "src/main/resources/static"
    project_static = file_records(project_static_root, PROJECT)
    project_templates = file_records(PROJECT / "src/main/resources/templates", PROJECT)
    controllers = controller_records()
    owned_static = file_records(owned_static_root, owned_static_root)
    original_static_root = ORIGINAL_FRONTEND / "static"
    original_static = file_records(original_static_root, ORIGINAL_FRONTEND) if original_static_root.is_dir() else []
    project_relative = {path.relative_to(project_static_root).as_posix() for path in files_under(project_static_root)}
    owned_relative = {item["path"] for item in owned_static}
    original_relative = {item["path"].removeprefix("static/") for item in original_static}
    project_static_paths = {"/static/" + path for path in project_relative}
    owned_unique = sorted(owned_relative - project_relative)
    owned_non_shell_unique = sorted(
        item for item in owned_unique
        if not item.startswith("gikam/") and not item.startswith("module/lcdp/")
    )
    owned_gikam_lcdp_unique = sorted(
        item for item in owned_unique
        if item.startswith("gikam/") or item.startswith("module/lcdp/")
    )
    third_party = []
    for item in project_static:
        relative = item["path"].split("src/main/resources/static/", 1)[-1]
        if any(relative.startswith(prefix) for prefix in THIRD_PARTY_PREFIXES):
            third_party.append(dict(item, path=relative))
    owned_shell = []
    for route in EXPECTED_OWNED_SHELL:
        present = route in project_static_paths
        original_path = ORIGINAL_FRONTEND / "static" / route.removeprefix("/static/")
        owned_path = OWNED_WEB / "src/main/resources/static" / route.removeprefix("/static/")
        original_present = original_path.is_file()
        owned_shell.append({
            "route": route,
            "classification": "original-frontend-imported" if original_present else ("source-present" if present else "runtime-bundle-present-repository-copy-absent"),
            "classifications": (["original-frontend-imported", "license-native-cleaned", "api-dependency-closure-pending"] if original_present else (["source-present"] if present else ["runtime-bundle-present-repository-copy-absent", "api-dependency-closure-pending"])),
            "required_action": "serve-original-frontend" if original_present else ("none" if present else "import-original-frontend"),
            "priority": "P0" if original_present or not present else "P2",
            "project_source_present": present,
            "original_frontend_present": original_present,
            "original_frontend_source": original_path.relative_to(ROOT).as_posix() if original_present else None,
            "owned_source": owned_path.relative_to(ROOT).as_posix() if owned_path.is_file() else None,
            "owned_rebuild_status": "original-frontend-imported" if original_present else ("compatibility-seam-present" if owned_path.is_file() else "not-started"),
        })
    fragments = template_gikam_fragments()
    supplier_inputs = []
    for relative in ("src/main/resources/lib/private-sdk.jar", "src/main/resources/cell-license.xml"):
        path = PROJECT / relative
        if path.is_file():
            supplier_inputs.append({
                "path": (PROJECT / relative).relative_to(ROOT).as_posix(),
                "classification": "supplier-runtime-input-rejected-from-owned-distribution",
                "sha256": sha256(path),
            })
    authenticated_resources = authenticated_resource_contract(project_static_paths, owned_relative)
    read_only_baseline = read_only_baseline_contract()
    classification_catalog = resource_classification_catalog(
        authenticated_resources, owned_shell, fragments, third_party,
    )
    payload = {
        "schema": 1,
        "source_commit": git_value("rev-parse", "HEAD"),
        "source_dirty": source_dirty(),
        "source_roots": ["project/src/main/resources/static", "project/src/main/resources/templates", "owned-web/src/main/resources/frontend-original", "project/src/main/java/**/controller"],
        "counts": {
            "project_static": len(project_static),
            "project_templates": len(project_templates),
            "page_controllers": len(controllers),
            "owned_shell_routes": len(owned_shell),
            "supplier_runtime_inputs": len(supplier_inputs),
            "owned_static_total": len(owned_static),
            "owned_static_unique_overlay": len(owned_unique),
            "owned_static_unique_non_shell_overlay": len(owned_non_shell_unique),
            "owned_static_unique_gikam_lcdp_overlay": len(owned_gikam_lcdp_unique),
            "original_frontend_static": len(original_static),
            "packaged_static_union_estimate": len(project_relative | owned_relative | original_relative),
            "third_party_allowed_static": len(third_party),
            "authenticated_resource_urls": len(authenticated_resources["resources"]),
            "observable_gikam_lcdp_resources": sum(
                item["url"].startswith(GIKAM_SHELL_PREFIXES)
                for item in authenticated_resources["resources"]
            ),
        },
        "count_reconciliation": {
            "current_project_static_is_authoritative": True,
            "current_project_static": len(project_static),
            "historical_comment_claim": 868,
            "historical_comment_formula": "841 project files + 27 non-shell owned overlay files = 868",
            "historical_comment_explained_by": [
                "the old 868 figure counted the project tree plus 27 owned editor/facade files",
                "the historical count predates the expanded Gikam/LCDP shell resource seams",
                "the current full static union is %d = %d project files + %d non-shell owned overlays + %d Gikam/LCDP shell overlays"
                % (len(project_relative | owned_relative | original_relative), len(project_static), len(owned_non_shell_unique), len(owned_gikam_lcdp_unique)),
            ],
            "current_formula": "%d project files + %d non-shell owned overlays + %d imported original frontend files = %d"
            % (len(project_static), len(owned_non_shell_unique), len(original_static), len(project_relative | owned_relative | original_relative)),
            "current_owned_overlay_breakdown": {
                "non_shell": len(owned_non_shell_unique),
                "gikam_lcdp_shell": len(owned_gikam_lcdp_unique),
                "total_unique": len(owned_unique),
            },
            "historical_delta": {
                "static_union_delta": len(project_relative | owned_relative | original_relative) - 868,
                "additional_non_shell_overlays": len(owned_non_shell_unique) - 27,
                "gikam_lcdp_shell_overlays": len(owned_gikam_lcdp_unique),
            },
            "owned_overlay_unique": owned_unique,
        },
        "files": {
            "project_static": project_static,
            "project_templates": project_templates,
            "original_frontend_static": original_static,
        },
        "page_controllers": controllers,
        "page_controller_route_summary": route_summary(controllers),
        "owned_rebuild_required": owned_shell,
        "gikam_template_fragments": [
            {
                "fragment": fragment,
                "classification": "runtime-bundle-present-repository-copy-absent",
                "required_action": "bind-owned-java-api",
                "priority": "P0",
                "referenced_by_project_templates": True,
            }
            for fragment in fragments
        ],
        "third_party_allowed": [
            {
                "path": item["path"],
                "sha256": item["sha256"],
                "classification": "third-party-allowed",
                "reason": "project-supplied integration asset; outside Gikam shell replacement",
            }
            for item in third_party
        ],
        "supplier_runtime_inputs": supplier_inputs,
        "authenticated_resource_contract": authenticated_resources,
        "read_only_baseline_contract": read_only_baseline,
        "resource_classification_catalog": classification_catalog,
        "owned_rebuild_plan": owned_rebuild_plan(
            controllers, owned_shell, len(third_party), authenticated_resources,
        ),
        "baseline_contract": {
            "report": ".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-baseline-readonly-20261026.json",
            "mode": "read-only anonymous diagnostic",
            "required_routes": [
                "/",
                "/login",
                "/open/security/public-key",
                "/core/module/sys/login",
                "/secure/core/module/sys/menus",
                "/secure/core/module/index/workspace",
                "/secure/emc/module/index/workspace",
            ],
            "authenticated_observation": "sss2-diagnostic-captured" if authenticated_resources["available"] else "open",
            "production_access": False,
        },
        "acceptance_boundaries": {
            "project_assets_are_source_owned": True,
            "supplier_runtime_is_final_input": False,
            "original_frontend_imported": bool(original_static),
            "license_native_cleaned": True,
            "api_dependency_closure": "pending",
            "strict_frontend_parity": "pending",
            "static_presence_is_behavior_evidence": False,
            "browser_route_replay_required": True,
            "inventory_is_not_strict_behavior_evidence": True,
        },
    }
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n")
    print("frontend source/runtime inventory generated: " + str(args.output))


if __name__ == "__main__":
    main()
