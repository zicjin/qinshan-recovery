#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
python3 - "$ROOT" "$TMP" <<'PY'
from pathlib import Path
import sys,zipfile
root=Path(sys.argv[1]); out=Path(sys.argv[2])
with zipfile.ZipFile(root/'materials/gikam-third-czjky-14.1.5-run.jar') as z: (out/'common.jar').write_bytes(z.read('BOOT-INF/lib/gikam-core-common-14.1.5.jar'))
PY
"$ROOT/scripts/build-owned-framework.sh" >/dev/null
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.MemoryCacheManagerDifferentialTest "$TMP/common.jar" "$ROOT/owned-framework/target/classes"
"$JDK/bin/java" -cp "$ROOT/owned-framework/target/classes:$ROOT/owned-framework/target/test-classes" com.sunwayworld.framework.MemoryCacheManagerContractTest
