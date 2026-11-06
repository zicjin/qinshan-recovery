#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
FRAMEWORK="$ROOT/reconstituted/framework"
MAVEN_REPO="$ROOT/build/reconstituted-maven-repo"
PUBLIC_DEPS="${LIMS_PUBLIC_DEPENDENCY_DIR:-$ROOT/build/reconstituted-public-deps}"
[ -d "$PUBLIC_DEPS" ] || {
  echo "missing external public dependency directory: $PUBLIC_DEPS" >&2
  exit 2
}

python3 "$ROOT/scripts/prepare-reconstituted-maven-repo.py" \
  "$MAVEN_REPO" --overwrite \
  --public-deps "$PUBLIC_DEPS"

JAVA_HOME="$JDK" PATH="$JDK/bin:$PATH" mvn -q -f "$FRAMEWORK/pom.xml" \
  -Dmaven.repo.local="$MAVEN_REPO" \
  -Dmaven.test.skip=true clean package
python3 "$ROOT/scripts/record-reconstituted-framework-build.py" --root "$FRAMEWORK"
python3 "$ROOT/scripts/verify-reconstituted-framework-modules.py" --root "$FRAMEWORK" --compiled
