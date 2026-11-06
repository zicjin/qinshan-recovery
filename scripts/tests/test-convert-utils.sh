#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
mkdir "$TMP/refdeps" "$TMP/owneddeps"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/refdeps/"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$TMP/owneddeps/"
python3 - "$ROOT" "$TMP" <<'PY'
import hashlib,json,pathlib,sys,zipfile
root,out=map(pathlib.Path,sys.argv[1:])
common=next(x['sha256'] for x in json.loads((root/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if x['jar']=='BOOT-INF/lib/gikam-core-common-14.1.5.jar')
deps={'gikam-core-common-14.1.5.jar':common,'ojdbc8-19.3.0.0.jar':'a66d27a14f3adee484427cc4de008af85a5c3e78e2e3285a4dba1277332978a5','mssql-jdbc-7.4.1.jre8.jar':'00a6eaf49184dbffd89c4baf21d08a471797ead9bf48ca38faf7f9fe4244e391','commons-io-2.4.jar':'cc6a41dc3eaacc9e440a6bd0d2890b20d36b4ee408fe2d67122f328bb6e01581'}
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive:
 for name,digest in deps.items():
  data=archive.read('BOOT-INF/lib/'+name);assert hashlib.sha256(data).hexdigest()==digest,name
  (out/'supplier.jar' if name.startswith('gikam-') else out/'refdeps'/name).write_bytes(data)
PY
for zone in UTC Asia/Shanghai America/New_York; do
 "$JDK/bin/java" -Duser.timezone="$zone" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.ConvertUtilsDifferentialTest "$TMP/refdeps" "$TMP/supplier.jar" "$TMP/owneddeps" "$ROOT/owned-framework/target/classes" "$ROOT/owned-framework/target/test-classes"
done
