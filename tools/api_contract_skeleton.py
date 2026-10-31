#!/usr/bin/env python3
"""Generate route-complete API contract skeletons and continuous waves.

The output is planning material.  It deliberately leaves behaviour, fixture,
role and audit fields unknown until a real HTTP contract is written and
replayed.  A wave may share one strict runtime, while every route keeps its
own case skeleton and execution-group assignment. Source-only/boundary cases
remain in the same Spring application wave; they never imply a separate
compatibility process.
"""
from __future__ import annotations

import argparse
import collections
import json
from pathlib import Path
from typing import Any, Dict, Iterable, List, Mapping


WAVE_ORDER = (
    "foundation",
    "personnel",
    "equipment",
    "consumables-materials",
    "secondary",
)


def wave_for_family(family: str) -> str:
    if family == "core":
        return "foundation"
    if family in {"personnel", "equipment", "consumables-materials"}:
        return family
    return "secondary"


def _case(route: Mapping[str, Any], group_id: str | None, wave: str) -> Dict[str, Any]:
    wave_runtime_id = "api-wave-" + wave
    return {
        "id": str(route.get("id", "")),
        "route_id": str(route.get("id", "")),
        "method": route.get("method"),
        "path": route.get("path"),
        "source": route.get("source"),
        "class": route.get("class"),
        "family": route.get("family"),
        "route_kind": route.get("route_kind"),
        "native_relevance": route.get("native_relevance"),
        "route_origin": route.get("route_origin", "project-spring-source"),
        "boundary_semantics": route.get("boundary_semantics", "project Spring route"),
        "runtime_model": route.get("runtime_model", "same-spring-application"),
        "wave": wave,
        "wave_runtime_id": wave_runtime_id,
        "execution_group": group_id,
        "operation": "unknown",
        "body": {},
        "headers": {},
        "expected_status": [],
        "assert": {},
        "fixture": {
            "mode": "unknown",
            "setup_requests": [],
            "restore_requests": [],
            "business_write_via": "http",
        },
        "role": "unknown",
        "organization": "unknown",
        "mode": "strict",
        "database_objects": route.get("database_objects", []),
        "audit": {"required": "unknown", "status": "unknown"},
        "rollback": "unknown",
        "cleanup": "unknown",
        "disposition": "unknown",
        "evidence": None,
        "unknown_reason": "contract skeleton generated; no HTTP disposition registered",
    }


def build(ledger: Mapping[str, Any], plan: Mapping[str, Any]) -> Dict[str, Any]:
    routes = list(ledger.get("routes", [])) + list(ledger.get("boundary_routes", []))
    assignment = plan.get("assignment", {})
    if not isinstance(assignment, Mapping):
        raise ValueError("execution plan assignment must be an object")
    waves: Dict[str, List[Dict[str, Any]]] = collections.defaultdict(list)
    for route in routes:
        route_id = str(route.get("id", ""))
        group_id = assignment.get(route_id)
        if not group_id:
            raise ValueError("route missing execution group: " + route_id)
        wave = wave_for_family(str(route.get("family", "")))
        waves[wave].append(_case(route, str(group_id), wave))

    output_waves: List[Dict[str, Any]] = []
    for wave in WAVE_ORDER:
        cases = sorted(waves.get(wave, []), key=lambda item: (str(item.get("family")), str(item.get("id"))))
        group_counts = collections.Counter(str(item["execution_group"]) for item in cases)
        output_waves.append({
            "wave": wave,
            "runtime_id": "api-wave-" + wave,
            "families": sorted({str(item.get("family")) for item in cases}),
            "route_count": len(cases),
            "execution_group_count": len(group_counts),
            "runtime_policy": "one owned Spring application + one clone-derived Oracle + one Redis lifecycle for the wave; project and owned Java source-only routes share it; groups are case batches only",
            "boundary_process": False,
            "internal_http_bridge": False,
            "parallel_policy": "waves are sequential; parallel groups require isolated checkout, target, ports, containers, network and temporary root",
            "strict_ready": False,
            "cases": cases,
        })
    return {
        "schema": 1,
        "scope": "current-head-api-contract-skeleton-and-continuous-waves",
        "source_commit": ledger.get("source_commit"),
        "source_dirty": ledger.get("source_dirty"),
        "repository_head_at_capture": ledger.get("repository_head_at_capture", ledger.get("source_commit")),
        "implementation_binding": ledger.get("implementation_binding"),
        "implementation_tree_digest": ledger.get("implementation_tree_digest"),
        "implementation_dirty": ledger.get("implementation_dirty", ledger.get("source_dirty")),
        "implementation_scope": ledger.get("implementation_scope", []),
        "route_count": len(routes),
        "project_route_count": len(ledger.get("routes", [])),
        "boundary_route_count": len(ledger.get("boundary_routes", [])),
        "wave_order": list(WAVE_ORDER),
        "policy": "planning-only; skeleton fields do not promote route behaviour",
        "route_execution_model": {
            "application": "one owned Spring application per wave",
            "oracle": "one clone-derived Oracle per wave",
            "redis": "one Redis per wave",
            "boundary_route": "owned Java source-only route in the same Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "waves": output_waves,
    }


def main(argv: Iterable[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--plan", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args(argv)
    ledger = json.loads(args.ledger.read_text(encoding="utf-8"))
    plan = json.loads(args.plan.read_text(encoding="utf-8"))
    if ledger.get("implementation_tree_digest") and plan.get("implementation_tree_digest"):
        if ledger.get("implementation_tree_digest") != plan.get("implementation_tree_digest"):
            raise SystemExit("contract skeleton: ledger and execution plan implementation bindings differ")
        if ledger.get("implementation_binding") != plan.get("implementation_binding"):
            raise SystemExit("contract skeleton: ledger and execution plan implementation_binding differs")
        if ledger.get("implementation_dirty") is not False or plan.get("implementation_dirty") is not False:
            raise SystemExit("contract skeleton: implementation scope must be clean")
    elif ledger.get("source_commit") != plan.get("source_commit"):
        raise SystemExit("contract skeleton: legacy ledger and execution plan source commits differ")
    skeleton = build(ledger, plan)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(skeleton, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("API contract skeleton:", {
        "source_commit": skeleton["source_commit"],
        "route_count": skeleton["route_count"],
        "waves": {wave["wave"]: wave["route_count"] for wave in skeleton["waves"]},
    })
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
