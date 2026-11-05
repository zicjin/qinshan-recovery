#!/usr/bin/env python3
"""Build the current-head Spring Web API closure ledger.

The separate ``boundary_routes`` collection is a source-accounting view of
owned Java source-only routes. It does not describe a process or deployment
boundary: those routes run in the same Spring application as project routes.
"""
from __future__ import annotations

import argparse
import collections
import json
import re
import subprocess
import sys
from pathlib import Path
from typing import Dict, List

SCRIPT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPT_ROOT / "scripts"))
from evidence_output import guard_output_path  # noqa: E402
from source_binding import REPO_ROOT, capture_binding, is_standalone_root  # noqa: E402
ROOT = REPO_ROOT


def git_head() -> str:
    return subprocess.check_output(["git", "-C", str(ROOT), "rev-parse", "HEAD"], text=True).strip()


FAMILY_ORDER = (
    "core",
    "personnel",
    "equipment",
    "consumables-materials",
    "result-order-project",
    "attachment-report",
    "task-integration",
    "page-other",
)


def _route_text(route: Dict[str, object]) -> str:
    return " ".join(str(route.get(key, "")) for key in ("source", "class", "path", "annotation")).lower()


def _has_word(text: str, *words: str) -> bool:
    return any(re.search(r"(?<![a-z0-9])" + re.escape(word.lower()) + r"(?![a-z0-9])", text) for word in words)


def classify_family(route: Dict[str, object]) -> Dict[str, str]:
    """Assign a deterministic implementation family, without claiming behavior.

    The recovered project has many generic inherited mappings.  Family is a
    planning/index field only; it must never promote ``behavior_status``.
    Ordered rules keep the native-adjacent business families ahead of the
    broad task/integration and page fallbacks.
    """
    text = _route_text(route)
    path = str(route.get("path", "")).lower()
    source_class = " ".join(str(route.get(key, "")) for key in ("source", "class")).lower()
    if any(token in text for token in (
        "license", "sunway-app", "native", "macbinding", "mac_binding",
        "/open/security", "/core/", "/module/index", "workspace", "gikam",
        "public-key",
    )) or _has_word(text, "security", "identity", "permission", "menu", "role", "org", "login", "logout", "session", "rsa"):
        return {"family": "core", "family_reason": "framework/identity/security/workspace marker"}

    # Workflow/task routes are checked before broad BP module matching.  This
    # keeps order-task and scheduler endpoints out of the result/order bucket.
    if any(token in path for token in (
        "task", "workflow", "scheduler", "bpmn", "process", "/erp/", "/oa-",
        "/oa/", "/integration/", "/mobile/",
    )) or _has_word(source_class, "scheduler", "workflow", "bpmn", "erp", "ftp", "email", "integration", "mobile"):
        return {"family": "task-integration", "family_reason": "task/workflow/integration marker"}

    # Personnel resources may expose report/sample chooser paths. Keep those
    # routes in the personnel wave before the broad attachment/report marker;
    # their controller and service seam is still personnel-owned behavior.
    if any(token in path for token in (
        "/module/mdm/emp", "/module/item/staff", "personnel", "employee", "staff",
        "qualification", "training", "ability",
    )) or _has_word(source_class, "personnel", "employee", "staff", "qualification", "training", "ability"):
        return {"family": "personnel", "family_reason": "personnel/staff marker"}

    if any(token in path for token in (
        "attachment", "/file", "-file", "/report", "-report", "export", "import",
        "pageoffice", "spreadsheet", "renderer",
    )) or _has_word(source_class, "attachment", "file", "report", "renderer", "pageoffice", "spreadsheet"):
        return {"family": "attachment-report", "family_reason": "attachment/report/file marker"}

    if any(token in path for token in (
        "/module/mdm/equipt", "/module/bp/equipt", "equipment", "equipt", "equipcheck",
        "calibr", "repair", "patrol", "maintenance", "maintain",
    )) or _has_word(source_class, "equipment", "equipt", "equipcheck", "calibration", "repair", "patrol", "maintenance"):
        return {"family": "equipment", "family_reason": "equipment/maintenance marker"}

    if any(token in path for token in (
        "/module/item/consume", "/module/mdm/basemdm/mtl", "consum", "material",
        "reagent", "stock", "concent", "quality-control", "quality-controls", "mtl-",
    )) or _has_word(source_class, "consumable", "material", "reagent", "stock", "concentration", "quality"):
        return {"family": "consumables-materials", "family_reason": "consumable/material marker"}

    if any(token in path for token in (
        "/module/bp/result", "/module/bp/order", "/module/bp/project",
        "/module/bp/sample", "/module/bp/bp", "result", "order", "project",
        "sample", "folder",
    )) or _has_word(source_class, "result", "order", "project", "sample", "folder"):
        return {"family": "result-order-project", "family_reason": "result/order/project marker"}
    if "/module/bp/" in path:
        return {"family": "result-order-project", "family_reason": "business-process module fallback"}
    return {"family": "page-other", "family_reason": "no higher-priority family marker"}


