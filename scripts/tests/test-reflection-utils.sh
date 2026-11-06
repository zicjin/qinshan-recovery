#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"; JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"; TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
mkdir -p "$TMP/refdeps"; cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/refdeps/"
python3 - "$ROOT" "$TMP/reference.jar" <<'PY'
from pathlib import Path
import hashlib,json,sys,zipfile
root,out=map(Path,sys.argv[1:]); name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'; expected=next(x['sha256'] for x in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if x['jar']==name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as z:data=z.read(name)
assert hashlib.sha256(data).hexdigest()==expected;out.write_bytes(data)
PY
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/ReflectionUtilsDifferentialTest.java"
"$JDK/bin/java" -cp "$TMP:$ROOT/owned-framework/target/public-deps/*" com.sunwayworld.framework.ReflectionUtilsDifferentialTest "$TMP/refdeps" "$TMP/reference.jar" "$ROOT/owned-framework/target/classes"
