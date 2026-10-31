#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-$ROOT/materials/gikam-third-czjky-14.1.5-run.jar}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"
mkdir "$tmp/refdeps"
python3 - "$ROOT" "$RUN_JAR" "$tmp" <<'PY'
import hashlib, json, pathlib, sys, zipfile
root, run, out = map(pathlib.Path, sys.argv[1:])
expected = next(row['sha256'] for row in json.loads((root / 'evidence/framework-contract/jars.json').read_text())
                if row['jar'] == 'BOOT-INF/lib/gikam-core-common-14.1.5.jar')
with zipfile.ZipFile(run) as archive:
    data = archive.read('BOOT-INF/lib/gikam-core-common-14.1.5.jar')
assert hashlib.sha256(data).hexdigest() == expected
(out / 'supplier.jar').write_bytes(data)
PY
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/refdeps/"
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" \
  -d "$ROOT/owned-framework/target/test-classes" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/ArithUtilsDifferentialTest.java"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.ArithUtilsDifferentialTest \
  "$tmp/supplier.jar" "$tmp/refdeps" "$ROOT/owned-framework/target/classes"