def classify_route_kind(route: Dict[str, object]) -> Dict[str, str]:
    text = _route_text(route)
    source = str(route.get("source", "")).lower()
    annotation = str(route.get("annotation", ""))
    # Inherited GenericResource paths often contain ``gikam-attrs``.  The
    # annotation/source relationship is authoritative; a path token must not
    # reclassify those routes as workspace pages and hide them from API
    # differential screening.
    if annotation.startswith("inherited."):
        return {"route_kind": "inherited-api"}
    if "workspace" in text or "gikam" in text or "/module/index" in text:
        return {"route_kind": "workspace"}
    if "/resource/" in source or re.search(r"resource", str(route.get("class", "")), re.I):
        return {"route_kind": "api-resource"}
    if "/controller/" in source or re.search(r"controller", str(route.get("class", "")), re.I):
        return {"route_kind": "web-controller"}
    return {"route_kind": "web-route"}


def classify_disposition(route: Dict[str, object]) -> Dict[str, str]:
    return {
        "disposition": "unknown",
        "disposition_reason": "no clone-derived HTTP evidence registered",
    }


def classify(route: Dict[str, object]) -> Dict[str, str]:
    text = _route_text(route)
    if any(token in text for token in ("license", "sunway-app", "native", "macbinding", "mac_binding")):
        return {"native_relevance": "P0", "native_relevance_reason": "explicit native/license/MAC marker"}
    if any(token in text for token in ("workspace", "/module/index/", "gikam")):
        return {"native_relevance": "P0", "native_relevance_reason": "workspace/Gikam shell route"}
    if any(token in text for token in (
        "security", "identity", "permission", "menu", "role", "org", "/open/security", "/core/",
        "/module/mdm/emp", "/module/mdm/equipt", "/module/item/consume", "/module/mdm/basemdm/mtl",
    )):
        return {"native_relevance": "P1", "native_relevance_reason": "identity/security or native-adjacent business route"}
    return {"native_relevance": "P2", "native_relevance_reason": "no direct native-adjacent marker"}


def wave(route: Dict[str, object]) -> str:
    text = " ".join(str(route.get(key, "")) for key in ("source", "class", "path")).lower()
    if any(token in text for token in ("security", "identity", "permission", "menu", "role", "org", "/open/security", "/core/", "module/index")):
        return "core"
    if any(token in text for token in ("module/mdm/emp", "module/mdm/equipt", "module/item/consume", "module/mdm/basemdm/mtl")):
        return "business"
    return "secondary"


