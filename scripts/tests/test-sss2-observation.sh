#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
OBS="$ROOT/scripts/sss2-observe.sh"; REG="$ROOT/scripts/sss2-code-regression.sh"
bash -n "$OBS" "$REG"
tmp="$(mktemp -d)"; trap 'rm -rf "$tmp"' EXIT
receipt="$tmp/clone-a"
"$OBS" init clone-a "$receipt" fixture-baseline
"$OBS" record "$receipt" personnel.query read-flow-verified .scratch/sunway-supplier-exit-api-first/evidence-archive/t03-personnel-query.md authorized fixture-data
"$OBS" record "$receipt" tasks.scheduler disabled-in-isolated-env .scratch/sunway-supplier-exit-api-first/evidence-archive/t06-isolation-current.txt operator fixture-data
"$REG" "$receipt" personnel.write 0f06391 3578b2f evidence/sss2-code-regression.md >/dev/null
"$OBS" verify "$receipt" >/dev/null
grep -Fxq 'environment=sss2-writable-clone' "$receipt/metadata.env"
grep -Fxq 'external_side_effects=blocked' "$receipt/metadata.env"
grep -Fxq 'observation_count=3' "$receipt/summary.env"
if "$OBS" init production "$tmp/bad" >/dev/null 2>&1; then echo 'sss2 test: production id accepted' >&2; exit 1; fi
if "$OBS" record "$receipt" identity.login unknown evidence/x.md role password=bad >/dev/null 2>&1; then echo 'sss2 test: secret metadata accepted' >&2; exit 1; fi
printf 'tamper\n' >> "$receipt/observations.tsv"
if "$OBS" verify "$receipt" >/dev/null 2>&1; then echo 'sss2 test: tampered receipt accepted' >&2; exit 1; fi
echo 'sss2 observation test PASS'
