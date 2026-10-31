#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
CONFIG="${1:-${LIMS_DISTRIBUTION_CONFIG:-}}"
if [ -z "$CONFIG" ] || [ ! -f "$CONFIG" ]; then
    echo "external configuration is required: pass a config file to start.sh" >&2
    exit 2
fi
JDK="${JAVA_HOME:?Set JAVA_HOME to pinned Java 8}"
BIND_ADDRESS="${LIMS_BIND_ADDRESS:-127.0.0.1}"
exec "$JDK/bin/java" -Dserver.address="$BIND_ADDRESS" \
    -Dspring.config.location="file:$CONFIG" \
    -cp "$ROOT/app/lims-owned-application.jar:$ROOT/lib/*" \
    com.sunwayworld.emc.projectfunction.GikamWpjcApplication
