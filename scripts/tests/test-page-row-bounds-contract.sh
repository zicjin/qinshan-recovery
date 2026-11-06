#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVAC_BIN="$JDK/bin/javac" JAR_BIN="$JDK/bin/jar" JAVA_BIN="$JDK/bin/java" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/PageRowBoundsDifferentialTest.java"
mkdir -p "$TMP/supplier"
python3 - "$ROOT" "$TMP" <<'PY'
import hashlib,json,pathlib,sys,zipfile,shutil
root=pathlib.Path(sys.argv[1]);out=pathlib.Path(sys.argv[2])/'supplier';ids={r['jar']:r['sha256'] for r in json.loads((root.parent/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text())}
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as z:
 n='BOOT-INF/lib/gikam-core-common-14.1.5.jar';data=z.read(n);assert hashlib.sha256(data).hexdigest()==ids[n];
 (out/'common.jar').write_bytes(data)
 for name in ('ojdbc8-19.3.0.0.jar',):
  data=z.read('BOOT-INF/lib/'+name)
  assert hashlib.sha256(data).hexdigest()=='a66d27a14f3adee484427cc4de008af85a5c3e78e2e3285a4dba1277332978a5'
  (out/name).write_bytes(data)
for path in (root/'owned-framework/target/public-deps').glob('*.jar'):
 shutil.copyfile(path,out/path.name)
PY
"$JDK/bin/java" -cp "$TMP:$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.PageRowBoundsDifferentialTest "$TMP/supplier" "$ROOT/owned-framework/target/public-deps" "$ROOT/owned-framework/target/classes"
echo 'test-page-row-bounds-contract: PASS'
