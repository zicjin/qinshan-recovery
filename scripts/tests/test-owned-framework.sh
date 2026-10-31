#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
"$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
test -s "$ROOT/owned-framework/target/lims-owned-framework-1.0.0.jar"
grep -q 'owned framework build and contract test passed' "$TMP/build.log"
echo "test-owned-framework: PASS"