def build(inventory: Dict[str, object], native_registry: Dict[str, object] | None = None,
          inventory_source: str | None = None) -> Dict[str, object]:
    # Native registry/history is browser-priority metadata.  It must not be a
    # required input for the API ledger or influence route disposition.
    native_registry = native_registry or {}
    def enrich(source_route: Dict[str, object], *, boundary: bool = False) -> Dict[str, object]:
        route = dict(source_route)
        route.update(classify(route))
        route.update(classify_family(route))
        route.update(classify_route_kind(route))
        route.update(classify_disposition(route))
        route["wave"] = wave(route)
        route["database_objects"] = []
        route["database_objects_known"] = False
        route["roles"] = "unknown"
        route["organizations"] = "unknown"
        route["mode"] = "unknown"
        route["fixture"] = "unknown"
        route["expected_result"] = "unknown"
        route["audit"] = "unknown"
        route["cleanup"] = "unknown"
        route["runner"] = "unknown"
        route["evidence"] = "unknown"
        route["unknown_reason"] = "no clone-derived HTTP evidence registered"
        # Boundary is an ownership classification only.  It is served by an
        # owned Java Controller/Resource in the same Spring application.
        route["route_origin"] = "owned-java-source-only" if boundary else "project-spring-source"
        route["boundary_semantics"] = (
            "owned Java source-only route; boundary route != boundary process"
            if boundary else "project Spring route"
        )
        route["runtime_model"] = "same-spring-application"
        return route

    routes: List[Dict[str, object]] = [enrich(source_route) for source_route in inventory.get("routes", [])]
    boundary_routes: List[Dict[str, object]] = [
        enrich(source_route, boundary=True) for source_route in inventory.get("boundary_routes", [])
    ]
    priority = collections.Counter(route["native_relevance"] for route in routes)
    waves = collections.Counter(route["wave"] for route in routes)
    return {
        "schema": 2,
        "scope": "current-head-spring-web-api-route-ledger",
        "source_commit": inventory.get("source_commit"),
        "source_dirty": inventory.get("source_dirty"),
        "repository_head_at_capture": inventory.get("repository_head_at_capture", inventory.get("source_commit")),
        "implementation_binding": inventory.get("implementation_binding"),
        "implementation_tree_digest": inventory.get("implementation_tree_digest"),
        "implementation_dirty": inventory.get("implementation_dirty", inventory.get("source_dirty")),
        "implementation_scope": inventory.get("implementation_scope", []),
        "inventory_source": inventory_source or ".scratch/sunway-supplier-exit-api-first/archive/historical/business-api-inventory-current-head--14a87746122d.json",
        "planning_metadata": {
            "native_registry": {
                "path": ".scratch/sunway-supplier-exit-api-first/archive/historical/native-api-registry.json" if native_registry else None,
                "role": "browser-priority-only",
                "registry_items": native_registry.get("counts", {}).get("registry_items"),
            },
        },
        "policy": "route/source presence is not behavior evidence; behavior remains unknown until clone-derived HTTP evidence is bound",
        "route_execution_model": {
            "application": "one owned Spring application",
            "oracle": "one clone-derived Oracle per API wave",
            "redis": "one Redis per API wave",
            "boundary_route": "owned Java source-only route served in the same Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "classification_policy": {
            "P0": "explicit native/license/MAC marker or workspace/Gikam shell route",
            "P1": "identity/security/permission or native-adjacent personnel/equipment/consumables route",
            "P2": "no direct native-adjacent marker; API evidence remains required",
        },
        "family_policy": {
            "core": "framework, identity, security, permission, organization, menu, query conversion and workspace",
            "personnel": "employee, staff, qualification and training routes",
            "equipment": "equipment, maintenance, calibration, repair and inspection routes",
            "consumables-materials": "consumables, materials, reagents, stock, concentration and QC routes",
            "result-order-project": "result, sample, order, project and folder routes",
            "attachment-report": "attachment, file, report, import/export and renderer routes",
            "task-integration": "task, workflow, scheduler and external integration routes",
            "page-other": "routes without a higher-priority family marker",
        },
        "counts": {
            "route_count": len(routes),
            "project_route_count": len(routes),
            "total_route_count": len(routes) + len(boundary_routes),
            "behavior_unknown": sum(route["behavior_status"] == "unknown" for route in routes),
            "native_relevance": dict(sorted(priority.items())),
            "wave": dict(sorted(waves.items())),
            "owned_class_present": inventory.get("counts", {}).get("owned_class_present", 0),
            "owned_route_source_match": inventory.get("counts", {}).get("owned_route_source_match", 0),
            "family": dict(sorted(collections.Counter(route["family"] for route in routes).items())),
            "route_kind": dict(sorted(collections.Counter(route["route_kind"] for route in routes).items())),
            "disposition_unknown": sum(route["disposition"] == "unknown" for route in routes),
            "boundary_route_count": len(boundary_routes),
            "boundary_behavior_unknown": sum(route["behavior_status"] == "unknown" for route in boundary_routes),
            "boundary_family": dict(sorted(collections.Counter(route["family"] for route in boundary_routes).items())),
        },
        "routes": routes,
        "boundary_routes": boundary_routes,
    }


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--inventory", type=Path, required=True)
    parser.add_argument("--native-registry", type=Path,
                        help="optional browser-priority metadata; never used for API route closure")
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    inventory = json.loads(args.inventory.read_text(encoding="utf-8"))
    registry = json.loads(args.native_registry.read_text(encoding="utf-8")) if args.native_registry else None
    binding = capture_binding(ROOT)
    if inventory.get("implementation_tree_digest"):
        if inventory.get("implementation_tree_digest") != binding["implementation_tree_digest"]:
            raise SystemExit("route ledger: inventory implementation binding is stale")
        if inventory.get("implementation_dirty") is not False or binding["implementation_dirty"]:
            raise SystemExit("route ledger: implementation scope is dirty")
    elif inventory.get("source_commit") != git_head() or inventory.get("source_dirty") is not False:
        raise SystemExit("route ledger: legacy inventory source binding is stale or dirty")
    try:
        inventory_root = ROOT if is_standalone_root(ROOT) else ROOT / "lims-recovery"
        inventory_source = str(args.inventory.relative_to(inventory_root))
    except ValueError:
        inventory_source = str(args.inventory)
    ledger = build(inventory, registry, inventory_source=inventory_source)
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(ledger, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("API route ledger: PASS", ledger["counts"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
