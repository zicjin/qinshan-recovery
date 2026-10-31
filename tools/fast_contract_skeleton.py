#!/usr/bin/env python3
"""Generate planning-only fast differential cases and wave jobs from a ledger."""
from __future__ import annotations

import argparse
import json
from pathlib import Path
import sys
from typing import Any, Dict, List, Sequence

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path, current_implementation_binding  # noqa: E402
from fast_differential_api_runner import request_profiles, route_safe  # noqa: E402
from fast_differential_api_runner import behavior_family_key  # noqa: E402


def wave_for_family(family: str) -> str:
    if family == "core":
        return "foundation"
    if family in {"personnel", "equipment", "consumables-materials"}:
        return family
    return "secondary"


def validate_current_head(ledger: Dict[str, Any]) -> None:
    current = current_implementation_binding()
    if ledger.get("implementation_tree_digest"):
        if ledger.get("implementation_tree_digest") != current["implementation_tree_digest"]:
            raise ValueError("ledger implementation binding must match current implementation")
        if ledger.get("implementation_binding") != current["binding_id"]:
            raise ValueError("ledger implementation_binding must match current implementation")
        if ledger.get("implementation_dirty") is not False or current["implementation_dirty"]:
            raise ValueError("ledger implementation scope must be clean")
    else:
        raise ValueError("ledger lacks implementation binding; regenerate from current source binding")
    model = ledger.get("route_execution_model")
    if not isinstance(model, dict):
        raise ValueError("ledger route_execution_model is missing")
    if model.get("boundary_process") is not False or model.get("internal_http_bridge") is not False:
        raise ValueError("ledger declares a boundary process or internal HTTP bridge")


