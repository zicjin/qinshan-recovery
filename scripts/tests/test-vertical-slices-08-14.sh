#!/usr/bin/env bash
# Deterministic contract/replay regression for tickets 08-14.
# This test exercises owned public seams only; page/runtime parity remains unavailable.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
export PYTHONPATH="$ROOT/tools"
bash "$ROOT/scripts/tests/test-api-evidence-gate.sh" >/dev/null
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
python3 - "$tmp" <<'PY'
import json, sys
from pathlib import Path
from vertical_slices.harness import (
    DataScope, HarnessError, IntegrationReplay, IsolationViolation, PermissionDenied,
    Task, VerticalSliceHarness,
)
root = Path(sys.argv[1])
h = VerticalSliceHarness(root / "attachments")
alice = h.auth.login("alice", "fixture-password")
bob = h.auth.login("bob", "fixture-password")
assert h.queries["personnel"].list(alice)["total"] == 1
assert h.queries["personnel"].list(alice, contains="no-match")["total"] == 0
assert h.queries["personnel"].detail(alice, "p1")["organisation"] == "org-a"
try:
    h.queries["equipment"].list(bob)
except PermissionDenied:
    pass
else:
    raise AssertionError("low privilege equipment query was allowed")
try:
    h.queries["personnel"].detail(alice, "p2")
except KeyError:
    pass
else:
    raise AssertionError("out-of-scope personnel was visible")
h.attachments.register("a1", "fixture.txt", b"fixture", permission="attachment:read")
assert h.attachments.fetch(alice, "a1") == b"fixture"
try:
    h.attachments.fetch(alice, "missing")
except FileNotFoundError:
    pass
else:
    raise AssertionError("missing attachment did not fail")
assert h.replay.execute("fixture://inventory", source="test", idempotency_key="k")["status"] == 200
assert h.replay.execute("fixture://inventory", source="test", idempotency_key="k")["replayed"]
try:
    h.replay.execute("http://10.100.2.196:8081", source="test", idempotency_key="production")
except IsolationViolation:
    pass
else:
    raise AssertionError("production endpoint was not rejected")
assert h.reports.run(alice, "inventory", {"org": "org-a"}, endpoint="fixture://report")["status"] == 200
attempts = []
def flaky():
    attempts.append(1)
    if len(attempts) == 1: raise RuntimeError("fixture failure")
    return {"changed": 0}
h.scheduler.register(Task("flaky", "task:run", flaky))
assert h.scheduler.trigger(alice, "flaky", retries=1)["changed"] == 0
h.scheduler.register(Task("external", "task:run", lambda: {"sent": 1}, external_write=True))
try:
    h.scheduler.trigger(alice, "external")
except IsolationViolation:
    pass
else:
    raise AssertionError("external scheduler write was allowed")
assert h.scheduler.rollback_journal
assert h.audit.events("auth.login.success")
assert h.audit.events("auth.denied")
assert h.audit.events("integration.retry")
assert h.audit.events("scheduler.retry")
(root / "audit.jsonl").write_text("\n".join(json.dumps(x, sort_keys=True) for x in h.audit.records) + "\n")
print("vertical slices 08-14 contract/replay PASS")
PY
