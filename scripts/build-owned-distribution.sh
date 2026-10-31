#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LIMS_PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JAVA_HOME="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
export LIMS_PROJECT_ROOT JAVA_HOME
OUT="$ROOT/owned-distribution/target"
mkdir -p "$OUT"
BUILD_STARTED_NS="$(date +%s%N)"
exec 9>"$OUT/.build.lock"
flock 9
FINGERPRINT_FILE="$OUT/.distribution-build-fingerprint"
PROJECT_DIRTY=0
PROJECT_STATUS=""
if [ -d "$LIMS_PROJECT_ROOT" ]; then
    if [ -d "$LIMS_PROJECT_ROOT/.git" ] || [ -f "$LIMS_PROJECT_ROOT/.git" ]; then
        PROJECT_STATUS="$(git -C "$LIMS_PROJECT_ROOT" status --porcelain --untracked-files=all)"
    else
        # In standalone mode project/ is ordinary source under the root Git
        # repository.  Do not require a nested .git directory.
        PROJECT_STATUS="$(git -C "$ROOT" status --porcelain --untracked-files=all -- project)"
    fi
fi
if [ -n "$PROJECT_STATUS" ]; then
    # A dirty nested project may contain changed or untracked files whose
    # content is consumed by the module builders.  Never reuse a clean cache
    # for that working tree; the owned-source hash below still identifies the
    # rest of the build inputs for diagnostics.
    PROJECT_DIRTY=1
fi
hash_source_inputs() {
    (
        cd "$ROOT"
        {
            local project_path="${LIMS_PROJECT_ROOT#"$ROOT"/}"
            find "$project_path/src" -type f -print0 2>/dev/null || true
            printf '%s\0' "$project_path/pom.xml"
            find owned-framework/src owned-personnel/src owned-equipment/src \
                owned-consumables/src owned-attachments/src owned-report/src \
                owned-scheduler/src owned-web/src -type f -print0
            printf '%s\0' scripts/build-owned-*.sh owned-web/build-owned-web.sh \
                owned-distribution/package.py owned-distribution/start.sh
        } | sort -z | xargs -0 sha256sum
    )
}
fingerprint_input() {
    local project_head
    project_head="$(PYTHONPATH="$ROOT/scripts" python3 -c 'from source_binding import nested_project_commit; print(nested_project_commit())')"
    {
        printf 'project=%s\n' "$project_head"
        printf 'project_dirty=%s\n' "$PROJECT_DIRTY"
        printf 'java=%s\n' "$JAVA_HOME"
        "$JAVA_HOME/bin/java" -version 2>&1 | head -1 || true
        hash_source_inputs | sha256sum
    }
}
BUILD_KEY="$(fingerprint_input | sha256sum | awk '{print $1}')"
CACHED_DISTRIBUTION="$OUT/$BUILD_KEY/distribution"
activate_cached_distribution() {
    rm -rf "$OUT/distribution"
    ln -s "$BUILD_KEY/distribution" "$OUT/distribution"
}
if [ "${OWNED_DISTRIBUTION_REUSE:-0}" = "1" ] \
        && [ "$PROJECT_DIRTY" = 0 ] \
        && [ -f "$FINGERPRINT_FILE" ] \
        && [ "$(cat "$FINGERPRINT_FILE")" = "$BUILD_KEY" ] \
        && [ -f "$CACHED_DISTRIBUTION/manifest.json" ] \
        && [ -f "$CACHED_DISTRIBUTION/app/lims-owned-application.jar" ]; then
    activate_cached_distribution
    BUILD_ELAPSED_MS=$(( ( $(date +%s%N) - BUILD_STARTED_NS ) / 1000000 ))
    echo "owned distribution build reused: $CACHED_DISTRIBUTION elapsed_ms=$BUILD_ELAPSED_MS"
    exit 0
fi
"$ROOT/scripts/build-owned-framework.sh"
for module in framework personnel equipment consumables attachments report scheduler; do
    [ "$module" = framework ] && continue
    OWNED_SKIP_DEPENDENCY_BUILD=1 "$ROOT/scripts/build-owned-$module.sh"
done
"$ROOT/owned-web/build-owned-web.sh"
if [ "${OWNED_DISTRIBUTION_REUSE:-0}" = "1" ]; then
    if [ -e "$OUT/$BUILD_KEY" ]; then
        chmod -R u+w "$OUT/$BUILD_KEY" || true
    fi
    rm -rf "$OUT/$BUILD_KEY"
    mkdir -p "$CACHED_DISTRIBUTION"
    PACKAGE_OUTPUT="$CACHED_DISTRIBUTION"
else
    rm -rf "$OUT/distribution"
    mkdir -p "$OUT/distribution"
    PACKAGE_OUTPUT="$OUT/distribution"
fi
mkdir -p "$PACKAGE_OUTPUT/app" "$PACKAGE_OUTPUT/lib" "$PACKAGE_OUTPUT/modules" "$PACKAGE_OUTPUT/example"
cp "$ROOT/owned-distribution/start.sh" "$PACKAGE_OUTPUT/start.sh"
chmod 0755 "$PACKAGE_OUTPUT/start.sh"
cp "$ROOT/owned-distribution/example/owned-application.properties" "$PACKAGE_OUTPUT/example/"
python3 "$ROOT/owned-distribution/package.py" "$PACKAGE_OUTPUT" \
    "$ROOT/owned-framework/target/lims-owned-framework-1.0.0.jar" \
    "$ROOT/owned-personnel/target/lims-owned-personnel-1.0.0.jar" \
    "$ROOT/owned-equipment/target/lims-owned-equipment-1.0.0.jar" \
    "$ROOT/owned-consumables/target/lims-owned-consumables-1.0.0.jar" \
    "$ROOT/owned-attachments/target/lims-owned-attachments-1.0.0.jar" \
    "$ROOT/owned-report/target/lims-owned-report-1.0.0.jar" \
    "$ROOT/owned-scheduler/target/lims-owned-scheduler-1.0.0.jar" \
    "$ROOT/owned-web/target/lims-owned-web-1.0.0.jar"
"$ROOT/scripts/reject-supplier-runtime.sh" "$PACKAGE_OUTPUT/app/lims-owned-application.jar" \
    "$PACKAGE_OUTPUT/lib" "$PACKAGE_OUTPUT/modules"
if [ "${OWNED_DISTRIBUTION_REUSE:-0}" = "1" ]; then
    # Cached packages are read-only inputs for browser/application shards.
    chmod -R a-w "$OUT/$BUILD_KEY"
    activate_cached_distribution
    echo "$BUILD_KEY" > "$FINGERPRINT_FILE"
    BUILD_ELAPSED_MS=$(( ( $(date +%s%N) - BUILD_STARTED_NS ) / 1000000 ))
    echo "owned distribution build passed: $CACHED_DISTRIBUTION elapsed_ms=$BUILD_ELAPSED_MS"
else
    echo "$BUILD_KEY" > "$FINGERPRINT_FILE"
    BUILD_ELAPSED_MS=$(( ( $(date +%s%N) - BUILD_STARTED_NS ) / 1000000 ))
    echo "owned distribution build passed: $OUT/distribution elapsed_ms=$BUILD_ELAPSED_MS"
fi
