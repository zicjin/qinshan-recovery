#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/refresh-owned-sss2-debug-app.sh"

[[ -x "$SCRIPT" ]]
bash -n "$SCRIPT"
grep -Fq 'lims-owned-sss2-oracle' "$SCRIPT"
grep -Fq 'lims-owned-sss2-redis' "$SCRIPT"
grep -Fq 'reused diagnostic app' "$SCRIPT"
grep -Fq 'FORCE_REFRESH' "$SCRIPT"
grep -Fq 'existing_owned_debug_port_preserved' "$SCRIPT"
grep -Fq 'runtime_mode' "$SCRIPT"
grep -Fq 'oracle_restarted' "$SCRIPT"
grep -Fq 'redis_restarted' "$SCRIPT"
if grep -Eq 'docker (rm|stop|restart).*\$(ORA|REDIS)' "$SCRIPT"; then
  echo 'debug refresh must not mutate Oracle or Redis containers' >&2
  exit 1
fi
echo 'sss2 debug runtime reuse contract: PASS'
