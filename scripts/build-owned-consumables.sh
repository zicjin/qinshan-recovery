#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
OUT="$ROOT/owned-consumables/target"
FRAMEWORK="$ROOT/owned-framework/target"
SOURCE="$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/item/consume/consume"
if [ ! -d "$SOURCE" ]; then echo "missing recovered project source: $PROJECT_ROOT" >&2; exit 2; fi
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
if [ "${OWNED_SKIP_DEPENDENCY_BUILD:-0}" != 1 ]; then
 JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh"
fi
CP="$FRAMEWORK/classes:$FRAMEWORK/public-deps/*"
OWNED_SOURCES=()
JAVA_SOURCES=("$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java")
JAVA_SOURCES+=("$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/mtl/bean/EmcMtlBean.java" "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/mtl/bean/EmcStockBean.java" "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/mtl/bean/EmcStockLogBean.java" "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/mtl/bean/EmcMtlUseRecordBean.java")
if [ "${OWNED_CONSUMABLES_RED:-0}" != 1 ]; then
 for part in bean/EmcConsumeBean bean/EmcCsmStockBean bean/EmcCsmStockLogBean dao/EmcConsumeDao dao/EmcCsmStockDao dao/EmcCsmStockLogDao service/EmcConsumeService service/EmcCsmStockService service/EmcCsmStockLogService resource/EmcConsumeResource resource/impl/EmcConsumeResourceImpl controller/EmcConsumeController controller/impl/EmcConsumeControllerImpl; do
  JAVA_SOURCES+=("$SOURCE/$part.java")
 done
 OWNED_SOURCES=()
 while IFS= read -r -d '' source; do OWNED_SOURCES+=("$source"); done < <(find "$ROOT/owned-consumables/src/main/java" -name '*.java' -print0 | sort -z)
fi
"$JDK/bin/javac" -encoding UTF-8 -parameters -source 8 -target 8 -cp "$CP" -d "$OUT/classes" "${JAVA_SOURCES[@]}" "${OWNED_SOURCES[@]}"
if [ "${OWNED_CONSUMABLES_RED:-0}" != 1 ]; then
 cp -R "$ROOT/owned-consumables/src/main/resources/." "$OUT/classes/"
 for directory in templates/emc/module/item/consume/consumes static/emc/module/item/consume/consumes; do
  mkdir -p "$OUT/classes/$directory"
  cp -R "$PROJECT_ROOT/src/main/resources/$directory/." "$OUT/classes/$directory/"
 done
fi
python3 - "$PROJECT_ROOT" "$OUT/original-sources.sha256" "${JAVA_SOURCES[@]}" <<'PY_MANIFEST'
import hashlib, pathlib, sys
root=pathlib.Path(sys.argv[1]); out=pathlib.Path(sys.argv[2])
paths=[pathlib.Path(p) for p in sys.argv[3:]]
if len(paths)>1:
 paths += sorted(p for d in ('templates/emc/module/item/consume/consumes','static/emc/module/item/consume/consumes') for p in (root/'src/main/resources'/d).rglob('*') if p.is_file())
out.write_text(''.join(hashlib.sha256(p.read_bytes()).hexdigest()+'  '+str(p.relative_to(root))+'\n' for p in paths))
PY_MANIFEST
find "$ROOT/owned-consumables/src/test" -name '*.java' -print0 | xargs -0 "$JDK/bin/javac" -encoding UTF-8 -source 8 -target 8 -cp "$OUT/classes:$CP" -d "$OUT/test-classes"
"$JDK/bin/jar" cf "$OUT/lims-owned-consumables-1.0.0.jar" -C "$OUT/classes" .
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-consumables-1.0.0.jar" "$FRAMEWORK/lims-owned-framework-1.0.0.jar" "$FRAMEWORK/public-deps"
echo "owned consumables main/runtime module build passed"
