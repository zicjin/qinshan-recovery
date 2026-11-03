#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC="$ROOT/owned-framework/src/main/java"
TEST="$ROOT/owned-framework/src/test/java"
OUT="$ROOT/owned-framework/target"
JDK="${JAVA_HOME:-$ROOT/build/jdk1.8.0_391}"
JAVAC_BIN="${JAVAC_BIN:-$JDK/bin/javac}"
JAR_BIN="${JAR_BIN:-$JDK/bin/jar}"
JAVA_BIN="${JAVA_BIN:-$JDK/bin/java}"
rm -rf "$OUT"
mkdir -p "$OUT/classes" "$OUT/test-classes"
python3 "$ROOT/scripts/prepare-owned-public-deps.py" "$OUT/public-deps"
find "$SRC" -name '*.java' -print0 | xargs -0 "$JAVAC_BIN" -encoding UTF-8 -source 8 -target 8 -cp "$OUT/public-deps/*" -d "$OUT/classes"
find "$TEST" -name '*.java' -print0 | xargs -0 "$JAVAC_BIN" -encoding UTF-8 -source 8 -target 8 -cp "$OUT/classes:$OUT/public-deps/*" -d "$OUT/test-classes"
if [ -d "$ROOT/owned-framework/src/main/resources" ]; then
  cp -R "$ROOT/owned-framework/src/main/resources/." "$OUT/classes/"
fi
"$JAR_BIN" cf "$OUT/lims-owned-framework-1.0.0.jar" -C "$OUT/classes" .
python3 "$ROOT/scripts/tests/test-owned-public-contracts.py"
bash "$ROOT/scripts/tests/test-owned-static-resource-alias.sh"
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedFrameworkContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.RestJsonWrapperBeanContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.MapperParameterContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.DataSourceUtilsContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.ApplicationContextHelperContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSequenceProviderContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.AuthenticationHelperContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.JdbcValueContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.AuditEventContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedWriteSupportContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedWorkspaceControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedWorkspacePanelControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedReagentQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSolQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSubpackageQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedWorkConditionQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSampleRemindQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSampleRecordQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSampleMainReadControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedReagentWriteControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedMaterialApplyControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedMaterialApplyQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedMtlCategoryQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedAuditTrailControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedAuditTrailQueryContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedAuditRecordControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedOrderRetainControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedEquipmentGenericResourceControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedResultGenericResourceControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedCurveQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedEquipmentCurveConfigQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSampleBottleQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedSampleSetQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedVocQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedPhysChemicQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedOilFilmQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedTempHumidityQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedRunRemindQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedPretreatmentQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedProjectQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedFolderGenericResourceControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedResultRunControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedMobileProjectControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedQualityControlChartControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedOrderRetainQueryExtensionControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedOrderTaskResultQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedOrderTaskQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedDeliveryOrderLabelQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedProjectQcQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedProjectEditLogQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedProjectOtherFeeQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedProjectSchemeQueryControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedItemEquipmentResourceControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedEquipmentMdmResourceControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedWorkspaceShellControllerContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedTemplateViewResolverContractTest
"$JAVA_BIN" -cp "$OUT/classes:$OUT/test-classes:$OUT/public-deps/*" com.sunwayworld.framework.OwnedReportTmplControllerContractTest
"$ROOT/scripts/reject-supplier-runtime.sh" "$OUT/lims-owned-framework-1.0.0.jar" "$OUT/public-deps"
echo "owned framework build and contract test passed"
