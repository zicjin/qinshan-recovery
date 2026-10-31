#!/usr/bin/env bash
# Collect the known sample's metadata using SELECT only. The verified file must
# already be present under private/t06-attachment-sample/files/ (see t06 evidence).
# Requires Java 11+ for source-file execution; production driver comes from run.jar.
set -euo pipefail
umask 077
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR_TOOL="$ROOT/build/jdk1.8.0_391/bin/jar"
LIB="$ROOT/tools/db-probe/lib"
DRIVER="$LIB/BOOT-INF/lib/ojdbc8-19.3.0.0.jar"
[ -x "$JAR_TOOL" ] || { echo "先运行 scripts/build-prep.sh" >&2; exit 1; }
if [ ! -f "$DRIVER" ]; then
  mkdir -p "$LIB"
  (
    cd "$LIB"
    "$JAR_TOOL" xf "$ROOT/materials/gikam-third-czjky-14.1.5-run.jar" \
      BOOT-INF/lib/ojdbc8-19.3.0.0.jar
  )
fi
java -cp "$DRIVER" "$ROOT/tools/db-probe/AttachmentSample.java" "$ROOT"
