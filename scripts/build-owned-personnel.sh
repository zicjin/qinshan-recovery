#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
JAVAC="$JDK/bin/javac"
JAR="$JDK/bin/jar"
OUT="$ROOT/owned-personnel/target"
TEST="$ROOT/owned-personnel/src/test"
FRAMEWORK="$ROOT/owned-framework/target"
if [ ! -d "$PROJECT_ROOT/src/main/java" ]; then echo "missing recovered project source: $PROJECT_ROOT" >&2; exit 2; fi
JAVA_SOURCES=(
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/controller/EmcEmpController.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/controller/impl/EmcEmpControllerImpl.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpBean.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpQualBean.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpTestBean.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/bean/EmcEmpTrainBean.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/cert/bean/EmcCertBean.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/basemdm/cert/bean/EmcCertTestMethodBean.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/dao/EmcEmpDao.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/service/EmcEmpService.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/resource/EmcEmpResource.java"
 "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm/emp/emp/resource/impl/EmcEmpResourceImpl.java"
)
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
if [ "${OWNED_SKIP_DEPENDENCY_BUILD:-0}" != 1 ]; then
  JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh"
fi
python3 "$ROOT/scripts/prepare-owned-public-deps.py" "$FRAMEWORK/public-deps"
CP="$FRAMEWORK/classes:$FRAMEWORK/public-deps/*"
mapfile -d '' OWNED_SOURCES < <(find "$ROOT/owned-personnel/src/main/java" -name '*.java' -print0 | sort -z)
"$JAVAC" -parameters -source 8 -target 8 -cp "$CP" -d "$OUT/classes" "${JAVA_SOURCES[@]}" "${OWNED_SOURCES[@]}"
python3 - "$PROJECT_ROOT" "$OUT/original-sources.sha256" "${JAVA_SOURCES[@]}" <<'PY_MANIFEST'
import hashlib, pathlib, sys
root=pathlib.Path(sys.argv[1]); out=pathlib.Path(sys.argv[2])
out.write_text("".join(hashlib.sha256(pathlib.Path(path).read_bytes()).hexdigest()+"  "+str(pathlib.Path(path).relative_to(root))+"\n" for path in sys.argv[3:]))
PY_MANIFEST
cp -R "$ROOT/owned-personnel/src/main/resources/." "$OUT/classes/"
for directory in templates/emc/module/mdm/emp/emps static/emc/module/mdm/emp/emps; do
  mkdir -p "$OUT/classes/$directory"
  cp -R "$PROJECT_ROOT/src/main/resources/$directory/." "$OUT/classes/$directory/"
done
python3 - "$PROJECT_ROOT" "$OUT/original-sources.sha256" <<'PY_RESOURCES'
import hashlib, pathlib, sys
root=pathlib.Path(sys.argv[1]); out=pathlib.Path(sys.argv[2])
resources = sorted(path for directory in ('templates/emc/module/mdm/emp/emps', 'static/emc/module/mdm/emp/emps')
                   for path in (root/'src/main/resources'/directory).rglob('*') if path.is_file())
with out.open('a') as manifest:
    for path in resources:
        manifest.write(hashlib.sha256(path.read_bytes()).hexdigest()+'  '+str(path.relative_to(root))+'\n')
PY_RESOURCES
if [ -d "$TEST" ]; then
  find "$TEST" -name "*.java" -print0 | xargs -0 "$JAVAC" -source 8 -target 8 -cp "$OUT/classes:$CP" -d "$OUT/test-classes"
fi
"$JDK/bin/java" -cp "$OUT/classes:$OUT/test-classes:$CP" com.sunwayworld.framework.OwnedPersonnelAbilityResourceControllerContractTest
"$JAR" cf "$OUT/lims-owned-personnel-1.0.0.jar" -C "$OUT/classes" .
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-personnel-1.0.0.jar" "$FRAMEWORK/lims-owned-framework-1.0.0.jar" "$FRAMEWORK/public-deps"
echo "owned personnel main/runtime module build passed"
