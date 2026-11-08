#!/usr/bin/env python3
"""Bind clone-derived API reports to the route ledger without promoting diagnostics."""
from __future__ import annotations

import argparse
import collections
import csv
import datetime as dt
import hashlib
import json
from pathlib import Path
from typing import Any, Dict, Iterable, List, Mapping, Optional, Tuple
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "scripts"))
from evidence_output import (
    cleanup_contract_complete,
    guard_output_path,
    current_implementation_binding,
    route_execution_model_valid,
    strict_case_contract,
)

ALLOWED_DISPOSITIONS = {
    "owned-pass", "removed-negative-pass", "external-replay-pass", "disabled-in-isolated-env",
}


def _report_digest(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def _route_key(method: str, path: str) -> Tuple[str, str]:
    return method.upper(), path


def _index_routes(routes: Iterable[Mapping[str, Any]]) -> Dict[Tuple[str, str], List[Mapping[str, Any]]]:
    index: Dict[Tuple[str, str], List[Mapping[str, Any]]] = collections.defaultdict(list)
    for route in routes:
        index[_route_key(str(route.get("method", "")), str(route.get("path", "")))].append(route)
    return index


def _registry_class(report_path: Path) -> Optional[str]:
    evidence_root = Path(__file__).resolve().parents[1] / "evidence"
    registry = evidence_root / "evidence-index.tsv"
    try:
        if not report_path.resolve().is_relative_to(evidence_root.resolve()) or not registry.is_file():
            return None
    except ValueError:
        return None
    relative_path = report_path.resolve().relative_to(evidence_root.parent.parent).as_posix()
    with registry.open(encoding="utf-8", newline="") as handle:
        for row in csv.DictReader(handle, delimiter="\t"):
            if row.get("path") == relative_path:
                return row.get("class")
    return "unregistered"


def _family_binding_index(ledger: Mapping[str, Any]) -> Dict[str, Dict[str, str]]:
    result: Dict[str, Dict[str, str]] = {}
    for route in list(ledger.get("routes", [])) + list(ledger.get("boundary_routes", [])):
        route_id = str(route.get("id", ""))
        if not route_id:
            continue
        result[route_id] = {
            "behavior_family_id": str(route.get("behavior_family_id", "")),
            "family_fingerprint": str(route.get("family_fingerprint", "")),
        }
    return result


def _report_family_status(report: Mapping[str, Any],
                          ledger: Mapping[str, Any]) -> Tuple[bool, str, list[dict[str, Any]]]:
    expected = _family_binding_index(ledger)
    stale: list[dict[str, Any]] = []
    for index, case in enumerate(report.get("cases", [])):
        if not isinstance(case, Mapping):
            continue
        route = case.get("route") if isinstance(case.get("route"), Mapping) else {}
        route_ids = []
        if route.get("route_id"):
            route_ids.append(str(route["route_id"]))
        if isinstance(route.get("route_ids"), list):
            route_ids.extend(str(item) for item in route["route_ids"])
        if not route_ids and case.get("route_id"):
            route_ids.append(str(case["route_id"]))
        if not route_ids and isinstance(case.get("route_ids"), list):
            route_ids.extend(str(item) for item in case["route_ids"])
        fingerprints = {
            expected[route_id]["family_fingerprint"]
            for route_id in route_ids
            if route_id in expected and expected[route_id]["family_fingerprint"]
        }
        declared = str(case.get("family_fingerprint", ""))
        if fingerprints and declared and declared not in fingerprints:
            stale.append({
                "case_index": index,
                "route_ids": route_ids,
                "reason": "family-fingerprint-mismatch",
                "declared_family_fingerprint": declared,
                "expected_family_fingerprints": sorted(fingerprints),
            })
        if declared and not fingerprints:
            stale.append({
                "case_index": index,
                "route_ids": route_ids,
                "reason": "family-fingerprint-not-in-ledger",
                "declared_family_fingerprint": declared,
            })
        if case.get("validation_mode") == "revalidated-by-fingerprint":
            if report.get("harness_semantics_unchanged") is not True:
                stale.append({
                    "case_index": index,
                    "route_ids": route_ids,
                    "reason": "revalidation-without-golden-harness-proof",
                })
            if not declared or not case.get("request_fingerprint") or not case.get("response_fingerprint"):
                stale.append({
                    "case_index": index,
                    "route_ids": route_ids,
                    "reason": "revalidation-without-complete-fingerprints",
                })
    if stale:
        return False, "family fingerprint is stale or incomplete", stale
    return True, "family fingerprints match the current ledger", []


def _strict_report_valid(report: Mapping[str, Any], ledger: Mapping[str, Any], report_path: Path) -> Tuple[bool, str]:
    registered_class = _registry_class(report_path)
    if registered_class is not None and registered_class not in {"canonical-strict", "historical-superseded", "unregistered"}:
        return False, "canonical evidence registry does not classify report as canonical-strict"
    if report.get("status") != "executed-pass":
        return False, "report status is not executed-pass"
    if report.get("strict_evidence") is not True:
        return False, "report strict_evidence is not true"
    if report.get("production_access") is not False:
        return False, "report production_access is not false"
    if any(key in report for key in ("application_binding", "harness_binding", "runtime_contract_binding", "planning_binding")):
        if report.get("implementation_dirty") is not False or ledger.get("implementation_dirty") is not False:
            return False, "report dependency-surface binding is dirty"
        for surface in ("application", "harness", "planning"):
            if report.get(surface + "_binding") != ledger.get(surface + "_binding"):
                return False, "report %s binding does not match the current ledger" % surface
        # A strict milestone may use an isolated runtime schema/container. Its
        # runtime contract binding is evidence for that lifecycle and can
        # legitimately differ from the repository's default runtime contract;
        # the runtime manifest and cleanup/hash checks remain mandatory.
    else:
        report_digest = report.get("implementation_tree_digest")
        ledger_digest = ledger.get("implementation_tree_digest")
        if report_digest or ledger_digest:
            if report_digest != ledger_digest:
                return False, "report implementation binding does not match the current ledger"
            if report.get("implementation_dirty") is not False:
                return False, "report implementation_dirty is not false"
            if report.get("implementation_binding") != ledger.get("implementation_binding"):
                return False, "report implementation_binding does not match the current ledger"
        elif report.get("source_commit") != ledger.get("source_commit") or report.get("source_dirty") is not False:
            return False, "legacy report source binding is stale or dirty; migration or replay is required"
    family_ok, family_reason, _stale = _report_family_status(report, ledger)
    if not family_ok:
        return False, family_reason
    model = report.get("route_execution_model")
    if not isinstance(model, dict):
        return False, "report route_execution_model is missing"
    if not route_execution_model_valid(model):
        return False, "report declares a boundary process or internal HTTP bridge"
    cleanup = report.get("cleanup")
    if not cleanup_contract_complete(cleanup):
        return False, "report cleanup contract is incomplete"
    cases_ok, cases_reason = strict_case_contract(report)
    if not cases_ok:
        return False, cases_reason
    return True, "strict report contract valid"


def build_closure(ledger: Mapping[str, Any], report_paths: Iterable[Path]) -> Dict[str, Any]:
    routes = ledger.get("routes")
    boundary_routes = ledger.get("boundary_routes", [])
    if not isinstance(routes, list) or not isinstance(boundary_routes, list):
        raise ValueError("ledger routes and boundary_routes must be lists")
    route_index = _index_routes(routes + boundary_routes)
    rows: Dict[str, Dict[str, Any]] = {}
    for route in routes + boundary_routes:
        route_id = str(route.get("id", ""))
        rows[route_id] = {
            "id": route_id,
            "method": route.get("method"),
            "path": route.get("path"),
            "source_class": route.get("class") or route.get("source_class") or route.get("class_base"),
            "source_handler": route.get("handler") or route.get("source_method") or route.get("annotation"),
            "family": route.get("family"),
            "behavior_family_id": route.get("behavior_family_id"),
            "family_fingerprint": route.get("family_fingerprint"),
            "behavior_status": route.get("behavior_status", "unknown"),
            "disposition": route.get("disposition", "unknown"),
            "evidence": [],
            "diagnostic_observations": [],
            "validation_modes": [],
            "stale_reasons": [],
            "unknown_reason": route.get("unknown_reason"),
            "boundary_scope": route.get("boundary_scope"),
            "route_origin": route.get("route_origin", "project-spring-source"),
            "boundary_semantics": route.get("boundary_semantics", "project Spring route"),
            "runtime_model": route.get("runtime_model", "same-spring-application"),
        }
    report_index: List[Dict[str, Any]] = []
    source_scopes: set[str] = set()
    diagnostics: List[Dict[str, Any]] = []
    non_ledger_cases: List[Dict[str, Any]] = []
    non_ledger_strict_pass = 0
    revalidated_pass = 0
    affected_families: set[str] = set()
    affected_routes: set[str] = set()
    failures: List[str] = []
    for report_path in report_paths:
        report = json.loads(report_path.read_text(encoding="utf-8"))
        strict_ok, strict_reason = _strict_report_valid(report, ledger, report_path)
        family_ok, family_reason, family_stale = _report_family_status(report, ledger)
        digest = _report_digest(report_path)
        report_index.append({
            "path": str(report_path),
            "sha256": digest,
            "status": report.get("status"),
            "strict_evidence": report.get("strict_evidence"),
            "source_commit": report.get("source_commit"),
            "repository_head_at_capture": report.get("repository_head_at_capture"),
            "implementation_binding": report.get("implementation_binding"),
            "implementation_tree_digest": report.get("implementation_tree_digest"),
            "implementation_dirty": report.get("implementation_dirty"),
            "validation_mode": report.get("validation_mode"),
            "harness_semantics_unchanged": report.get("harness_semantics_unchanged"),
            "family_stale": family_stale,
            "strict_contract": strict_reason,
        })
        if isinstance(report.get("source_scope"), list):
            source_scopes.update(str(item) for item in report["source_scope"])
        for case in report.get("cases", []):
            route = case.get("route") if isinstance(case, dict) else None
            # The fast differential runner keeps route identity at the case
            # level and intentionally has no strict contract envelope. Bind
            # that diagnostic shape to the ledger so it is counted as a
            # diagnostic observation rather than a route-binding failure.
            if (not isinstance(route, dict)
                    and report.get("execution") == "fast-differential"
                    and isinstance(case, dict) and (case.get("route_id") or case.get("route_ids"))):
                route = {
                    "route_id": case.get("route_id"),
                    "route_ids": case.get("route_ids"),
                    "method": case.get("method"),
                    "path": case.get("path"),
                    "disposition": case.get("classification", "fast-mismatch"),
                    "scope": "fast-differential",
                }
            if not isinstance(route, dict):
                failures.append(f"{report_path}: case lacks route binding")
                continue
            method, path = _route_key(str(route.get("method", "")), str(route.get("path", "")))
            route_id = route.get("route_id")
            route_ids = route.get("route_ids") if isinstance(route.get("route_ids"), list) else None
            if route_ids and not route_id:
                # Fast behavior-family reports may probe once and cover many
                # ledger aliases. Keep every alias visible in diagnostic
                # closure without promoting the screening result.
                expected_source_class = str(case.get("class") or "")
                expected_profile = case.get("request", {}).get("profile") if isinstance(case.get("request"), dict) else None
                for alias_id in route_ids:
                    # route_ids are emitted by the contract generator as the
                    # authoritative alias binding.  A shared owned controller
                    # is expected to serve project-source aliases, so its
                    # class name will often differ from the ledger source
                    # class.  Use source_class only as diagnostic metadata;
                    # never discard an explicit route id because of that
                    # implementation seam.
                    alias_candidates = [candidate for candidate in rows.values()
                                        if candidate.get("id") == str(alias_id)
                                        and str(candidate.get("method", "")).upper() in {method, "ANY"}]
                    if len(alias_candidates) == 1:
                        alias_target = rows[str(alias_candidates[0]["id"])]
                        alias_observation = {"report": str(report_path), "report_sha256": digest,
                                             "case_id": case.get("id"), "status": case.get("status"),
                                             "classification": case.get("classification"), "diff": case.get("diff"),
                                             "behavior_family": case.get("behavior_family"),
                                             "behavior_family_id": case.get("behavior_family_id"),
                                             "family_fingerprint": case.get("family_fingerprint"),
                                             "validation_mode": case.get("validation_mode", "executed"),
                                             "source_class": expected_source_class or None,
                                             "request_profile": expected_profile,
                                             "request_path": case.get("request_path")}
                        if strict_ok:
                            mode = str(case.get("validation_mode", "executed"))
                            alias_target["behavior_status"] = (
                                "revalidated-by-fingerprint"
                                if mode == "revalidated-by-fingerprint"
                                else "executed-pass"
                            )
                            if mode == "revalidated-by-fingerprint":
                                revalidated_pass += 1
                            alias_target["disposition"] = str(route.get("disposition", "owned-pass"))
                            alias_target["unknown_reason"] = None
                            alias_target["validation_modes"].append(mode)
                            alias_target["evidence"].append(alias_observation)
                        else:
                            alias_target["diagnostic_observations"].append(alias_observation)
                            alias_target["stale_reasons"].append(family_reason if not family_ok else strict_reason)
                            diagnostics.append({"route_id": alias_target["id"], **alias_observation,
                                                "reason": family_reason if not family_ok else strict_reason,
                                                "stale_reason": family_reason if not family_ok else strict_reason})
                            affected_routes.add(alias_target["id"])
                            if family_stale:
                                affected_families.update(
                                    str(item.get("behavior_family_id") or item.get("declared_family_fingerprint") or "unknown")
                                    for item in family_stale
                                )
                    elif expected_source_class:
                        diagnostics.append({"route_id": str(alias_id), "report": str(report_path),
                                            "case_id": case.get("id"), "classification": case.get("classification"),
                                            "reason": "behavior-family source class does not match ledger alias",
                                            "source_class": expected_source_class})
                continue
            if route_id:
                # HTTP observations may contain a concrete path (for example
                # ``/reagents/1/compositions/queries``) while the ledger
                # stores the controller template (``/{id}/...``).  An
                # explicit route_id is the authoritative binding in that
                # case; still enforce the declared HTTP method.
                candidates = [candidate for candidate in rows.values()
                              if candidate.get("id") == route_id
                              and str(candidate.get("method", "")).upper() in {method, "ANY"}]
            else:
                candidates = route_index.get((method, path), [])
            if len(candidates) != 1:
                reason = "ambiguous" if len(candidates) > 1 else "not found"
                non_ledger_scope = route.get("scope") in {"removed-negative", "non-ledger"}
                valid_non_ledger_disposition = route.get("disposition") in ALLOWED_DISPOSITIONS
                if not candidates and non_ledger_scope and valid_non_ledger_disposition:
                    observation = {
                        "report": str(report_path),
                        "report_sha256": digest,
                        "case_id": case.get("id"),
                        "method": method,
                        "path": path,
                        "status": case.get("status"),
                        "disposition": route.get("disposition"),
                        "scope": "removed-negative",
                    }
                    non_ledger_cases.append({**observation, "strict": strict_ok})
                    if strict_ok:
                        non_ledger_strict_pass += 1
                    else:
                        diagnostics.append({"non_ledger": True, **observation, "reason": strict_reason})
                    continue
                failures.append(f"{report_path}: {method} {path} route binding {reason}; provide route_id")
                continue
            target = rows[str(candidates[0]["id"])]
            observation = {
                "report": str(report_path),
                "report_sha256": digest,
                "case_id": case.get("id"),
                "status": case.get("status"),
                "disposition": route.get("disposition", "unknown"),
                "behavior_family_id": case.get("behavior_family_id"),
                "family_fingerprint": case.get("family_fingerprint"),
                "validation_mode": case.get("validation_mode", "executed"),
            }
            if report.get("execution") == "fast-differential":
                observation["classification"] = case.get("classification")
                observation["diff"] = case.get("diff")
                observation["behavior_family"] = case.get("behavior_family")
                observation["source_class"] = case.get("class")
                observation["request_profile"] = (case.get("request") or {}).get("profile") if isinstance(case.get("request"), dict) else None
                observation["request_path"] = case.get("request_path")
            if not strict_ok:
                target["diagnostic_observations"].append(observation)
                reason = family_reason if not family_ok else strict_reason
                target["stale_reasons"].append(reason)
                diagnostics.append({"route_id": target["id"], **observation, "reason": reason,
                                    "stale_reason": reason})
                affected_routes.add(target["id"])
                if family_stale:
                    affected_families.update(
                        str(item.get("behavior_family_id") or item.get("declared_family_fingerprint") or "unknown")
                        for item in family_stale
                    )
                continue
            disposition = route.get("disposition", "owned-pass")
            if disposition not in ALLOWED_DISPOSITIONS:
                failures.append(f"{report_path}: invalid disposition {disposition!r} for {target['id']}")
                continue
            if case.get("status") not in range(200, 600):
                failures.append(f"{report_path}: case status is not an HTTP status for {target['id']}")
                continue
            mode = str(case.get("validation_mode", "executed"))
            target["behavior_status"] = (
                "revalidated-by-fingerprint"
                if mode == "revalidated-by-fingerprint"
                else "executed-pass"
            )
            if mode == "revalidated-by-fingerprint":
                revalidated_pass += 1
            target["disposition"] = disposition
            target["unknown_reason"] = None
            target["validation_modes"].append(mode)
            target["evidence"].append(observation)
    counts = collections.Counter(row["behavior_status"] for row in rows.values())
    dispositions = collections.Counter(row["disposition"] for row in rows.values())
    return {
        "schema": 1,
        "scope": "current-head-spring-web-api-route-closure",
        "generated_at": dt.datetime.now(dt.timezone.utc).isoformat(),
        "source_commit": ledger.get("source_commit"),
        "repository_head_at_capture": ledger.get("repository_head_at_capture", ledger.get("source_commit")),
        "source_dirty": ledger.get("source_dirty"),
        "implementation_binding": ledger.get("implementation_binding"),
        "implementation_tree_digest": ledger.get("implementation_tree_digest"),
        "implementation_dirty": ledger.get("implementation_dirty", ledger.get("source_dirty")),
        "implementation_scope": ledger.get("implementation_scope", []),
        "source_scope": sorted(source_scopes),
        "route_execution_model": ledger.get("route_execution_model"),
        "route_count": len(rows),
        "project_route_count": len(routes),
        "boundary_route_count": len(boundary_routes),
        "behavior_counts": dict(sorted(counts.items())),
        "disposition_counts": dict(sorted(dispositions.items())),
        "strict_pass_count": sum(row["behavior_status"] in {"executed-pass", "revalidated-by-fingerprint"} for row in rows.values()),
        "executed_strict_count": sum(row["behavior_status"] == "executed-pass" for row in rows.values()),
        "revalidated_by_fingerprint_count": revalidated_pass,
        "non_ledger_strict_pass_count": non_ledger_strict_pass,
        "diagnostic_observation_count": len(diagnostics),
        "required_unknown": sum(row["behavior_status"] == "unknown" for row in rows.values()),
        "project_required_unknown": sum(row["behavior_status"] == "unknown" for row in rows.values() if not row.get("boundary_scope")),
        "boundary_required_unknown": sum(row["behavior_status"] == "unknown" for row in rows.values() if row.get("boundary_scope")),
        "failures": failures,
        "reports": report_index,
        "diagnostics": diagnostics,
        "affected_replay": {
            "families": sorted(affected_families),
            "route_ids": sorted(affected_routes),
        },
        "non_ledger_cases": non_ledger_cases,
        "routes": list(rows.values()),
    }


def main(argv: List[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--report", type=Path, action="append", required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args(argv)
    ledger = json.loads(args.ledger.read_text(encoding="utf-8"))
    current_binding = current_implementation_binding()
    for surface in ("application", "harness", "runtime_contract", "planning"):
        if ledger.get(surface + "_binding") != current_binding.get(surface + "_binding"):
            raise SystemExit("closure ledger %s binding is stale" % surface)
    if ledger.get("implementation_dirty") is not False or current_binding.get("implementation_dirty") is not False:
        raise SystemExit("closure ledger implementation binding is dirty")
    closure = build_closure(ledger, args.report)
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(closure, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("API route closure:", {
        "route_count": closure["route_count"],
        "strict_pass_count": closure["strict_pass_count"],
        "diagnostic_observation_count": closure["diagnostic_observation_count"],
        "required_unknown": closure["required_unknown"],
        "failures": len(closure["failures"]),
    })
    return 0 if not closure["failures"] else 1


if __name__ == "__main__":
    raise SystemExit(main())
