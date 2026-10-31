#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-$ROOT/materials/gikam-third-czjky-14.1.5-run.jar}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
python3 - "$ROOT" "$RUN_JAR" "$TMP/supplier.jar" <<'PY'
from pathlib import Path
import hashlib, json, sys, zipfile
root, run, output = map(Path, sys.argv[1:])
with zipfile.ZipFile(run) as archive:
    name = 'BOOT-INF/lib/gikam-core-common-14.1.5.jar'
    data = archive.read(name)
    expected = next(row['sha256'] for row in json.loads((root/'evidence/framework-contract/jars.json').read_text())
                    if row['jar'] == name)
    assert hashlib.sha256(data).hexdigest() == expected
    output.write_bytes(data)
PY
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" \
  com.sunwayworld.framework.DataLeavesDifferentialTest \
  "$TMP/supplier.jar" "$ROOT/owned-framework/target/classes" "$ROOT/owned-framework/target/public-deps"
echo 'test-data-leaves: PASS'
