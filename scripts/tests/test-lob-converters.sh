#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)";JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}";TMP="$(mktemp -d)";trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
mkdir -p "$TMP/refdeps";cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/refdeps/"
python3 - "$ROOT" "$TMP/refdeps" <<'PY'
from pathlib import Path
import hashlib,json,sys,zipfile
r,o=map(Path,sys.argv[1:]); rows=json.loads((r/'evidence/framework-contract/jars.json').read_text()); extras={'mssql-jdbc-7.4.1.jre8.jar':'00a6eaf49184dbffd89c4baf21d08a471797ead9bf48ca38faf7f9fe4244e391','commons-io-2.4.jar':'cc6a41dc3eaacc9e440a6bd0d2890b20d36b4ee408fe2d67122f328bb6e01581'}
with zipfile.ZipFile(r/'materials/gikam-third-czjky-14.1.5-run.jar') as z:
    for dep in ('gikam-core-common-14.1.5.jar','mssql-jdbc-7.4.1.jre8.jar','commons-io-2.4.jar'):
        name='BOOT-INF/lib/'+dep; expected=extras.get(dep) or next(x['sha256'] for x in rows if x['jar']==name)
        b=z.read(name); assert hashlib.sha256(b).hexdigest()==expected, dep+' hash mismatch'; (o/dep).write_bytes(b)
PY
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/LobConverterDifferentialTest.java"
"$JDK/bin/java" -cp "$TMP" com.sunwayworld.framework.LobConverterDifferentialTest "$TMP/refdeps" "$ROOT/owned-framework/target/classes"
