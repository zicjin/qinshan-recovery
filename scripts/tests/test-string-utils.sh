#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"; JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"; TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
mkdir -p "$TMP/refdeps"; cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/refdeps/"
python3 - "$ROOT" "$TMP/reference.jar" "$TMP/refdeps/browscap-java-1.2.11.jar" "$TMP/refdeps/commons-csv-1.7.jar" "$TMP/refdeps/commons-codec-1.14.jar" <<'PY'
from pathlib import Path
import hashlib,json,sys,zipfile
root,out,brows,csv,codec=map(Path,sys.argv[1:]); name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'; expected=next(x['sha256'] for x in json.loads((root/'evidence/framework-contract/jars.json').read_text()) if x['jar']==name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as z:
 data=z.read(name); cap=z.read('BOOT-INF/lib/browscap-java-1.2.11.jar'); c=z.read('BOOT-INF/lib/commons-csv-1.7.jar'); cc=z.read('BOOT-INF/lib/commons-codec-1.14.jar')
assert hashlib.sha256(data).hexdigest()==expected; assert hashlib.sha256(cap).hexdigest()=='0a02b8cb0c89aaf61f45797d9632832967128da183803647849d27d818cd0a86'; assert hashlib.sha256(c).hexdigest()=='25f5e7914729a3cb9cbb83918b5f1116625cca63ce38a50f0fe596f837b9a524'; assert hashlib.sha256(cc).hexdigest()=='a128e4f93fabe5381ded64cf2873019e06030b718eb43ceeae0b0e5d17ad33e9'; out.write_bytes(data); brows.write_bytes(cap); csv.write_bytes(c); codec.write_bytes(cc)
PY
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/StringUtilsDifferentialTest.java"
"$JDK/bin/java" -cp "$TMP:$ROOT/owned-framework/target/public-deps/*" com.sunwayworld.framework.StringUtilsDifferentialTest "$TMP/refdeps" "$TMP/reference.jar" "$ROOT/owned-framework/target/classes"
