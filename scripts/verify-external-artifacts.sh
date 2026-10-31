#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ARTIFACT_ROOT="${LIMS_EXTERNAL_ARTIFACT_ROOT:-$ROOT/.external-materials}"
MANIFEST="${EXTERNAL_ARTIFACT_MANIFEST:-$ROOT/evidence/external-artifacts.sha256}"

[ -d "$ARTIFACT_ROOT" ] || { echo "external artifact root is missing: $ARTIFACT_ROOT" >&2; exit 1; }
[ -f "$MANIFEST" ] || { echo "external artifact manifest is missing: $MANIFEST" >&2; exit 1; }

failed=0
checked=0
while read -r expected relative; do
    [ -z "${expected:-}" ] && continue
    case "$expected" in \#*) continue ;; esac
    file="$ARTIFACT_ROOT/$relative"
    checked=$((checked + 1))
    if [ ! -f "$file" ]; then
        echo "FAIL missing external artifact: $relative"
        failed=1
        continue
    fi
    actual="$(sha256sum "$file" | awk '{print $1}')"
    if [ "$actual" != "$expected" ]; then
        echo "FAIL hash mismatch: $relative (expected $expected, got $actual)"
        failed=1
    else
        echo "OK $relative"
    fi
done < "$MANIFEST"

[ "$checked" -gt 0 ] || { echo "external artifact manifest is empty" >&2; exit 1; }
[ "$failed" -eq 0 ] || exit 1
echo "external artifact verification passed ($checked files)"
