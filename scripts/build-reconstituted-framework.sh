#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
FRAMEWORK="$ROOT/reconstituted/framework"
MAVEN_REPO="$ROOT/build/reconstituted-maven-repo"
PUBLIC_DEPS="${LIMS_PUBLIC_DEPENDENCY_DIR:-$ROOT/build/reconstituted-public-deps}"
STAGED_PUBLIC_DEPS="$ROOT/build/reconstituted-public-deps"
ARTIFACT_ROOT="${LIMS_EXTERNAL_ARTIFACT_ROOT:-$ROOT/.external-materials}"
PLUGIN_SEED="${MAVEN_PLUGIN_SEED:-$ARTIFACT_ROOT/maven-build-plugin-seed.tar.gz}"
[ -d "$PUBLIC_DEPS" ] || {
  echo "missing external public dependency directory: $PUBLIC_DEPS" >&2
  exit 2
}
mkdir -p "$ROOT/build/tmp"
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djava.io.tmpdir=$ROOT/build/tmp"

python3 "$ROOT/scripts/stage-source-only-public-deps.py" \
  --input "$PUBLIC_DEPS" \
  --output "$STAGED_PUBLIC_DEPS"

python3 "$ROOT/scripts/prepare-reconstituted-maven-repo.py" \
  "$MAVEN_REPO" --overwrite \
  --seed "$PLUGIN_SEED" \
  --manifest "$ROOT/build-support/t02-build-plugin-seed.sha256" \
  --public-deps "$STAGED_PUBLIC_DEPS"

MAVEN_OPTS="${MAVEN_OPTS:-} -Djava.io.tmpdir=$ROOT/build/tmp" \
JAVA_HOME="$JDK" PATH="$JDK/bin:$PATH" mvn -q -f "$FRAMEWORK/pom.xml" \
  -Dmaven.repo.local="$MAVEN_REPO" \
  -Dmaven.test.skip=true clean package
python3 "$ROOT/scripts/record-reconstituted-framework-build.py" --root "$FRAMEWORK"
python3 "$ROOT/scripts/verify-reconstituted-framework-modules.py" --root "$FRAMEWORK" --compiled
