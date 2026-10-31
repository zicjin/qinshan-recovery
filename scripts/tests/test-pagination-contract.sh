#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
mkdir -p "$TMP/supplier"
python3 - "$ROOT" "$TMP" <<'PY'
import hashlib,json,pathlib,sys,zipfile
root=pathlib.Path(sys.argv[1]); out=pathlib.Path(sys.argv[2])/'supplier'
identities={r['jar']:r['sha256'] for r in json.loads((root/'evidence/framework-contract/jars.json').read_text())}
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as z:
    for n in ('BOOT-INF/lib/gikam-core-common-14.1.5.jar','BOOT-INF/lib/spring-data-commons-2.3.4.RELEASE.jar','BOOT-INF/lib/mybatis-3.5.6.jar'):
        content=z.read(n)
        if n in identities:
            assert hashlib.sha256(content).hexdigest()==identities[n], 'supplier reference hash differs'
        (out/pathlib.Path(n).name).write_bytes(content)
PY
JAVAC_BIN="$JDK/bin/javac" JAR_BIN="$JDK/bin/jar" JAVA_BIN="$JDK/bin/java" "$ROOT/scripts/build-owned-framework.sh" >/dev/null
"$JDK/bin/javac" -source 8 -target 8 -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/public-deps/*" -d "$TMP" "$ROOT/owned-framework/src/test/java/com/sunwayworld/framework/PaginationDifferentialTest.java"
"$JDK/bin/java" -cp "$TMP" com.sunwayworld.framework.PaginationDifferentialTest "$TMP/supplier" "$ROOT/owned-framework/target/classes" "$ROOT/owned-framework/target/public-deps" >"$TMP/differential.log"
grep -Fq 'pagination differential: PASS' "$TMP/differential.log"
tail -n 1 "$TMP/differential.log"
echo 'test-pagination-contract: PASS'
