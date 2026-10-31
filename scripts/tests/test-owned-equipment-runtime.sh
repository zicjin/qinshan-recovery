#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-equipment.sh"
if [ "${1:-}" = "--build-only" ]; then echo "owned equipment runtime compile passed"; exit 0; fi
