#!/usr/bin/env python3
"""Ensure historical scheduler declarations have explicit isolated status."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
REPORT = ROOT / ".scratch/sunway-supplier-exit-api-first/archive/historical/scheduler-runtime-20261022.json"
payload = json.loads(REPORT.read_text(encoding="utf-8"))
assert payload["status"] == "executed-pass"
assert payload["production_access"] is False
assert payload["source_dirty"] is False
assert payload["historical_equivalence"].startswith("unknown;")
checks = set(payload["checks"])
assert "historical scheduled declarations visibly disabled" in checks
assert "default/disabled mode has no scheduler routes" in checks
assert "POST /tasks/dcuAcquisition/trigger HTTP 403" in checks
assert "POST /tasks/invented-job/trigger HTTP 404" in checks
assert sum(1 for row in payload["journal"] if row["status"] == "DISABLED" and row["message"] == "disabled-in-isolated-env") == 1
assert payload["cleanup"]["fixture_root_removed"] is True
assert all(not re.search(r"password|token|secret", json.dumps(row), re.I) for row in payload["journal"])
print("scheduler disabled status: PASS (four historical declarations disabled, unknown task rejected, default routes absent)")
