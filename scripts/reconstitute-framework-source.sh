#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RESOLVER="$ROOT/scripts/resolve-java-source-closure.py"

usage() {
  cat <<'EOF'
Usage:
  reconstitute-framework-source.sh inventory --output-dir DIR
  reconstitute-framework-source.sh pilot \
    --target-source PROJECT_JAVA_FILE \
    [--seed-class FQN]... \
    --output-dir DIR

External read-only inputs:
  SUPPLIER_RUN_JAR       default: .external-materials/gikam-third-czjky-14.1.5-run.jar
  VINEFLOWER_JAR         optional CFR compile-failure fallback
  VINEFLOWER_JAVA        default: java (Vineflower 1.11 requires Java 17+)
  LIMS_JDK8_HOME         default: build/jdk1.8.0_391

Generated output is not canonical evidence. Use an isolated work directory.
EOF
}

if [[ $# -eq 0 || "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

command_name="$1"
shift
case "$command_name" in
  inventory|pilot)
    ;;
  *)
    usage >&2
    exit 2
    ;;
esac

run_jar="${SUPPLIER_RUN_JAR:-$ROOT/.external-materials/gikam-third-czjky-14.1.5-run.jar}"
jdk8="${LIMS_JDK8_HOME:-$ROOT/build/jdk1.8.0_391}"
vineflower_args=()
if [[ -n "${VINEFLOWER_JAR:-}" ]]; then
  vineflower_args+=(--vineflower-jar "$VINEFLOWER_JAR")
fi

exec python3 "$RESOLVER" "$command_name" \
  --run-jar "$run_jar" \
  --java8 "$jdk8/bin/java" \
  --javac "$jdk8/bin/javac" \
  "${vineflower_args[@]}" \
  "$@"
