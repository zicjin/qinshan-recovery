#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PROJECT="$ROOT/project/src/main/resources/static"
test -f "$PROJECT/emc/config/emc-choose-config.js"
test -f "$PROJECT/emc/config/emc-select-config.js"
OUT="$(mktemp -d -t owned-web-default.XXXXXX)"
trap 'rm -rf "$OUT"' EXIT
bash "$ROOT/owned-web/build-owned-web.sh" "$OUT" >/dev/null
test -f "$OUT/static/gikam/main/js/home.js"
test -f "$OUT/static/gikam/main/js/workspace.js"
test -s "$OUT/static/gikam/main/js/gikam.js"
for panel in download-attachment report notice-panel todo-panel todo-overdue; do
  test -s "$OUT/static/workspace-panels/$panel.html"
done
test -s "$OUT/static/gikam/extend/css/extend.css"
test ! -e "$OUT/static/owned-web/app.js"
test ! -e "$OUT/static/owned-web/owned.css"
test ! -e "$OUT/static/owned-web/owned-gikam.js"
grep -Fq 'original-supplier-runtime-bundle-import' "$OUT/manifest.json"
echo 'owned Gikam entrypoint contract: PASS (original supplier frontend is default; simplified shell excluded)'
