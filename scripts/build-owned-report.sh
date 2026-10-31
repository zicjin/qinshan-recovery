#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
OUT="$ROOT/owned-report/target"
FRAMEWORK="$ROOT/owned-framework/target"
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
if [ "${OWNED_SKIP_DEPENDENCY_BUILD:-0}" != 1 ]; then
  JAVA_HOME="$JDK" LIMS_PROJECT_ROOT="$PROJECT_ROOT" "$ROOT/scripts/build-owned-personnel.sh"
  "$ROOT/owned-web/build-owned-web.sh"
fi
CP="$FRAMEWORK/classes:$FRAMEWORK/public-deps/*"
JAVA_SOURCES=("$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java")
OWNED_SOURCES=()
while IFS= read -r -d '' source; do OWNED_SOURCES+=("$source"); done < <(find "$ROOT/owned-report/src/main/java" -name '*.java' -print0 | sort -z)
"$JDK/bin/javac" -encoding UTF-8 -parameters -source 8 -target 8 -cp "$CP" -d "$OUT/classes" "${JAVA_SOURCES[@]}" "${OWNED_SOURCES[@]}"
cp -R "$ROOT/owned-report/src/main/resources/." "$OUT/classes/"
find "$ROOT/owned-report/src/test" -name '*.java' -print0 | xargs -0 "$JDK/bin/javac" -encoding UTF-8 -source 8 -target 8 -cp "$OUT/classes:$FRAMEWORK/classes:$FRAMEWORK/public-deps/*:$ROOT/owned-personnel/target/classes" -d "$OUT/test-classes"
"$JDK/bin/jar" cf "$OUT/lims-owned-report-1.0.0.jar" -C "$OUT/classes" .
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-report-1.0.0.jar" "$FRAMEWORK/lims-owned-framework-1.0.0.jar" "$FRAMEWORK/public-deps"
echo "owned report main/runtime module build passed"
