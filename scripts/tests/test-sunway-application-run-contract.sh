#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd -P)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-$ROOT/materials/gikam-third-czjky-14.1.5-run.jar}"
[ -x "$JDK/bin/java" ] || { echo "missing Java 8 runtime: $JDK" >&2; exit 1; }
[ -f "$RUN_JAR" ] || { echo "missing run material: $RUN_JAR" >&2; exit 1; }
"$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"; trap 'rm -rf "$tmp"' EXIT
unzip -p "$RUN_JAR" BOOT-INF/lib/gikam-core-common-14.1.5.jar > "$tmp/supplier-common.jar"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" \
  com.sunwayworld.framework.SunwayApplicationRunDifferentialTest \
  "$ROOT/owned-framework/target/public-deps" \
  "$ROOT/owned-framework/target/test-classes" \
  "$tmp/supplier-common.jar" \
  "$ROOT/owned-framework/target/classes"
echo 'test-sunway-application-run-contract: PASS'
