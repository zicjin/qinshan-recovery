#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"; JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"; trap 'python3 -c "import shutil,sys; shutil.rmtree(sys.argv[1])" "$tmp"' EXIT; mkdir "$tmp/refdeps" "$tmp/owneddeps"
python3 - "$ROOT" "$tmp/supplier.jar" <<'PYREF'
import hashlib,json,pathlib,sys,zipfile
root,out=map(pathlib.Path,sys.argv[1:]); member='BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected=next(row['sha256'] for row in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if row['jar']==member)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive: data=archive.read(member)
assert hashlib.sha256(data).hexdigest()==expected; out.write_bytes(data)
PYREF
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/refdeps/"; cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/owneddeps/"
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$ROOT/owned-framework/target/test-classes" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/BeanUtilsDifferentialTest.java"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.BeanUtilsDifferentialTest "$tmp/refdeps" "$tmp/supplier.jar" "$tmp/owneddeps" "$ROOT/owned-framework/target/classes" "$ROOT/owned-framework/target/test-classes"
