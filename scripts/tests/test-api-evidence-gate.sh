#!/usr/bin/env bash
# API/closure gate: lifecycle publication safety plus deterministic API planners.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"

bash "$ROOT/scripts/tests/test-evidence-lifecycle.sh" >/dev/null
python3 "$ROOT/scripts/tests/test-api-route-closure.py" >/dev/null
python3 "$ROOT/scripts/tests/test-api-route-execution-plan.py" >/dev/null
python3 "$ROOT/scripts/tests/test-api-wave-orchestrator.py" >/dev/null
python3 "$ROOT/scripts/tests/test-fast-differential-api-runner.py" >/dev/null
python3 "$ROOT/scripts/tests/test-current-head-evidence.py" >/dev/null

echo 'API evidence/closure gate PASS'
