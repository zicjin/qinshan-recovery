#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
for script in "$ROOT/scripts/start-strict-sss2-api-runtime.sh" "$ROOT/scripts/cleanup-strict-sss2-api-runtime.sh"; do
  [[ -x "$script" ]]; bash -n "$script"
done
[[ -x "$ROOT/scripts/start-reused-sss2-api-runtime.sh" ]]; bash -n "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'clone-ready-unverified' "$ROOT/scripts/start-strict-sss2-api-runtime.sh"
grep -Fq 'SOURCE_DIRTY' "$ROOT/scripts/start-strict-sss2-api-runtime.sh"
grep -Fq 'REUSE_ORACLE' "$ROOT/scripts/start-strict-sss2-api-runtime.sh"
grep -Fq 'oracle_reused' "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'baseline_schema' "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'REFRESH_BASELINE' "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'owned.personnel.write.mode=isolated' "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'owned.equipment.write.mode=isolated' "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'owned.consumables.write.mode=isolated' "$ROOT/scripts/start-reused-sss2-api-runtime.sh"
grep -Fq 'nsenter -n -t' "$ROOT/scripts/start-strict-sss2-api-runtime.sh"
grep -Fq 'strict source export failed' "$ROOT/scripts/start-strict-sss2-api-runtime.sh"
grep -Fq "'remote_root':root" "$ROOT/scripts/start-strict-sss2-api-runtime.sh"
grep -Fq 'host_forward_pid' "$ROOT/scripts/cleanup-strict-sss2-api-runtime.sh"
if grep -Fq "'source_dirty':False" "$ROOT/scripts/start-strict-sss2-api-runtime.sh"; then
  echo 'strict runtime script hard-codes source_dirty=false' >&2; exit 1
fi
grep -Fq 'docker rm --force --volumes' "$ROOT/scripts/cleanup-strict-sss2-api-runtime.sh"
grep -Fq 'persistent-oracle' "$ROOT/scripts/cleanup-strict-sss2-api-runtime.sh"
if grep -Eiq '10\.100\.|218\.93\.' "$ROOT/scripts/start-strict-sss2-api-runtime.sh"; then
  echo 'strict runtime script contains production marker' >&2; exit 1
fi
echo 'strict API runtime script contracts: PASS'
