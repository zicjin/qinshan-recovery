#!/usr/bin/env python3
"""Validate the shape and redaction boundary of the authenticated sss2 report."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REPORT = ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/frontend-authenticated-baseline-sss2-20261026.json"
payload = json.loads(REPORT.read_text())
assert payload["status"] == "diagnostic-pass"
assert payload["mode"] == "sss2-authenticated-read-only"
assert payload["production_access"] is False
assert payload["credentials_used"] is True
assert payload["credential_material_stored"] is False
assert set(payload["targets"]) == {"supplier", "owned"}
for target in payload["targets"].values():
    serialized = json.dumps(target).lower()
    assert "body" not in serialized
    assert "password" not in serialized
    assert "cookie" not in serialized
    assert "token" not in serialized
    assert set(target["page_routes"]) == {
        "/secure/emc/module/mdm/emp/emps/page/edit-list?readonly=1",
        "/secure/emc/module/mdm/equipt/equipts/page/edit-list?readonly=1",
        "/secure/emc/module/item/consume/consumes/page/edit-list?readonly=1",
        "/secure/emc/module/bp/reagents/page/edit-list",
        "/secure/emc/module/index/workspace/download-attachment",
    }
    for route in target["page_routes"].values():
        assert "body" not in json.dumps(route).lower()
    assert set(target["api_routes"]) == {"/secure/emc/module/bp/reagents/queries/raw"}
    assert "body" not in json.dumps(target["api_routes"]).lower()
    tree = target["menu_tree_summary"]
    assert tree["json"] is True
    assert tree["node_count"] > 0
    assert tree["root_count"] > 0
    assert tree["ids_sha256"]
assert payload["targets"]["owned"]["routes"]["/secure/core/module/sys/menus"]["status"] == 200
assert payload["targets"]["owned"]["routes"]["/secure/core/module/sys/menus/tree/root"]["status"] == 200
assert payload["targets"]["supplier"]["routes"]["/module/index/workspaces"]["status"] == 200
assert payload["targets"]["owned"]["routes"]["/module/index/workspaces"]["status"] == 200
assert payload["targets"]["owned"]["login_identity"]["authenticated"] is True
assert payload["targets"]["owned"]["login_identity"]["identity_fields"] == ["id", "orgId", "orgName"]
assert payload["comparison"]["page_route_statuses_same"] is True
assert payload["targets"]["owned"]["api_routes"]["/secure/emc/module/bp/reagents/queries/raw"]["status"] == 200
assert payload["targets"]["owned"]["api_routes"]["/secure/emc/module/bp/reagents/queries/raw"]["summary"]["json"] is True
supplier_reagent = payload["targets"]["supplier"]["api_routes"]["/secure/emc/module/bp/reagents/queries/raw"]["summary"]
owned_reagent = payload["targets"]["owned"]["api_routes"]["/secure/emc/module/bp/reagents/queries/raw"]["summary"]
for field in ("keys", "first", "last", "numberOfElements", "pageNumber", "pageSize", "total", "totalPages", "row_count"):
    assert owned_reagent[field] == supplier_reagent[field], (field, supplier_reagent, owned_reagent)
assert payload["comparison"]["page_route_statuses"][
    "/secure/emc/module/mdm/emp/emps/page/edit-list?readonly=1"
] == {"supplier": 200, "owned": 200}
assert payload["comparison"]["page_route_statuses"][
    "/secure/emc/module/mdm/equipt/equipts/page/edit-list?readonly=1"
] == {"supplier": 200, "owned": 200}
assert payload["comparison"]["page_route_statuses"][
    "/secure/emc/module/item/consume/consumes/page/edit-list?readonly=1"
] == {"supplier": 200, "owned": 200}
assert payload["comparison"]["page_route_statuses"][
    "/secure/emc/module/bp/reagents/page/edit-list"
] == {"supplier": 200, "owned": 200}
assert payload["targets"]["owned"]["menu_summary"]["menu_count"] > 0
assert payload["targets"]["supplier"]["routes"]["/secure/core/module/sys/menus"]["status"] != 200
print("authenticated sss2 frontend baseline contract: PASS")
