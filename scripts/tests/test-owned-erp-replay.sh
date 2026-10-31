#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JAVA_HOME="$JDK" LIMS_PROJECT_ROOT="$PROJECT_ROOT" "$ROOT/scripts/build-owned-attachments.sh" >/dev/null
CP="$ROOT/owned-attachments/target/classes:$ROOT/owned-attachments/target/test-classes:$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*"
"$JDK/bin/java" -cp "$CP" OwnedErpReplayContractTest
