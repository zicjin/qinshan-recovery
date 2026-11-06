#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST="${1:-${DIST:-}}"
if [ -z "$DIST" ]; then
  echo "usage: $0 <previous-owned-distribution>" >&2
  exit 2
fi
if [ ! -f "$DIST/manifest.json" ] || [ ! -f "$DIST/app/lims-owned-application.jar" ]; then
  echo "previous distribution is incomplete: $DIST" >&2
  exit 2
fi

python3 "$ROOT/scripts/verify-source-only-checkpoint.py" --distribution "$DIST" >/dev/null
DIST="$DIST" bash "$ROOT/scripts/refresh-owned-sss2-app.sh"
