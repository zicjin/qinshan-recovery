#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
"$ROOT/scripts/build-owned-framework.sh" >/dev/null
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes:$ROOT/owned-framework/target/public-deps/*" com.sunwayworld.framework.SunwayApplicationSelectionTest
"$JDK/bin/javap" -classpath "$ROOT/owned-framework/target/classes" -public -s com.sunwayworld.framework.spring.SunwayApplication > "$TMP/owned.txt"
grep -Fq 'descriptor: (Ljava/lang/Class;[Ljava/lang/String;)Lorg/springframework/context/ConfigurableApplicationContext;' "$TMP/owned.txt"
grep -Fq 'descriptor: ()Ljava/lang/Class;' "$TMP/owned.txt"
python3 - "$ROOT" "$TMP" <<'PYREF'
import pathlib,sys,zipfile
r=pathlib.Path(sys.argv[1]);o=pathlib.Path(sys.argv[2]);
with zipfile.ZipFile(r/'materials/gikam-third-czjky-14.1.5-run.jar') as z: (o/'supplier.jar').write_bytes(z.read('BOOT-INF/lib/gikam-core-common-14.1.5.jar'))
PYREF
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.BootSelectionDifferentialTest "$ROOT/owned-framework/target/public-deps" "$ROOT/owned-framework/target/test-classes" "$TMP/supplier.jar" "$ROOT/owned-framework/target/classes"
echo 'test-sunway-application-selection: PASS' 
