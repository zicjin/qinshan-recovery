#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
FRAMEWORK="$ROOT/reconstituted/framework"
MAVEN_REPO="$ROOT/build/reconstituted-maven-repo"

python3 "$ROOT/scripts/prepare-reconstituted-public-deps.py" \
  "$ROOT/build/reconstituted-public-deps" \
  --overwrite

python3 "$ROOT/scripts/prepare-reconstituted-maven-repo.py" \
  "$MAVEN_REPO" --overwrite \
  --public-deps "$ROOT/build/reconstituted-public-deps"

JAVA_HOME="$JDK" mvn -q -f "$FRAMEWORK/pom.xml" \
  -Dmaven.repo.local="$MAVEN_REPO" \
  -Dmaven.test.skip=true clean package
python3 "$ROOT/scripts/record-reconstituted-framework-build.py" --root "$FRAMEWORK"
python3 "$ROOT/scripts/verify-reconstituted-framework-modules.py" --root "$FRAMEWORK" --compiled
