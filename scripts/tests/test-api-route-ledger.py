#!/usr/bin/env python3
"""Validate the route ledger shape without promoting behavior."""
import importlib.util
import json
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parents[2]
import sys
sys.path.insert(0, str(ROOT / "scripts"))
from source_binding import REPO_ROOT, is_standalone_root
PENDING = REPO_ROOT.parent / ".scratch/sunway-supplier-exit-api-first/diagnostic-evidence/pending"
CURRENT_HEAD = subprocess.check_output(
    ["git", "-C", str(REPO_ROOT), "rev-parse", "HEAD"], text=True
).strip()
LEDGER = (REPO_ROOT / "evidence/api-route-ledger-current-head.json"
          if is_standalone_root(REPO_ROOT) else
          REPO_ROOT.parent / ".scratch/sunway-supplier-exit-api-first/archive/historical/api-route-ledger-20261027-current-head.json")
for candidate in sorted(PENDING.glob("current-*/api-route-ledger-current-head.json"), reverse=True):
    try:
        payload = json.loads(candidate.read_text(encoding="utf-8"))
    except (OSError, ValueError):
        continue
    if payload.get("source_commit") == CURRENT_HEAD and payload.get("source_dirty") is False:
        LEDGER = candidate
        break
MODULE_PATH = ROOT / "tools" / "api_route_ledger.py"
SPEC = importlib.util.spec_from_file_location("api_route_ledger", MODULE_PATH)
MODULE = importlib.util.module_from_spec(SPEC)
assert SPEC and SPEC.loader
SPEC.loader.exec_module(MODULE)

payload = json.loads(LEDGER.read_text(encoding="utf-8"))
routes = payload["routes"]
boundary_routes = payload.get("boundary_routes", [])
assert payload["schema"] == 2
assert payload["counts"]["route_count"] == len(routes)
assert payload["counts"]["project_route_count"] == len(routes)
assert payload["counts"]["total_route_count"] == len(routes) + len(boundary_routes)
assert payload["counts"]["behavior_unknown"] == len(routes)
assert {row["native_relevance"] for row in routes} == {"P0", "P1", "P2"}
assert {row["wave"] for row in routes} == {"core", "business", "secondary"}
required = {
    "source", "class", "method", "path", "native_relevance", "wave", "database_objects",
    "database_objects_known", "roles", "organizations", "mode", "fixture", "expected_result",
    "audit", "cleanup", "runner", "evidence", "unknown_reason", "family", "family_reason",
    "route_kind", "disposition", "disposition_reason",
}
assert all(required.issubset(row) for row in routes)
assert all(row["unknown_reason"] for row in routes)
assert payload["counts"]["disposition_unknown"] == len(routes)
assert sum(payload["counts"]["family"].values()) == len(routes)
assert payload["counts"]["boundary_route_count"] == len(boundary_routes)
assert payload["counts"]["boundary_behavior_unknown"] == len(boundary_routes)
assert all(row.get("boundary_scope") == "owned-source-only" for row in boundary_routes)
assert all(row.get("route_origin") == "owned-java-source-only" for row in boundary_routes)
assert all(row.get("runtime_model") == "same-spring-application" for row in boundary_routes)
assert all("boundary route != boundary process" in row.get("boundary_semantics", "") for row in boundary_routes)
assert payload["route_execution_model"]["boundary_process"] is False
assert payload["route_execution_model"]["internal_http_bridge"] is False
assert all(required.issubset(row) for row in boundary_routes)
assert set(payload["counts"]["family"]) <= set(MODULE.FAMILY_ORDER)
minimal = MODULE.build({
    "source_commit": "test",
    "source_dirty": False,
    "routes": [{"id": "r1", "source": "x.java", "class": "X", "method": "GET", "path": "/x",
                "behavior_status": "unknown"}],
    "boundary_routes": [],
})
assert minimal["planning_metadata"]["native_registry"]["role"] == "browser-priority-only"
assert minimal["planning_metadata"]["native_registry"]["path"] is None
assert MODULE.classify_family({"source": "x/mdm/emp/EmpResource.java", "class": "EmpResource", "path": "/secure/emc/module/mdm/emp/emps", "annotation": "RequestMapping"})["family"] == "personnel"
assert MODULE.classify_family({"source": "x/bp/result/RunResource.java", "class": "RunResource", "path": "/secure/emc/module/bp/result/runs", "annotation": "RequestMapping"})["family"] == "result-order-project"
assert MODULE.classify_route_kind({"source": "x/controller/WorkspaceController.java", "class": "WorkspaceController", "path": "/workspace", "annotation": "RequestMapping"})["route_kind"] == "workspace"
print("API route ledger: PASS (%d project routes + %d owned boundary routes; family/route-kind/disposition fields present; behavior remains unknown)" % (len(routes), len(boundary_routes)))
