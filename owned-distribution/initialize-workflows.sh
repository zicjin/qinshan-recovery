#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
CONFIG="${1:-}"
[[ "$#" == 1 && -f "$CONFIG" ]] || { echo 'usage: initialize-workflows.sh /private/runtime.properties < /private/workflows.json' >&2; exit 2; }
JDK="${JAVA_HOME:?Set JAVA_HOME to pinned Java 8}"
CLASSPATH="$ROOT/app/lims-owned-application.jar"
MANIFEST="$ROOT/installation/deployment/oracle/workflow/manifest.json"
[[ -f "$ROOT/lib/classpath.txt" && -f "$MANIFEST" ]] || { echo 'packaged workflow installation inputs are missing' >&2; exit 2; }
while IFS= read -r entry; do
  [[ -z "$entry" ]] || CLASSPATH="$CLASSPATH:$ROOT/lib/$entry"
done < "$ROOT/lib/classpath.txt"
exec "$JDK/bin/java" -Dfile.encoding=UTF-8 -cp "$CLASSPATH" com.sunwayworld.owned.install.WorkflowInstaller "$CONFIG" "$MANIFEST"
