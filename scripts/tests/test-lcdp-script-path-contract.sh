#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
PUBLIC_DEPS="${LIMS_PUBLIC_DEPENDENCY_DIR:-/tmp/lims-public-deps-verify}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

"$JDK/bin/javac" -source 8 -target 8 \
  -cp "$PUBLIC_DEPS/*" \
  -d "$TMP" \
  "$ROOT/owned-framework/src/main/java/com/sunwayworld/framework/exception/checked/CheckedException.java" \
  "$ROOT/reconstituted/framework/owned-core-lcdp/src/main/java/com/sunwayworld/cloud/module/lcdp/resource/support/LcdpScriptPath.java" \
  "$ROOT/scripts/tests/fixtures/lcdp/LcdpScriptPathContractTest.java"

"$JDK/bin/java" -cp "$TMP:$PUBLIC_DEPS/*" \
  com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptPathContractTest
