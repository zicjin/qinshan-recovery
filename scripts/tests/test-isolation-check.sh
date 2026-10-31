#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/isolation-check.sh"
bash -n "$SCRIPT"
grep -Fq 'assert_restricted_file "$CFG"' "$SCRIPT" || {
  echo "isolation test: missing restricted config validation" >&2; exit 1;
}
grep -Fq 'endpoint_count=' "$SCRIPT" || {
  echo "isolation test: endpoint inventory may be empty without failing" >&2; exit 1;
}
grep -Fq 'NF < 3' "$SCRIPT" || {
  echo "isolation test: malformed endpoint rows are not rejected" >&2; exit 1;
}
echo "isolation-check test PASS"
