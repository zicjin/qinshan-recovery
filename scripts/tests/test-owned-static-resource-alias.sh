#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CONFIG="$ROOT/owned-framework/src/main/java/com/sunwayworld/owned/internal/web/OwnedStaticResourceConfiguration.java"
WEB="$ROOT/owned-web/src/main/resources/static"

grep -F 'addResourceHandler("/static/**")' "$CONFIG" >/dev/null
grep -F 'addResourceHandler("/emc/**")' "$CONFIG" >/dev/null
test -f "$WEB/emc/module/mdm/emp/emps/emc-emp.js"
test -f "$WEB/emc/module/mdm/equipt/equipts/emc-equipt.js"
test -f "$WEB/emc/module/item/consume/consumes/emc-consume.js"
test -f "$ROOT/owned-web/src/main/resources/frontend-original/static/gikam/extend/css/extend.css"
test -s "$ROOT/owned-web/src/main/resources/frontend-original/static/gikam/extend/css/extend.css"
echo 'owned original static resource alias contract: PASS'
