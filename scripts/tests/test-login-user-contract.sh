#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
"$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
python3 - "$ROOT" "$TMP" <<'PY'
import hashlib, json, pathlib, sys, zipfile
root, output = map(pathlib.Path, sys.argv[1:])
name = 'BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected = next(row['sha256'] for row in json.loads((root/'evidence/framework-contract/jars.json').read_text()) if row['jar'] == name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive:
    data = archive.read(name)
    assert hashlib.sha256(data).hexdigest() == expected, 'supplier reference hash differs'
    (output/'supplier.jar').write_bytes(data)
PY
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.LoginUserDifferentialTest \
  "$ROOT/owned-framework/target/public-deps" "$TMP/supplier.jar" "$ROOT/owned-framework/target/classes"
echo 'test-login-user-contract: PASS'
