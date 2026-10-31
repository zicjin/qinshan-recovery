#!/usr/bin/env bash
set -euo pipefail
SSH_HOST="${SSH_HOST:-sss2}"
REMOTE_ROOT="${REMOTE_ROOT:-/data/lims-owned-sss2}"
ssh "$SSH_HOST" bash -s -- "$REMOTE_ROOT" <<'REMOTE'
set -euo pipefail
ROOT="$1"
if [ -s "$ROOT/port-forward.pid" ]; then
  kill "$(cat "$ROOT/port-forward.pid")" >/dev/null 2>&1 || true
fi
for name in lims-owned-sss2-app lims-owned-sss2-redis lims-owned-sss2-oracle; do
  docker rm -f "$name" >/dev/null 2>&1 || true
done
docker image rm --force lims-owned-sss2-app:current >/dev/null 2>&1 || true
docker image rm --force lims-owned-sss2-owned:current >/dev/null 2>&1 || true
docker volume rm lims-owned-sss2-oracle-data >/dev/null 2>&1 || true
docker network rm lims-owned-sss2-net >/dev/null 2>&1 || true
rm -rf "$ROOT/incoming-owned" "$ROOT/refresh-owned-app-context"
rm -f "$ROOT/application.properties" "$ROOT/debug-application.properties" "$ROOT/app-refresh-report.json" "$ROOT/source.properties" "$ROOT/audit-line-schema.sql" "$ROOT/port-forward.pid" "$ROOT/port-forward.py" "$ROOT/port-forward.log"
echo "cleanup-owned-sss2: removed owned resources; existing sss2 containers were not touched"
REMOTE
