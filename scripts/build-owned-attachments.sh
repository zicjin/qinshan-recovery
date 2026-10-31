#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT_ROOT="${LIMS_PROJECT_ROOT:-$ROOT/project}"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
OUT="$ROOT/owned-attachments/target"
FRAMEWORK="$ROOT/owned-framework/target"
if [ "${OWNED_SKIP_DEPENDENCY_BUILD:-0}" != 1 ]; then
 JAVA_HOME="$JDK" "$ROOT/scripts/build-owned-framework.sh"
fi
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
CP="$FRAMEWORK/classes:$FRAMEWORK/public-deps/*"
SOURCE="$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/item/homeattachment"
printf '%s\n' "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/projectfunction/GikamWpjcApplication.java" > "$OUT/historical-sources.txt"
for relative in bean/EmcHomeAttachmentBean.java bean/EmcHomeAttachmentPermissionBean.java dao/EmcHomeAttachmentDao.java service/EmcHomeAttachmentService.java resource/EmcHomeAttachmentResource.java resource/impl/EmcHomeAttachmentResourceImpl.java controller/EmcHomeAttachmentController.java controller/impl/EmcHomeAttachmentControllerImpl.java; do
    printf '%s\n' "$SOURCE/$relative" >> "$OUT/historical-sources.txt"
done
printf '%s\n' "$PROJECT_ROOT/src/main/java/com/sunwayworld/emc/module/erp/service/ErpFileService.java" >> "$OUT/historical-sources.txt"
xargs -d '\n' sha256sum < "$OUT/historical-sources.txt" > "$OUT/historical-source.sha256"
find "$ROOT/owned-attachments/src/main/java" -name '*.java' -print > "$OUT/owned-sources.txt"
"$JDK/bin/javac" -encoding UTF-8 -parameters -source 8 -target 8 -cp "$CP" -d "$OUT/classes" @"$OUT/historical-sources.txt" @"$OUT/owned-sources.txt"
cp -R "$ROOT/owned-attachments/src/main/resources/." "$OUT/classes/"
for kind in templates static; do
    DEST="$OUT/classes/$kind/emc/module/item/home-attachments"
    [ "$kind" != static ] || DEST="$OUT/classes/static/emc/module/item/home-attachments"
    mkdir -p "$DEST"
    cp "$PROJECT_ROOT/src/main/resources/$kind/emc/module/item/home-attachments/"* "$DEST/"
done
mkdir -p "$OUT/classes/templates/emc/module/index/workspace"
cp "$PROJECT_ROOT/src/main/resources/templates/emc/module/index/workspace/emc-home-attachment-download-list.html" "$OUT/classes/templates/emc/module/index/workspace/"
find "$ROOT/owned-attachments/src/test/java" -name '*.java' -print > "$OUT/test-sources.txt"
"$JDK/bin/javac" -encoding UTF-8 -parameters -source 8 -target 8 -cp "$OUT/classes:$CP" -d "$OUT/test-classes" @"$OUT/test-sources.txt"
"$JDK/bin/jar" cf "$OUT/lims-owned-attachments-1.0.0.jar" -C "$OUT/classes" .
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-attachments-1.0.0.jar" "$FRAMEWORK/lims-owned-framework-1.0.0.jar" "$FRAMEWORK/public-deps"
