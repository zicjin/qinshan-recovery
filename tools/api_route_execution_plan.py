#!/usr/bin/env python3
"""Create deterministic API execution groups from the route ledger.

``boundary_routes`` are owned Java source-only routes, not a second runtime.
They are grouped into the same family wave and share that wave's Spring
application, Oracle and Redis lifecycle.
"""
from __future__ import annotations

import argparse
import collections
import hashlib
import json
import sys
from pathlib import Path
from typing import Any, Dict, List, Mapping

SCRIPT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(SCRIPT_ROOT / "scripts"))
from evidence_output import guard_output_path  # noqa: E402
from source_binding import REPO_ROOT, binding_metadata  # noqa: E402
ROOT = REPO_ROOT

DEFAULT_GROUP_SIZE = 100
WAVE_ORDER = (
    "foundation",
    "personnel",
    "equipment",
    "consumables-materials",
    "secondary",
)


def wave_for_family(family: str) -> str:
    """Map ledger families to the five continuous API waves.

    Groups remain useful for affected-runner selection and bounded case
    manifests, but they must not imply a separate Oracle/Redis/application
    lifecycle.  The lifecycle boundary is the wave.
    """
    if family == "core":
        return "foundation"
    if family in {"personnel", "equipment", "consumables-materials"}:
        return family
    return "secondary"


def source_scope(route: Mapping[str, Any]) -> str:
    source = str(route.get("source", ""))
    parts = source.split("/")
    return "/".join(parts[:2]) if len(parts) >= 2 else source


def build_plan(ledger: Mapping[str, Any], group_size: int = DEFAULT_GROUP_SIZE) -> Dict[str, Any]:
    if group_size < 1:
        raise ValueError("group_size must be positive")
    routes = ledger.get("routes")
    boundary_routes = ledger.get("boundary_routes", [])
    if not isinstance(routes, list) or not isinstance(boundary_routes, list):
        raise ValueError("ledger routes and boundary_routes must be lists")
    all_routes = routes + boundary_routes
    ordered = sorted(all_routes, key=lambda row: (str(row.get("family")), str(row.get("route_kind")), str(row.get("id"))))
    groups: List[Dict[str, Any]] = []
    assignment: Dict[str, str] = {}
    buckets: Dict[tuple[str, str], List[Mapping[str, Any]]] = collections.defaultdict(list)
    for route in ordered:
        buckets[(str(route.get("family", "unknown")), str(route.get("route_kind", "unknown")))].append(route)
    for (family, kind), bucket in sorted(buckets.items()):
        wave = wave_for_family(family)
        wave_runtime_id = "api-wave-" + wave
        for offset in range(0, len(bucket), group_size):
            chunk = bucket[offset:offset + group_size]
            index = offset // group_size + 1
            group_id = "%s-%s-%03d" % (family, kind, index)
            case_ids = [str(route.get("id")) for route in chunk]
            for case_id in case_ids:
                if case_id in assignment:
                    raise ValueError("route assigned to multiple groups: " + case_id)
                assignment[case_id] = group_id
            groups.append({
                "group_id": group_id,
                "family": family,
                "route_kind": kind,
                "wave": wave,
                "wave_runtime_id": wave_runtime_id,
                "case_count": len(chunk),
                "case_ids": case_ids,
                "source_scopes": sorted({source_scope(route) for route in chunk}),
                "runtime_policy": "one owned Spring application + one clone-derived Oracle + one Redis per wave; project and owned Java source-only routes share it",
                "boundary_process": False,
                "internal_http_bridge": False,
                "parallel_policy": "requires isolated checkout,target,ports,containers,network and temporary root",
                "behavior_status": "unknown",
                "family_fingerprints": dict(sorted({
                    str(route.get("behavior_family_id", "")): str(route.get("family_fingerprint", ""))
                    for route in chunk
                    if route.get("family_fingerprint")
                }.items())),
            })
    route_count = len(all_routes)
    if len(assignment) != route_count:
        raise ValueError("execution plan does not cover every route")
    plan_id = hashlib.sha256("\n".join(f"{key}:{value}" for key, value in sorted(assignment.items())).encode()).hexdigest()[:16]
    wave_counts = {
        wave: sum(group["case_count"] for group in groups if group["wave"] == wave)
        for wave in WAVE_ORDER
    }
    return {
        "schema": 1,
        "scope": "current-head-spring-web-api-execution-plan",
        "source_commit": ledger.get("source_commit"),
        "source_dirty": ledger.get("source_dirty"),
        **binding_metadata(ledger),
        "implementation_dirty": ledger.get("implementation_dirty", ledger.get("source_dirty")),
        "route_count": route_count,
        "project_route_count": len(routes),
        "boundary_route_count": len(boundary_routes),
        "group_size": group_size,
        "group_count": len(groups),
        "wave_order": list(WAVE_ORDER),
        "wave_counts": wave_counts,
        "runtime_count": len([wave for wave in WAVE_ORDER if wave_counts[wave] > 0]),
        "plan_id": plan_id,
        "family_fingerprints": dict(sorted({
            str(route.get("behavior_family_id", "")): str(route.get("family_fingerprint", ""))
            for route in all_routes
            if route.get("family_fingerprint")
        }.items())),
        "groups": groups,
        "assignment": assignment,
        "policy": "planning only; no route behavior is promoted by group assignment",
        "route_execution_model": {
            "application": "one owned Spring application per wave",
            "oracle": "one clone-derived Oracle per wave",
            "redis": "one Redis per wave",
            "boundary_route": "owned Java source-only route in the same Spring application",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
    }


def main(argv: List[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--group-size", type=int, default=DEFAULT_GROUP_SIZE)
    args = parser.parse_args(argv)
    plan = build_plan(json.loads(args.ledger.read_text(encoding="utf-8")), args.group_size)
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(plan, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("API execution plan:", {key: plan[key] for key in ("route_count", "group_count", "group_size", "plan_id")})
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
