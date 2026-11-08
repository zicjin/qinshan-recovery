#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
CONFIG="${1:-${LIMS_DISTRIBUTION_CONFIG:-}}"
if [ -z "$CONFIG" ] || [ ! -f "$CONFIG" ]; then
    echo "external configuration is required: pass a config file to start.sh" >&2
    exit 2
fi
JDK="${JAVA_HOME:?Set JAVA_HOME to pinned Java 8}"
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
