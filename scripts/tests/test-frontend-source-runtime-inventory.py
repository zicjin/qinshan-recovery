#!/usr/bin/env python3
"""Contract tests for the source-owned frontend/runtime inventory generator."""
import json
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
GENERATOR = ROOT / "scripts/frontend-source-runtime-inventory.py"


def run_inventory(output):
    return subprocess.run(
        [sys.executable, str(GENERATOR), "--output", str(output)],
        cwd=ROOT, capture_output=True, text=True, check=False,
    )


def main():
    assert GENERATOR.is_file(), "inventory generator is missing"
    with tempfile.TemporaryDirectory(prefix="frontend-inventory-test-") as directory:
        output = Path(directory) / "inventory.json"
        first = run_inventory(output)
        assert first.returncode == 0, first.stdout + first.stderr
        payload = json.loads(output.read_text())
        assert payload["schema"] == 1
        assert isinstance(payload["source_dirty"], bool)
        assert payload["counts"]["project_static"] >= 800
        assert payload["counts"]["project_templates"] >= 600
        assert payload["counts"]["page_controllers"] >= 300
        assert payload["files"]["project_static"]
        assert payload["files"]["project_templates"]
        assert payload["page_controllers"]
        summary = payload["page_controller_route_summary"]
        assert summary["controller_mapping_records"] >= 493
        assert summary["unique_request_paths"] >= 450
        assert summary["secure_page_paths"] >= 150
        assert any(item.get("request_mapping_records") for item in payload["page_controllers"])
        assert payload["owned_rebuild_required"]
        assert any(item["route"] == "/static/gikam/main/js/home.js" for item in payload["owned_rebuild_required"])
        assert payload["supplier_runtime_inputs"]
        assert all("sunway-app.so" not in item for item in payload["supplier_runtime_inputs"])
        assert payload["counts"]["project_static"] == 841
        assert payload["counts"]["project_templates"] == 661
        assert payload["counts"]["page_controllers"] == 341
        assert payload["counts"]["original_frontend_static"] == 639
        assert payload["counts"]["supplier_runtime_inputs"] == 2
        assert payload["count_reconciliation"]["historical_comment_claim"] == 868
        assert payload["count_reconciliation"]["current_project_static_is_authoritative"] is True
        assert payload["count_reconciliation"]["current_formula"] == (
            "841 project files + 28 non-shell owned overlays + 639 imported original frontend files = 1507"
        )
        assert payload["count_reconciliation"]["current_owned_overlay_breakdown"] == {
            "non_shell": 28, "gikam_lcdp_shell": 0, "total_unique": 28,
        }
        assert payload["count_reconciliation"]["historical_delta"] == {
            "static_union_delta": 639,
            "additional_non_shell_overlays": 1,
            "gikam_lcdp_shell_overlays": 0,
        }
        assert payload["counts"]["packaged_static_union_estimate"] == 1507
        baseline = payload["read_only_baseline_contract"]
        if baseline["available"]:
            assert payload["counts"]["authenticated_resource_urls"] >= 20
        else:
            # A standalone clean clone does not carry the restricted browser
            # baseline artifact. Static source inventory remains testable; the
            # authenticated URL closure is supplied through external evidence.
            assert payload["counts"]["authenticated_resource_urls"] == 0
        assert {item["fragment"] for item in payload["gikam_template_fragments"]} == {"home", "workspace"}
        assert all(item["classification"] == "third-party-allowed" for item in payload["third_party_allowed"])
        plan = payload["owned_rebuild_plan"]
        assert {item["priority"] for item in plan} >= {"P0", "P1", "P2"}
        assert any(item["id"] == "authenticated-baseline-capture" and item["status"] == "waiting-for-permitted-matching-test-account" for item in plan)
        assert payload["baseline_contract"]["authenticated_observation"] in {"sss2-diagnostic-captured", "open"}
        assert payload["baseline_contract"]["production_access"] is False
        if baseline["available"]:
            assert baseline["production_read_only"] is True
            assert baseline["credentials_used"] is False
        if baseline["available"]:
            assert set(baseline["targets"]) == {"production", "supplier"}
            for target in baseline["targets"].values():
                routes = target["routes"]
                assert "/" in routes
                assert "/login" in routes
                assert "/secure/core/module/index/workspace" in routes
                assert "/secure/core/module/sys/menus" in routes
                assert routes["/"]["initial_resource_urls"]
        classification = payload["resource_classification_catalog"]
        assert classification["private_jar_internal_resources_enumerated"] is False
        assert set(classification["classification_counts"]) >= {
            "source-present", "source-missing", "owned-rebuild-required",
            "third-party-allowed",
        }
        assert classification["classification_counts"]["source-missing"] == 0
        assert classification["classification_counts"]["owned-rebuild-required"] == 0
        minimum_imported = 13 if baseline["available"] else 6
        assert classification["classification_counts"]["original-frontend-imported"] >= minimum_imported
        assert classification["classification_counts"]["runtime-bundle-present-repository-copy-absent"] >= 2
        assert classification["classification_counts"]["third-party-allowed"] == 2
        if baseline["available"]:
            assert any(
                item["id"] == "/static/gikam/main/js/workspace.js"
                and item["classifications"] == ["original-frontend-imported", "license-native-cleaned"]
                for item in classification["entries"]
            )
        authenticated = payload["authenticated_resource_contract"]
        assert authenticated["available"] is baseline["available"]
        if authenticated["available"]:
            workspace = next(item for item in authenticated["resources"] if item["url"] == "/static/gikam/main/js/workspace.js")
            assert workspace["classification"] == "original-frontend-imported"
            assert workspace["classifications"] == ["original-frontend-imported", "license-native-cleaned"]
            assert any("source-present" in item["classifications"] for item in authenticated["resources"])
        assert all(item["classification"] == "third-party-allowed" for item in payload["third_party_allowed"])
        if baseline["available"]:
            assert any(item["id"] == "authenticated-gikam-runtime-resources" for item in plan)
        assert all(
            set(item) <= {"path", "sha256", "size"}
            for records in payload["files"].values()
            for item in records
        )
        first_bytes = output.read_bytes()
        second = run_inventory(output)
        assert second.returncode == 0, second.stdout + second.stderr
        assert output.read_bytes() == first_bytes, "inventory output is not deterministic"
    print("frontend source/runtime inventory checks passed")


if __name__ == "__main__":
    main()
