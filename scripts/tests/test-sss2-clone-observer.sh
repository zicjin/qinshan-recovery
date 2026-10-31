#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/observe-sss2-clone-runtime.sh"
[[ -x "$SCRIPT" ]]
bash -n "$SCRIPT"
grep -Fq "clone-ready-unverified" "$SCRIPT"
grep -Fq "source_schema_metadata_sha256" "$SCRIPT"
grep -Fq "oracle_restarted':False" "$SCRIPT"
if grep -Eq 'docker (restart|stop|rm|start|create|run)' "$SCRIPT"; then
  echo 'clone observer must be read-only' >&2
  exit 1
fi
echo 'sss2 clone observer contract: PASS'
