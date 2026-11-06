#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"; trap 'rm -rf "$tmp"' EXIT
mkdir "$tmp/refdeps" "$tmp/owneddeps"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/refdeps/"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/owneddeps/"
python3 - "$ROOT" "$tmp/supplier.jar" <<'PY'
from pathlib import Path
import hashlib,json,sys,zipfile
root,out=map(Path,sys.argv[1:])
name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected=next(x['sha256'] for x in json.loads((root.parent/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if x['jar']==name)
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive: data=archive.read(name)
assert hashlib.sha256(data).hexdigest()==expected
out.write_bytes(data)
PY
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.ArrayCollectionUtilsDifferentialTest "$tmp/supplier.jar" "$ROOT/owned-framework/target/classes" "$ROOT/owned-framework/target/test-classes" "$ROOT/owned-framework/target/public-deps"
