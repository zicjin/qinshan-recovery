#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TASK_TMP="$(mktemp -d)"
trap 'python3 -c "import shutil,sys; shutil.rmtree(sys.argv[1])" "$TASK_TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >"$TASK_TMP/build.log" 2>&1 || {
  cat "$TASK_TMP/build.log" >&2
  exit 1
}
CP="$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*"
mkdir -p "$TASK_TMP/classes/templates/emc/module/mdm/emp/emps" "$TASK_TMP/classes/static/static/emc/module/mdm/emp/emps"
cp "$ROOT/project/src/main/resources/templates/emc/module/mdm/emp/emps/"*.html "$TASK_TMP/classes/templates/emc/module/mdm/emp/emps/"
cp "$ROOT/project/src/main/resources/static/emc/module/mdm/emp/emps/"*.js "$TASK_TMP/classes/static/static/emc/module/mdm/emp/emps/"
"$JDK/bin/javac" -source 8 -target 8 -cp "$CP" -d "$TASK_TMP/classes" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/controller/EmcEmpController.java" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/controller/impl/EmcEmpControllerImpl.java" \
  "$ROOT/owned-framework/src/test/fixtures/entrypoint/OwnedPersonnelHttpCheck.java"
"$ROOT/scripts/reject-supplier-runtime.sh" "$TASK_TMP/classes" "$ROOT/owned-framework/target/lims-owned-framework-1.0.0.jar" "$ROOT/owned-framework/target/public-deps" >"$TASK_TMP/scan.log"
(cd "$TASK_TMP" && "$JDK/bin/java" -cp "$TASK_TMP/classes:$CP" OwnedPersonnelHttpCheck) >"$TASK_TMP/http.log" 2>&1 || {
  cat "$TASK_TMP/http.log" >&2
  exit 1
}
cat "$TASK_TMP/scan.log"
cat "$TASK_TMP/http.log"
