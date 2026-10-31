#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/OwnedJdbcConnectionContractTest.java"
"$JDK/bin/java" -cp "$TMP:$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" com.sunwayworld.framework.OwnedJdbcConnectionContractTest
