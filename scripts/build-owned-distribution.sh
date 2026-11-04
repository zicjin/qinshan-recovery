#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LIMS_PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JAVA_HOME="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
export LIMS_PROJECT_ROOT JAVA_HOME

FRAMEWORK="$ROOT/reconstituted/framework"
PUBLIC_DEPS="$ROOT/build/reconstituted-public-deps"
APPLICATION_DIR="$ROOT/build/source-only-application"
OUT="$ROOT/owned-distribution/target"
mkdir -p "$OUT"

BUILD_STARTED_NS="$(date +%s%N)"
exec 9>"$OUT/.build.lock"
flock 9

source_hash() {
  (
    cd "$ROOT"
    {
      find project/src reconstituted/overrides scripts -type f \
        ! -path 'scripts/__pycache__/*' ! -name '*.pyc' -print0
      printf '%s\0' project/pom.xml reconstituted/framework/pom.xml
      printf '%s\0' owned-distribution/package.py owned-distribution/start.sh
      printf '%s\0' owned-distribution/example/owned-application.properties
    } | sort -z | xargs -0 sha256sum
  )
}

BUILD_KEY="$({
  printf 'mode=source-only-reconstituted\n'
  "$JAVA_HOME/bin/java" -version 2>&1 | head -1
  source_hash | sha256sum
} | sha256sum | awk '{print $1}')"
FINGERPRINT_FILE="$OUT/.distribution-build-fingerprint"
CACHED_DISTRIBUTION="$OUT/$BUILD_KEY/distribution"

activate_cached_distribution() {
  rm -rf "$OUT/distribution"
  ln -s "$BUILD_KEY/distribution" "$OUT/distribution"
}

if [ "${OWNED_DISTRIBUTION_REUSE:-0}" = "1" ] \
    && [ -f "$FINGERPRINT_FILE" ] \
    && [ "$(cat "$FINGERPRINT_FILE")" = "$BUILD_KEY" ] \
    && [ -f "$CACHED_DISTRIBUTION/manifest.json" ] \
    && [ -f "$CACHED_DISTRIBUTION/app/lims-owned-application.jar" ]; then
  activate_cached_distribution
  ELAPSED_MS=$(( ( $(date +%s%N) - BUILD_STARTED_NS ) / 1000000 ))
  echo "owned source-only distribution build reused: $CACHED_DISTRIBUTION elapsed_ms=$ELAPSED_MS"
  exit 0
fi

bash "$ROOT/scripts/build-reconstituted-framework.sh"
python3 "$ROOT/scripts/build-source-only-application.py" \
  > "$APPLICATION_DIR/build-output.json"

if [ "${OWNED_DISTRIBUTION_REUSE:-0}" = "1" ]; then
  if [ -e "$OUT/$BUILD_KEY" ]; then
    chmod -R u+w "$OUT/$BUILD_KEY" || true
  fi
  rm -rf "$OUT/$BUILD_KEY"
  PACKAGE_OUTPUT="$CACHED_DISTRIBUTION"
else
  rm -rf "$OUT/distribution"
  PACKAGE_OUTPUT="$OUT/distribution"
fi
mkdir -p "$PACKAGE_OUTPUT/app" "$PACKAGE_OUTPUT/lib" \
  "$PACKAGE_OUTPUT/modules" "$PACKAGE_OUTPUT/example" "$PACKAGE_OUTPUT/provenance"
cp "$ROOT/owned-distribution/start.sh" "$PACKAGE_OUTPUT/start.sh"
chmod 0755 "$PACKAGE_OUTPUT/start.sh"
cp "$ROOT/owned-distribution/example/owned-application.properties" "$PACKAGE_OUTPUT/example/"
cp "$APPLICATION_DIR/compile-report.json" "$PACKAGE_OUTPUT/provenance/source-only-application.json"
cp "$ROOT/docs/reconstitution/framework-module-registry.json" "$PACKAGE_OUTPUT/provenance/framework-module-registry.json"
cp "$ROOT/reconstituted/framework/public-dependency-allowlist.json" "$PACKAGE_OUTPUT/provenance/public-dependency-allowlist.json"

mapfile -t FRAMEWORK_JARS < <(
  jq -r '.modules[].output_jar' "$ROOT/docs/reconstitution/framework-module-registry.json" |
    sed "s#^#$FRAMEWORK/#"
)

OWNED_PUBLIC_DEPENDENCY_DIR="$PUBLIC_DEPS" \
OWNED_ADDITIONAL_DEPENDENCY_DIRS="$APPLICATION_DIR/third-party-deps" \
OWNED_THIRD_PARTY_DEPENDENCY_PROVENANCE="$APPLICATION_DIR/third-party-dependencies.json" \
OWNED_DISTRIBUTION_MODE="source-only-reconstituted" \
python3 "$ROOT/owned-distribution/package.py" "$PACKAGE_OUTPUT" \
  "$APPLICATION_DIR/lims-owned-application.jar" \
  "${FRAMEWORK_JARS[@]}"

"$ROOT/scripts/reject-supplier-runtime.sh" \
  "$PACKAGE_OUTPUT/app/lims-owned-application.jar" \
  "$PACKAGE_OUTPUT/lib" "$PACKAGE_OUTPUT/modules"

if [ "${OWNED_DISTRIBUTION_REUSE:-0}" = "1" ]; then
  chmod -R a-w "$OUT/$BUILD_KEY"
  activate_cached_distribution
  echo "$BUILD_KEY" > "$FINGERPRINT_FILE"
else
  echo "$BUILD_KEY" > "$FINGERPRINT_FILE"
fi

ELAPSED_MS=$(( ( $(date +%s%N) - BUILD_STARTED_NS ) / 1000000 ))
echo "owned source-only distribution build passed: $PACKAGE_OUTPUT elapsed_ms=$ELAPSED_MS"
