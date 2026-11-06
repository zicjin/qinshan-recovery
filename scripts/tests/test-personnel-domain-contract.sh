#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
JAVAC_BIN="$JDK/bin/javac" JAR_BIN="$JDK/bin/jar" JAVA_BIN="$JDK/bin/java" "$ROOT/scripts/build-owned-framework.sh" >"$TMP/build.log"
python3 - "$ROOT" "$TMP" <<'PY'
import hashlib,json,pathlib,sys,zipfile
root=pathlib.Path(sys.argv[1]); output=pathlib.Path(sys.argv[2])
name='BOOT-INF/lib/gikam-core-common-14.1.5.jar'
expected=next(row['sha256'] for row in json.loads((root.parent/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if row['jar']==name)
source=root/'project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpBean.java'
assert hashlib.sha256(source.read_bytes()).hexdigest()=='e83c14c067c2df9da3473de8b0259fc9e786041ea36e7063d607206df52df768', 'personnel source identity differs'
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as archive:
    data=archive.read(name)
    assert hashlib.sha256(data).hexdigest()==expected, 'supplier reference hash differs'
    (output/'supplier.jar').write_bytes(data)
PY
mkdir -p "$TMP/beans"
# This is the unchanged real recovered DTO, compiled with owned framework classes
# and pinned public annotation/JSON libraries. No supplier class is on this path.
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$TMP/beans" \
  "$ROOT/project/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpBean.java"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.PersonnelDomainDifferentialTest \
  "$ROOT/owned-framework/target/public-deps" "$TMP/beans" "$ROOT/owned-framework/target/classes" "$TMP/supplier.jar" "$ROOT/owned-framework/target/test-classes"
echo 'test-personnel-domain-contract: PASS'
