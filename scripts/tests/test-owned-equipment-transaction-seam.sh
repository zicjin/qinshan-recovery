#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SOURCE="$ROOT/owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedWriteSupport.java"
DAO_ROOT="$ROOT/owned-equipment/src/main/java"

test -f "$SOURCE"
rg -q 'static <T> T transaction\(' "$SOURCE"

if matches="$(rg -n 'setAutoCommit\(|\.commit\(\)|\.rollback\(\)' "$DAO_ROOT" || true)"; then
  if [ -n "$matches" ]; then
    printf '%s\n' "$matches" >&2
    echo 'owned equipment transaction seam: FAIL (DAO contains direct JDBC transaction control)' >&2
    exit 1
  fi
fi

count="$(rg -l 'OwnedWriteSupport\.transaction\(' "$DAO_ROOT" | wc -l | tr -d ' ')"
test "$count" -ge 5
echo "owned equipment transaction seam: PASS ($count DAO files use OwnedWriteSupport.transaction)"
