#!/usr/bin/env bash
# Differential contract test for MatchPattern, FilterParamPattern and SearchFilter.
# The supplier common JAR is extracted read-only from the recorded run material
# and loaded only by the test's isolated classloader.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-$ROOT/materials/gikam-third-czjky-14.1.5-run.jar}"
[ -f "$RUN_JAR" ] || { echo "missing run material: $RUN_JAR" >&2; exit 1; }
"$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
unzip -p "$RUN_JAR" BOOT-INF/lib/gikam-core-common-14.1.5.jar >"$tmp/gikam-core-common-14.1.5.jar"
# Reference runtime needs public libraries used by the original conversion
# helpers. Extract only those already pinned by the owned build.
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/"
# Original converter registration references the Oracle public Datum types.
unzip -p "$RUN_JAR" BOOT-INF/lib/ojdbc8-19.3.0.0.jar >"$tmp/ojdbc8-19.3.0.0.jar"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" \
  com.sunwayworld.framework.SearchFilterDifferentialTest \
  "$tmp" "$ROOT/owned-framework/target/public-deps" \
  "$ROOT/owned-framework/target/classes"
