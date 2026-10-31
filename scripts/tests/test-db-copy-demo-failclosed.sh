#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/db-copy-demo.sh"
bash -n "$SCRIPT"
grep -Fq 'whenever sqlerror exit sql.sqlcode' "$SCRIPT" || {
  echo "db-copy demo test: generated fill SQL does not fail closed" >&2; exit 1;
}
grep -Fq 'if ! sql_app < "$SQL_FILE"' "$SCRIPT" || {
  echo "db-copy demo test: SQL fill exit status is ignored" >&2; exit 1;
}
if grep -Fq 'sql_app < "$SQL_FILE" > "$OUT_DIR/t01-demo-fill.log" 2>&1 || true' "$SCRIPT"; then
  echo "db-copy demo test: fill error is still masked" >&2; exit 1;
fi
grep -Fq '拒绝生成成功基准' "$SCRIPT" || {
  echo "db-copy demo test: Oracle error log does not block baseline" >&2; exit 1;
}
echo "db-copy demo fail-closed test PASS"
