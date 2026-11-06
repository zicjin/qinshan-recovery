#!/usr/bin/env bash
# Compile and run the standalone payload-policy regression harness against the
# recovered application classes and production-pinned dependency set.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JAVA_HOME="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TEST_SOURCE="$ROOT/scripts/tests/FileQueryGuardTest.java"
TARGET_CLASSES="$ROOT/build/source-only-application/classes"

[ -x "$JAVA_HOME/bin/javac" ] || { echo "missing recovered JDK" >&2; exit 1; }
[ -d "$TARGET_CLASSES" ] || { echo "missing source-only classes; run scripts/build-owned-distribution.sh first" >&2; exit 1; }

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
classpath="$TARGET_CLASSES:$(find "$ROOT/reconstituted/framework" "$ROOT/build/reconstituted-public-deps" -type f -name '*.jar' -print | paste -sd: -)"

"$JAVA_HOME/bin/javac" -cp "$classpath" -d "$tmp" "$TEST_SOURCE"
"$JAVA_HOME/bin/java" -cp "$tmp:$classpath" com.sunwayworld.emc.framework.security.FileQueryGuardTest
