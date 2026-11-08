#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT
mkdir -p "$WORK/jdk/bin"
cat > "$WORK/jdk/bin/java" <<'JAVA'
#!/usr/bin/env bash
printf '%s|%s' "$LANG" "$LC_ALL"
JAVA
chmod +x "$WORK/jdk/bin/java"
: > "$WORK/runtime.properties"
actual="$(env LANG=C LC_ALL=C JAVA_HOME="$WORK/jdk" bash "$ROOT/owned-distribution/start.sh" "$WORK/runtime.properties")"
[ "$actual" = 'C.UTF-8|C.UTF-8' ] || { echo 'distribution must select UTF-8 filename locale'; exit 1; }
actual="$(env LANG=C LC_ALL=C LIMS_RUNTIME_LOCALE=C JAVA_HOME="$WORK/jdk" bash "$ROOT/owned-distribution/start.sh" "$WORK/runtime.properties")"
[ "$actual" = 'C|C' ]
echo 'distribution filename locale contract: PASS'
