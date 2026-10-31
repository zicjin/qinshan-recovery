#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
"$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
unzip -p "$ROOT/materials/gikam-third-czjky-14.1.5-run.jar" BOOT-INF/lib/gikam-core-common-14.1.5.jar >"$TMP/supplier.jar"
python3 - "$ROOT" "$TMP/supplier.jar" <<'PY'
import hashlib, json, pathlib, sys
root, supplier = map(pathlib.Path, sys.argv[1:])
expected = next(x['sha256'] for x in json.loads((root/'evidence/framework-contract/jars.json').read_text()) if x['jar'] == 'BOOT-INF/lib/gikam-core-common-14.1.5.jar')
assert hashlib.sha256(supplier.read_bytes()).hexdigest() == expected, 'supplier reference hash differs'
PY
mkdir -p "$TMP/fixture/com/sunwayworld/framework"
cp "$ROOT/owned-framework/target/test-classes/com/sunwayworld/framework/MapperValueContractTest\$StringLengthConverter.class" "$TMP/fixture/com/sunwayworld/framework/"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.MapperValueContractTest \
  "$TMP" "$ROOT/owned-framework/target/public-deps" "$ROOT/owned-framework/target/classes" "$TMP/fixture"
echo 'test-mapper-value-contract: PASS'
