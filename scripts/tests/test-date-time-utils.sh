#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-$ROOT/materials/gikam-third-czjky-14.1.5-run.jar}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
mkdir "$TMP/refdeps"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/refdeps/"
unzip -p "$RUN_JAR" BOOT-INF/lib/ojdbc8-19.3.0.0.jar > "$TMP/refdeps/ojdbc8-19.3.0.0.jar"
python3 - "$ROOT" "$RUN_JAR" "$TMP/supplier.jar" "$TMP/refdeps/ojdbc8-19.3.0.0.jar" <<'PY'
from pathlib import Path
import hashlib, json, sys, zipfile
root, run, output, ojdbc = map(Path, sys.argv[1:])
with zipfile.ZipFile(run) as archive:
    data = archive.read('BOOT-INF/lib/gikam-core-common-14.1.5.jar')
    expected = next(row['sha256'] for row in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text())
                    if row['jar'] == 'BOOT-INF/lib/gikam-core-common-14.1.5.jar')
    assert hashlib.sha256(data).hexdigest() == expected
    output.write_bytes(data)
assert hashlib.sha256(ojdbc.read_bytes()).hexdigest() == 'a66d27a14f3adee484427cc4de008af85a5c3e78e2e3285a4dba1277332978a5'
PY
for zone in UTC Asia/Shanghai America/New_York; do
  "$JDK/bin/java" -Duser.timezone="$zone" -cp "$ROOT/owned-framework/target/test-classes" \
    com.sunwayworld.framework.DateTimeUtilsDifferentialTest \
    "$TMP/supplier.jar" "$ROOT/owned-framework/target/classes" "$TMP/refdeps" "$ROOT/owned-framework/target/public-deps"
done
echo 'test-date-time-utils: PASS'
