#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)";JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}";TMP="$(mktemp -d)";trap 'rm -rf "$TMP"' EXIT
python3 - "$ROOT" "$TMP" <<'PY'
from pathlib import Path
import sys,zipfile,hashlib,json
r,o=map(Path,sys.argv[1:]);
expected=next(x['sha256'] for x in json.loads((r/'evidence/framework-contract/jars.json').read_text()) if x['jar']=='BOOT-INF/lib/gikam-core-common-14.1.5.jar')
with zipfile.ZipFile(r/'materials/gikam-third-czjky-14.1.5-run.jar') as z:
 data=z.read('BOOT-INF/lib/gikam-core-common-14.1.5.jar')
 assert hashlib.sha256(data).hexdigest()==expected,'supplier reference hash differs'
 (o/'supplier.jar').write_bytes(data)
PY
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/ConverterLeavesDifferentialTest.java"
"$JDK/bin/java" -cp "$TMP:$ROOT/owned-framework/target/classes" com.sunwayworld.framework.ConverterLeavesDifferentialTest "$TMP/supplier.jar" "$ROOT/owned-framework/target/classes"
