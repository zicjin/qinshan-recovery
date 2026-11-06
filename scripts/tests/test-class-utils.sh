#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
mkdir -p "$TMP/refdeps" "$TMP/fixtures/contractresources/nested" "$TMP/jarclasses"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/refdeps/"
python3 - "$ROOT" "$TMP/reference.jar" <<'PY'
from pathlib import Path
import hashlib,json,sys,zipfile
root,out=map(Path,sys.argv[1:])
name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected=next(x['sha256'] for x in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if x['jar']==name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as z: data=z.read(name)
assert hashlib.sha256(data).hexdigest()==expected,'supplier reference hash mismatch'
out.write_bytes(data)
PY
"$JDK/bin/javac" -source 8 -target 8 -d "$TMP/fixtures" "$ROOT/owned-framework/src/test/fixtures/classutils/com/sunwayworld/contractscan/ScanFixture.java" "$ROOT/owned-framework/src/test/fixtures/classutils/com/sunwayworld/framework/utils/EncodingUtils.java"
"$JDK/bin/javac" -source 8 -target 8 -d "$TMP/jarclasses" "$ROOT/owned-framework/src/test/fixtures/classutils/contractjar/JarFixture.java"
"$JDK/bin/jar" cf "$TMP/fixtures.jar" -C "$TMP/jarclasses" .
printf 'alpha\n' >"$TMP/fixtures/contractresources/one.txt"
printf 'beta\n' >"$TMP/fixtures/contractresources/nested/two.txt"
printf 'other\n' >"$TMP/fixtures/contractresources/other.dat"
CP="$ROOT/owned-framework/target/test-classes"
ARGS=("$TMP/refdeps" "$TMP/reference.jar" "$ROOT/owned-framework/target/classes" "$TMP/fixtures" "$TMP/fixtures.jar")
"$JDK/bin/java" -cp "$CP" com.sunwayworld.framework.ClassUtilsDifferentialTest "${ARGS[@]}"
for implementation in supplier owned; do
    set +e
    timeout 3s "$JDK/bin/java" -cp "$CP" com.sunwayworld.framework.ClassUtilsDifferentialTest "${ARGS[@]}" hang "$implementation" >"$TMP/$implementation-loop.log" 2>&1
    status=$?
    set -e
    test "$status" -eq 124
    rg -q '^annotation-loop-entered$' "$TMP/$implementation-loop.log"
done
echo 'ClassUtils inherited annotation nontermination: supplier and owned matched under process timeouts'
