#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
OUT="$ROOT/owned-equipment/target"
FRAMEWORK="$ROOT/owned-framework/target"
SOURCE="$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/mdm"
if [ ! -d "$SOURCE" ]; then echo "missing historical project source" >&2; exit 2; fi
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
if [ ! -f "$FRAMEWORK/lims-owned-framework-1.0.0.jar" ]; then JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh"; fi
JAVA_SOURCES=( "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java" )
for name in EmcEquiptBean EmcEquiptDCUConfBean EmcEquiptPerCheckPlanBean EmcEquiptPerCheckRecordBean EmcEquiptPlanBean EmcEquiptRecordBean EmcEquiptTestItemBean EmcEquiptTestMethodBean EmcEquiptUseRecordBean; do
  JAVA_SOURCES+=("$SOURCE/equipt/equipt/bean/$name.java")
done
JAVA_SOURCES+=("$SOURCE/equipt/equipt/bean/EmcEquiptMaintainRepairBean.java")
JAVA_SOURCES+=("$SOURCE/equipt/equipt/bean/PfEquiptMaintainRepairDTO.java")
JAVA_SOURCES+=("$SOURCE/equipt/equipt/bean/PfEquiptUserDTO.java")
JAVA_SOURCES+=("$SOURCE/equipt/equipt/bean/PfEquiptPlanDTO.java")
JAVA_SOURCES+=("$SOURCE/equipt/equipt/bean/PfEquiptPerCheckDTO.java")
JAVA_SOURCES+=("$SOURCE/basemdm/equiptcategory/bean/EmcEquiptMaintBean.java" "$SOURCE/basemdm/equiptcategory/dao/EmcEquiptMaintDao.java" "$SOURCE/basemdm/equiptcategory/service/EmcEquiptMaintService.java")
for name in EmcEquiptService EmcEquiptPlanService EmcEquiptRecordService EmcEquiptTestMethodService EmcEquiptUseRecordService EmcEquiptPerCheckPlanService EmcEquiptPerCheckRecordService EmcEquiptMaintainRepairService; do
  JAVA_SOURCES+=("$SOURCE/equipt/equipt/service/$name.java")
done
JAVA_SOURCES+=("$SOURCE/equipt/equipt/dao/EmcEquiptDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptPlanDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptRecordDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptUseRecordDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptPerCheckPlanDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptPerCheckRecordDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptMaintainRepairDao.java" "$SOURCE/equipt/equipt/dao/EmcEquiptTestMethodDao.java" "$SOURCE/equipt/equipt/resource/EmcEquiptResource.java" "$SOURCE/equipt/equipt/resource/impl/EmcEquiptResourceImpl.java" "$SOURCE/equipt/equipt/controller/EmcEquiptController.java" "$SOURCE/equipt/equipt/controller/impl/EmcEquiptControllerImpl.java")
sha256sum "${JAVA_SOURCES[@]}" > "$OUT/historical-source.sha256"
CP="$FRAMEWORK/classes:$FRAMEWORK/public-deps/*"
find "$ROOT/owned-equipment/src/main/java" -name '*.java' -print > "$OUT/owned-sources.txt"
"$JDK/bin/javac" -encoding UTF-8 -parameters -source 8 -target 8 -cp "$CP" -d "$OUT/classes" "${JAVA_SOURCES[@]}" @"$OUT/owned-sources.txt"
cp -R "$ROOT/owned-equipment/src/main/resources/." "$OUT/classes/"
mkdir -p "$OUT/classes/templates/emc/module/mdm/equipt/equipts" "$OUT/classes/static/emc/module/mdm/equipt/equipts"
cp "$PROJECT_ROOT/src/main/resources/templates/emc/module/mdm/equipt/equipts/"*.html "$OUT/classes/templates/emc/module/mdm/equipt/equipts/"
cp "$PROJECT_ROOT/src/main/resources/static/emc/module/mdm/equipt/equipts/"*.js "$OUT/classes/static/emc/module/mdm/equipt/equipts/"
find "$ROOT/owned-equipment/src/test/java" -name '*.java' -print > "$OUT/test-sources.txt"
"$JDK/bin/javac" -encoding UTF-8 -parameters -source 8 -target 8 -cp "$OUT/classes:$CP" -d "$OUT/test-classes" @"$OUT/test-sources.txt"
"$JDK/bin/jar" cf "$OUT/lims-owned-equipment-1.0.0.jar" -C "$OUT/classes" .
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-equipment-1.0.0.jar" "$FRAMEWORK/lims-owned-framework-1.0.0.jar" "$FRAMEWORK/public-deps"
echo "owned equipment main/runtime module build passed"