def build(ledger: Dict[str, Any], *, families: set[str] | None,
          route_kinds: set[str], limit: int, role: str, organization: str,
          plan: Dict[str, Any] | None = None,
          profiles: Sequence[str] = ("default", "page-2", "empty", "malformed")) -> Dict[str, Any]:
    rows = list(ledger.get("routes", [])) + list(ledger.get("boundary_routes", []))
    cases: List[Dict[str, Any]] = []
    probes: Dict[str, Dict[str, Any]] = {}
    skipped: Dict[str, int] = {}
    for route in rows:
        if families and str(route.get("family")) not in families:
            continue
        if str(route.get("route_kind")) not in route_kinds:
            continue
        safe, reason = route_safe(route)
        if not safe:
            skipped[reason] = skipped.get(reason, 0) + 1
            continue
        route_profiles = request_profiles(route, profiles)
        if not route_profiles:
            skipped["no-request-profile"] = skipped.get("no-request-profile", 0) + 1
            continue
        for profile in route_profiles:
            wave = wave_for_family(str(route.get("family", "")))
            probe_key = behavior_family_key(route, profile)
            # Once the requested unique-probe limit is reached, retain aliases
            # for already selected probes but do not create new HTTP jobs.
            if probe_key not in probes and limit and len(probes) >= limit:
                continue
            item = probes.setdefault(probe_key, {
                "id": "probe-" + probe_key, "probe_key": probe_key,
                "covered_route_ids": [], "covered_route_count": 0,
                "aliases": [], "source_classes": [],
                "method": route.get("method"), "path": route.get("path"),
                "source_class": route.get("class") or route.get("source_class") or route.get("class_base"),
                "source_handler": route.get("handler") or route.get("source_method") or route.get("annotation"),
                "family": route.get("family"), "route_kind": route.get("route_kind"),
                "route_origin": route.get("route_origin", "project-spring-source"),
                "role": role, "organization": organization, "wave": wave,
                "wave_runtime_id": "api-wave-" + wave, "request_profile": profile,
                "disposition": "unknown", "unknown_reason": "fast differential pending",
            })
            route_id = str(route.get("id"))
            if route_id not in item["covered_route_ids"]:
                item["covered_route_ids"].append(route_id)
                item["covered_route_count"] += 1
            item["aliases"].append({"method": route.get("method"), "path": route.get("path"),
                                    "route_kind": route.get("route_kind"),
                                    "source_class": route.get("class") or route.get("source_class") or route.get("class_base"),
                                    "source_handler": route.get("handler") or route.get("source_method") or route.get("annotation")})
            if route.get("class") not in item["source_classes"]:
                item["source_classes"].append(route.get("class"))
    cases = list(probes.values())
    covered_route_ids = {route_id for item in cases for route_id in item["covered_route_ids"]}
    for item in cases:
        item["execution_groups"] = []
        item["execution_group"] = None
        if plan:
            assignment = plan.get("assignment", {})
            group_ids = sorted({assignment.get(route_id) for route_id in item["covered_route_ids"] if assignment.get(route_id)})
            item["execution_groups"] = group_ids
            item["execution_group"] = group_ids[0] if len(group_ids) == 1 else "multiple"
            runtimes = []
            for group in plan.get("groups", []):
                if group.get("group_id") in group_ids:
                    runtime_id = group.get("wave_runtime_id", item["wave_runtime_id"])
                    if runtime_id not in runtimes:
                        runtimes.append(runtime_id)
            item["wave_runtime_id"] = runtimes[0] if len(runtimes) == 1 else ("multiple" if runtimes else item["wave_runtime_id"])
    jobs = []
    for wave in ("foundation", "personnel", "equipment", "consumables-materials", "secondary"):
        wave_cases = [case for case in cases if case["wave"] == wave]
        ids = [case["id"] for case in wave_cases]
        if ids:
            families_for_wave = sorted({str(case.get("family", "")) for case in wave_cases if case.get("family")})
            jobs.append({"wave": wave, "wave_runtime_id": "api-wave-" + wave,
                         "job_kind": "fast-differential-diagnostic",
                         "strict_evidence": False,
                         "case_ids": ids,
                         "probe_ids": ids,
                         "families": families_for_wave,
                         "route_kind": sorted(route_kinds),
                         "profiles": list(profiles),
                         "runner": "lims-recovery/scripts/fast_differential_api_runner.py",
                         "command_template": ["--family", ",".join(families_for_wave),
                                               "--route-kind", ",".join(sorted(route_kinds)),
                                               "--profiles", ",".join(profiles)],
                         "runtime_policy": "one shared Spring application/Oracle/Redis"})
    return {
        "schema": 1, "scope": "fast-differential-contract-skeleton", "planning_only": True,
        "source_commit": ledger.get("source_commit"), "source_dirty": ledger.get("source_dirty"),
        "repository_head_at_capture": ledger.get("repository_head_at_capture", ledger.get("source_commit")),
        "implementation_binding": ledger.get("implementation_binding"),
        "implementation_tree_digest": ledger.get("implementation_tree_digest"),
        "implementation_dirty": ledger.get("implementation_dirty"),
        "implementation_scope": ledger.get("implementation_scope", []),
        "inventory_route_count": len(rows), "route_count": len(covered_route_ids),
        "unique_probe_count": len(cases),
        "profile_probe_count": len(cases),
        "covered_alias_count": len(covered_route_ids),
        "covered_profile_route_count": sum(int(item["covered_route_count"]) for item in cases),
        "role": role, "organization": organization,
        "route_kind": sorted(route_kinds), "family": sorted(families) if families else None,
        "skipped": skipped, "cases": cases, "jobs": jobs,
        "strict_evidence": False,
        "policy": "fast-equivalent is diagnostic and never promotes strict pass",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--plan", type=Path, help="current-HEAD execution plan for group/wave binding")
    parser.add_argument("--family", default="")
    parser.add_argument("--route-kind", default="inherited-api,api-resource")
    parser.add_argument("--limit", type=int, default=100)
    parser.add_argument("--profiles", default="default,page-2,empty,malformed",
                        help="comma-separated read profiles")
    parser.add_argument("--role", default="manager")
    parser.add_argument("--organization", default="org-a")
    args = parser.parse_args()
    ledger = json.loads(args.ledger.read_text())
    validate_current_head(ledger)
    plan = json.loads(args.plan.read_text()) if args.plan else None
    if plan is not None:
        if plan.get("implementation_tree_digest") != ledger.get("implementation_tree_digest") or plan.get("implementation_binding") != ledger.get("implementation_binding"):
            raise ValueError("execution plan implementation binding does not match ledger")
        if plan.get("implementation_dirty") is not False:
            raise ValueError("execution plan implementation scope must be clean")
        ids = {str(r.get("id")) for r in list(ledger.get("routes", [])) + list(ledger.get("boundary_routes", []))}
        if set(plan.get("assignment", {})) != ids:
            raise ValueError("execution plan assignment does not cover ledger")
    result = build(ledger,
                   families=set(args.family.split(",")) if args.family else None,
                   route_kinds=set(args.route_kind.split(",")), limit=args.limit,
                   role=args.role, organization=args.organization, plan=plan,
                   profiles=tuple(args.profiles.split(",")))
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n")
    print("fast contract skeleton:", {"route_count": result["route_count"], "jobs": len(result["jobs"]), "skipped": result["skipped"]})
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
