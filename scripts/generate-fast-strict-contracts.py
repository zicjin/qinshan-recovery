#!/usr/bin/env python3
"""Generate read-only strict contract documents from fast-equivalent probes.

The generated cases retain ``route_ids`` and source identity.  The HTTP runner
executes one representative request; the closure mapper promotes the result to
every alias only when the source class and HTTP method match the ledger.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any, Dict, List, Mapping, Sequence

ROOT = Path(__file__).resolve().parents[1]
REPO_ROOT = ROOT.parent
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from evidence_output import guard_output_path, current_implementation_binding  # noqa: E402


def _load(path: Path) -> Dict[str, Any]:
    value = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(value, dict):
        raise ValueError("JSON document must be an object: " + str(path))
    return value


def build(fast: Mapping[str, Any], ledger: Mapping[str, Any], *, family: str | None = None) -> Dict[str, Any]:
    current = current_implementation_binding()
    for label, document in (("fast report", fast), ("ledger", ledger)):
        if document.get("implementation_tree_digest") != current["implementation_tree_digest"]:
            raise ValueError(label + " implementation binding is stale")
        if document.get("implementation_binding") != current["binding_id"]:
            raise ValueError(label + " implementation_binding does not match current implementation")
        if document.get("implementation_dirty") is not False or current["implementation_dirty"]:
            raise ValueError(label + " implementation scope is dirty")
    if fast.get("execution") != "fast-differential":
        raise ValueError("input is not a fast differential report")
    cases: List[Dict[str, Any]] = []
    skipped: Dict[str, int] = {}
    for probe in fast.get("cases", []):
        if not isinstance(probe, dict):
            continue
        if probe.get("classification") != "fast-equivalent":
            reason = str(probe.get("classification", "unknown"))
            skipped[reason] = skipped.get(reason, 0) + 1
            continue
        if family and str(probe.get("family")) != family:
            continue
        route_ids = probe.get("route_ids") or ([probe["route_id"]] if probe.get("route_id") else [])
        if not route_ids:
            skipped["missing-route-id"] = skipped.get("missing-route-id", 0) + 1
            continue
        request = probe.get("request") if isinstance(probe.get("request"), dict) else {}
        owned = probe.get("owned") if isinstance(probe.get("owned"), dict) else {}
        owned_json = owned.get("json") if isinstance(owned.get("json"), dict) else {}
        expected_status = int(owned.get("status", 200))
        profile = str(request.get("profile", "default"))
        behavior_family = str(probe.get("behavior_family", "unknown"))
        case: Dict[str, Any] = {
            "id": "fast-strict-" + behavior_family + "-" + profile,
            "method": request.get("method", probe.get("method", "GET")),
            "path": probe.get("request_path") or probe.get("path"),
            "headers": {"Accept": "application/json, text/plain, */*"},
            "route_ids": [str(item) for item in route_ids],
            "operation": "read",
            "scope": "behavior-family-strict-read",
            "disposition": "owned-pass",
            "expected_status": expected_status,
            "assert": {"content_type": owned.get("content_type")},
            "fixture": "none",
            "audit": "not-required",
            "source_class": probe.get("class"),
            "behavior_family": behavior_family,
            "request_profile": profile,
        }
        if request.get("body") is not None:
            case["body"] = request["body"]
        if isinstance(owned_json, dict):
            case["assert"]["json_keys"] = sorted(str(key) for key in owned_json.keys())
        cases.append(case)
    return {
        "schema": 1,
        "family": family or "fast-equivalent",
        "description": "Generated read-only strict contracts from fast-equivalent behavior families.",
        "source_commit": ledger.get("source_commit"),
        "source_dirty": ledger.get("source_dirty"),
        "repository_head_at_capture": ledger.get("repository_head_at_capture", ledger.get("source_commit")),
        "implementation_binding": ledger.get("implementation_binding"),
        "implementation_tree_digest": ledger.get("implementation_tree_digest"),
        "implementation_dirty": ledger.get("implementation_dirty"),
        "implementation_scope": ledger.get("implementation_scope", []),
        "strict_evidence": False,
        "planning_source": "fast-equivalent-diagnostic",
        "route_execution_model": {
            "application": "one owned Spring application",
            "oracle": "one clone-derived Oracle per wave",
            "redis": "one Redis per wave",
            "boundary_process": False,
            "internal_http_bridge": False,
            "jni_replacement_process": False,
        },
        "cases": cases,
        "skipped": skipped,
        "policy": "Generated contracts require real HTTP replay; fast-equivalent itself is never strict evidence.",
    }


def main(argv: Sequence[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--fast-report", type=Path, required=True)
    parser.add_argument("--ledger", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--family")
    args = parser.parse_args(argv)
    result = build(_load(args.fast_report), _load(args.ledger), family=args.family)
    guard_output_path(args.output)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("generated fast strict contracts:", {"cases": len(result["cases"]), "skipped": result["skipped"]})
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
