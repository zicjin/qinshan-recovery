#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
python3 - "$ROOT" "$TMP/supplier.jar" <<'PY'
import hashlib,json,pathlib,sys,zipfile
root,out=map(pathlib.Path,sys.argv[1:])
name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected=next(row['sha256'] for row in json.loads((root/'evidence/framework-contract/jars.json').read_text()) if row['jar']==name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive:
    content=archive.read(name)
    assert hashlib.sha256(content).hexdigest()==expected
    out.write_bytes(content)
PY
CP="$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes:$ROOT/owned-framework/target/public-deps/*"
"$JDK/bin/java" -cp "$CP" com.sunwayworld.framework.PageRequestContractTest
"$JDK/bin/java" -cp "$CP" com.sunwayworld.framework.ReviewedValueContractTest
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.PageRequestDifferentialTest "$ROOT/owned-framework/target/public-deps" "$TMP/supplier.jar" "$ROOT/owned-framework/target/classes"
