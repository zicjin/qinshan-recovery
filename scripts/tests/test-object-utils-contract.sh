#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-/home/ts/qinshan/lims-recovery/build/jdk1.8.0_391}"
RUN_JAR="${RUN_JAR:-/home/ts/qinshan/lims-recovery/materials/gikam-third-czjky-14.1.5-run.jar}"
"$ROOT/scripts/build-owned-framework.sh" >/dev/null
tmp="$(mktemp -d)"; mkdir "$tmp/refdeps" "$tmp/owneddeps"; trap 'rm -rf "$tmp"' EXIT
unzip -p "$RUN_JAR" BOOT-INF/lib/gikam-core-common-14.1.5.jar >"$tmp/gikam-core-common-14.1.5.jar"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/refdeps/"
cp "$ROOT/owned-framework/target/public-deps/"*.jar "$tmp/owneddeps/"
unzip -p "$RUN_JAR" BOOT-INF/lib/ojdbc8-19.3.0.0.jar >"$tmp/refdeps/ojdbc8-19.3.0.0.jar"
unzip -p "$RUN_JAR" BOOT-INF/lib/cglib-nodep-3.2.5.jar >"$tmp/refdeps/cglib-nodep-3.2.5.jar"
unzip -p "$RUN_JAR" BOOT-INF/lib/mssql-jdbc-7.4.1.jre8.jar >"$tmp/refdeps/mssql-jdbc-7.4.1.jre8.jar"
for library in commons-beanutils-1.9.4.jar commons-collections-3.2.2.jar commons-logging-1.2.jar; do unzip -p "$RUN_JAR" "BOOT-INF/lib/$library" >"$tmp/refdeps/$library"; done
python3 - "$ROOT" "$tmp" <<'PY'
import hashlib, json, pathlib, sys
root, temporary = map(pathlib.Path, sys.argv[1:])
expected = next(x['sha256'] for x in json.loads((root.parent/'.scratch/sunway-supplier-exit-api-first/evidence-archive/framework-contract/jars.json').read_text()) if x['jar'] == 'BOOT-INF/lib/gikam-core-common-14.1.5.jar')
assert hashlib.sha256((temporary/'gikam-core-common-14.1.5.jar').read_bytes()).hexdigest() == expected, 'supplier reference hash differs'
for name, digest in {
 'ojdbc8-19.3.0.0.jar': 'a66d27a14f3adee484427cc4de008af85a5c3e78e2e3285a4dba1277332978a5',
 'cglib-nodep-3.2.5.jar': '9ab68b361ffdce66747f63bbe0676391c0a96d6de3e743ac03d7e998ff0a1064',
 'mssql-jdbc-7.4.1.jre8.jar': '00a6eaf49184dbffd89c4baf21d08a471797ead9bf48ca38faf7f9fe4244e391',
 'commons-beanutils-1.9.4.jar': '7d938c81789028045c08c065e94be75fc280527620d5bd62b519d5838532368a',
 'commons-collections-3.2.2.jar': 'eeeae917917144a68a741d4c0dff66aa5c5c5fd85593ff217bced3fc8ca783b8',
 'commons-logging-1.2.jar': 'daddea1ea0be0f56978ab3006b8ac92834afeefbd9b7e4e6316fca57df0fa636'
}.items():
 assert hashlib.sha256((temporary/'refdeps'/name).read_bytes()).hexdigest() == digest, 'public reference dependency hash differs: '+name
PY
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" \
  com.sunwayworld.framework.ObjectUtilsDifferentialTest "$tmp/refdeps" "$tmp/gikam-core-common-14.1.5.jar" "$tmp/owneddeps" "$ROOT/owned-framework/target/classes"
