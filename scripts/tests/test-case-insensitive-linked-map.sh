#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-$ROOT/materials/gikam-third-czjky-14.1.5-run.jar}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"; trap 'rm -rf "$tmp"' EXIT; mkdir "$tmp/refdeps" "$tmp/owneddeps"
python3 - "$ROOT" "$RUN_JAR" "$tmp" <<'PY'
import hashlib,json,pathlib,sys,zipfile
root,run,out=map(pathlib.Path,sys.argv[1:]); name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected=next(r['sha256'] for r in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if r['jar']==name)
with zipfile.ZipFile(run) as z:
 data=z.read(name); assert hashlib.sha256(data).hexdigest()==expected; (out/'supplier.jar').write_bytes(data)
PY
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/refdeps/"; cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/owneddeps/"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.CaseInsensitiveLinkedMapDifferentialTest "$tmp/refdeps" "$tmp/supplier.jar" "$tmp/owneddeps" "$ROOT/owned-framework/target/classes"
