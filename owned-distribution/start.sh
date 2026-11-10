#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
CONFIG="${1:-${LIMS_DISTRIBUTION_CONFIG:-}}"
if [ -z "$CONFIG" ] || [ ! -f "$CONFIG" ]; then
    echo "external configuration is required: pass a config file to start.sh" >&2
    exit 2
fi
JDK="${JAVA_HOME:?Set JAVA_HOME to pinned Java 8}"
# Database access mode is a deployment input. Keep it in the private config so
# a plain rebuild cannot silently replace a logical-clone artifact with DB-link SQL.
schema_error() {
    echo "external-schema: $*" >&2
    exit 2
}
read_mode() {
    awk -F '[=:]' -v key="$2" '
        /^[[:space:]]*[#!]/ { next }
        { name=$1; gsub(/^[[:space:]]+|[[:space:]]+$/, "", name) }
        name==key { value=$0; sub(/^[^=:]*[=:]/, "", value);
                    sub(/^[[:space:]]+/, "", value); sub(/[[:space:]\r]+$/, "", value);
                    count++; result=value }
        END { if (count==1 && result!="") print result; else exit 1 }
    ' "$1"
}
REQUIRED_SCHEMA_MODE="$(read_mode "$CONFIG" owned.external-schema.access-mode)" ||
    schema_error "set owned.external-schema.access-mode exactly once in the runtime properties"
case "$REQUIRED_SCHEMA_MODE" in
    original-db-link|direct-schema|local-view) ;;
    *) schema_error "unsupported runtime access mode" ;;
esac
MODE_FILE="$ROOT/provenance/external-schema.properties"
[[ -f "$MODE_FILE" ]] || schema_error "package mode is missing; rebuild the distribution"
PACKAGED_SCHEMA_MODE="$(read_mode "$MODE_FILE" mode)" || schema_error "invalid packaged mode"
[[ "$REQUIRED_SCHEMA_MODE" == "$PACKAGED_SCHEMA_MODE" ]] ||
    schema_error "runtime requires $REQUIRED_SCHEMA_MODE but artifact is $PACKAGED_SCHEMA_MODE; rebuild with the matching EXTERNAL_SCHEMA_PROFILE"
CONTRACT_SHA="$(read_mode "$MODE_FILE" contract_sha256)" || schema_error "missing contract digest"
[[ "$CONTRACT_SHA" =~ ^[a-f0-9]{64}$ ]] || schema_error "invalid contract digest"
[[ -x "$JDK/bin/jar" ]] || schema_error "pinned JDK archive reader is missing"
SCHEMA_TMP="$(mktemp -d)"
trap 'rm -rf -- "$SCHEMA_TMP"' EXIT
if ! (cd "$SCHEMA_TMP" && "$JDK/bin/jar" xf "$ROOT/app/lims-owned-application.jar" \
    META-INF/lims-external-schema.json META-INF/lims-external-schema.properties); then
    schema_error "application contract is unreadable"
fi
[[ -f "$SCHEMA_TMP/META-INF/lims-external-schema.json" ]] || schema_error "application contract is missing"
cmp -s "$MODE_FILE" "$SCHEMA_TMP/META-INF/lims-external-schema.properties" ||
    schema_error "package mode differs from application contract"
ACTUAL_CONTRACT_SHA="$(sha256sum "$SCHEMA_TMP/META-INF/lims-external-schema.json")"
[[ "${ACTUAL_CONTRACT_SHA%% *}" == "$CONTRACT_SHA" ]] || schema_error "application contract digest mismatch"
rm -rf -- "$SCHEMA_TMP"
trap - EXIT
# Java 8 derives its filesystem encoding at startup from the process locale.
# The original resources include Chinese filenames; POSIX cannot resolve them.
export LANG="${LIMS_RUNTIME_LOCALE:-C.UTF-8}"
export LC_ALL="$LANG"
BIND_ADDRESS="${LIMS_BIND_ADDRESS:-127.0.0.1}"
CLASSPATH="$ROOT/app/lims-owned-application.jar"
if [ -f "$ROOT/lib/classpath.txt" ]; then
    while IFS= read -r jar_name; do
        [ -z "$jar_name" ] && continue
        CLASSPATH="$CLASSPATH:$ROOT/lib/$jar_name"
    done < "$ROOT/lib/classpath.txt"
else
    CLASSPATH="$CLASSPATH:$ROOT/lib/*"
fi
exec "$JDK/bin/java" -Dserver.address="$BIND_ADDRESS" \
    -Dspring.config.location="file:$CONFIG" \
    -cp "$CLASSPATH" \
    com.sunwayworld.emc.projectfunction.GikamWpjcApplication
