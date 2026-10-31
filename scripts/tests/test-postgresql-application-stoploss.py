#!/usr/bin/env python3
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REPORT = ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/t15-postgresql-application-stoploss-20261020.json"

report = json.loads(REPORT.read_text())
assert report["status"] == "executed-pass"
assert report["production_access"] is False
assert report["final_mainline_decision"] == "oracle-fallback"
assert report["application_compatibility"].startswith("failed-at-login-sqlstate-")
assert report["isolation"]["network_internal"] is True
assert report["isolation"]["port_bindings"] == {}
assert report["isolation"]["bind_mount_count"] == 0
assert report["cleanup"]["container_removed"] is True
assert report["cleanup"]["network_removed"] is True
assert report["negative_control"]["invalid_object_rejected"] is True
assert report["negative_control"]["sqlstate"] == "42P01"
assert any(x.startswith("DIRECT|login|") for x in report["application_markers"])
assert any(x.startswith("HTTP|login|500|LOGIN_UNAVAILABLE") for x in report["application_markers"])
assert "HTTP|/secure/core/module/sys/menus|403|PERMISSION_UNAUTHORIZED" in report["application_markers"]
assert all(not re.search(r"Ephemeral|secret|password=", x, re.I) for x in report["application_markers"])
print("postgresql application stop-loss evidence: PASS")
