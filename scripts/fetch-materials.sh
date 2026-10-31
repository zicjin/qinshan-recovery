#!/usr/bin/env bash
# Read-only supplier artifact capture. Outputs stay outside the standalone Git
# tree; native/license/private inputs are deliberately not copied.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_HOST="${SRC_HOST:-sss}"
SRC_DIR="${SRC_DIR:-/data/LIMS/jiankeyuan/lims-czjky-emc}"
SRC_JDK="${SRC_JDK:-/usr/java/jdk-8u391-linux-x64.tar.gz}"
ARTIFACT_ROOT="${LIMS_EXTERNAL_ARTIFACT_ROOT:-$ROOT/.external-materials}"

mkdir -p "$ARTIFACT_ROOT"
pull() {
  local src="$1" dst="$2"
  echo "[fetch] $src -> ${dst#"$ARTIFACT_ROOT"/}"
  rsync -av --partial "$SRC_HOST:$src" "$dst"
}

pull "$SRC_DIR/gikam-third-czjky-14.1.5-run.jar" "$ARTIFACT_ROOT/gikam-third-czjky-14.1.5-run.jar"
pull "$SRC_JDK" "$ARTIFACT_ROOT/jdk-8u391-linux-x64.tar.gz"
pull "$SRC_DIR/simsun.ttc" "$ARTIFACT_ROOT/simsun.ttc"
pull "$SRC_DIR/log4j2.xml" "$ARTIFACT_ROOT/log4j2.xml"

echo "[fetch] $SRC_DIR/index/ -> external artifact index/"
mkdir -p "$ARTIFACT_ROOT/index"
rsync -av --partial "$SRC_HOST:$SRC_DIR/index/" "$ARTIFACT_ROOT/index/"

echo "[fetch] native/license/private inputs were not copied; record approved remote hashes separately."
echo "[fetch] complete; run scripts/verify-external-artifacts.sh after updating the approved manifest."
