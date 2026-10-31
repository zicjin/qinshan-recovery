#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
OUT="$ROOT/owned-scheduler/target"
FRAMEWORK="$ROOT/owned-framework/target"
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
if [ "${OWNED_SKIP_DEPENDENCY_BUILD:-0}" != 1 ]; then
 JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh"
fi
CP="$FRAMEWORK/classes:$FRAMEWORK/public-deps/*"
MAIN="$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java"
SOURCES=("$MAIN")
if [ "${OWNED_SCHEDULER_RED:-0}" != 1 ]; then
 mapfile -d '' OWNED < <(find "$ROOT/owned-scheduler/src/main/java" -name '*.java' -print0 | sort -z)
 SOURCES+=("${OWNED[@]}")
fi
"$JDK/bin/javac" -parameters -source 8 -target 8 -cp "$CP" -d "$OUT/classes" "${SOURCES[@]}"
if [ "${OWNED_SCHEDULER_RED:-0}" != 1 ]; then cp -R "$ROOT/owned-scheduler/src/main/resources/." "$OUT/classes/"; fi
"$JDK/bin/javac" -source 8 -target 8 -cp "$OUT/classes:$CP" -d "$OUT/test-classes" "$ROOT/owned-scheduler/src/test/java/com/sunwayworld/owned/scheduler/OwnedSchedulerTestApplication.java"
sha256sum "$MAIN" > "$OUT/original-sources.sha256"
"$JDK/bin/jar" cf "$OUT/lims-owned-scheduler-1.0.0.jar" -C "$OUT/classes" .
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-scheduler-1.0.0.jar" "$FRAMEWORK/lims-owned-framework-1.0.0.jar" "$FRAMEWORK/public-deps"
echo "owned scheduler module build passed"
