#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log" 2>&1 || {
  cat "$TMP/build.log" >&2
  exit 1
}
CP="$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*"
mkdir -p "$TMP/classes/templates/emc/module/mdm/equipt/equipts" "$TMP/classes/static/static/emc/module/mdm/equipt/equipts"
cp "$ROOT/project/src/main/resources/templates/emc/module/mdm/equipt/equipts/"*.html "$TMP/classes/templates/emc/module/mdm/equipt/equipts/"
cp "$ROOT/project/src/main/resources/static/emc/module/mdm/equipt/equipts/"*.js "$TMP/classes/static/static/emc/module/mdm/equipt/equipts/"
"$JDK/bin/javac" -source 8 -target 8 -cp "$CP" -d "$TMP/classes" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/controller/EmcEquiptController.java" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/equipt/equipt/controller/impl/EmcEquiptControllerImpl.java" \
  "$ROOT/owned-framework/src/test/fixtures/entrypoint/OwnedEquipmentHttpCheck.java"
"$ROOT/scripts/reject-supplier-runtime.sh" "$TMP/classes" "$ROOT/owned-framework/target/lims-owned-framework-1.0.0.jar" "$ROOT/owned-framework/target/public-deps" >"$TMP/scan.log"
(cd "$TMP" && "$JDK/bin/java" -cp "$TMP/classes:$CP" OwnedEquipmentHttpCheck) >"$TMP/http.log" 2>&1 || {
  cat "$TMP/http.log" >&2
  exit 1
}
cat "$TMP/scan.log"; cat "$TMP/http.log"
