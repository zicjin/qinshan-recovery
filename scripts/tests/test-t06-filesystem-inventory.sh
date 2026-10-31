#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPT="$ROOT/scripts/t06-filesystem-inventory.sh"
[ -x "$SCRIPT" ] || { echo "t06 filesystem test: helper is not executable" >&2; exit 1; }
bash -n "$SCRIPT"
grep -Fq -- '--production-read-only' "$SCRIPT" || { echo "t06 filesystem test: no explicit read-only flag" >&2; exit 1; }
grep -Fq -- '--storage-snapshot-id' "$SCRIPT" || { echo "t06 filesystem test: no consistency window attestation" >&2; exit 1; }
grep -Fq 'find -P' "$SCRIPT" || { echo "t06 filesystem test: no physical walk" >&2; exit 1; }
grep -Fq -- '-xdev' "$SCRIPT" || { echo "t06 filesystem test: walk may cross filesystems" >&2; exit 1; }
grep -Fq '! -L "$OUT_PARENT"' "$SCRIPT" || { echo "t06 filesystem test: output parent symlink guard missing" >&2; exit 1; }
grep -Fq -- "-printf '%s\\\\0%T@\\\\0%p\\\\0'" "$SCRIPT" || { echo "t06 filesystem test: output is not triple-NUL metadata" >&2; exit 1; }
if grep -nE '^[[:space:]]*(scp|rsync|docker|cp)[[:space:]]' "$SCRIPT"; then
  echo "t06 filesystem test: helper copies content or runs containers" >&2
  exit 1
fi
if "$SCRIPT" --production-read-only --storage-snapshot-id x --host sss >/dev/null 2>&1; then
  echo "t06 filesystem test: malformed snapshot id was accepted" >&2
  exit 1
fi
echo "t06 filesystem inventory test PASS"
