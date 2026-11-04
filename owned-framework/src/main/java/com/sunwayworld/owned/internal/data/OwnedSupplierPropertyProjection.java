package com.sunwayworld.owned.internal.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Maps Oracle result labels to the JavaBean properties exposed by supplier
 * GenericResource responses.  Supplier row JSON is a Bean projection, while
 * columnTypeMap describes every SQL result column.
 */
public final class OwnedSupplierPropertyProjection {
    private static final Map<String, String> ORDER = properties(
            "id orderNo sampleParamOrderNo projId folderId labelNo clientOrderName "
            + "sampleAddress parentOrderNo orderDesc sendDate sendTime cycle frequency "
            + "monitorTmplId orderCategory sampleId sampleBeginDate sampleBeginTime "
            + "sampleCompleteDate sampleCompleteTime sampleNum qcCategoryId qcCategoryCode "
            + "qcName orderNum remark termination acceptedUserId accepteUserName acceptedDate "
            + "horizontalCode verticalCode verticalLine mobileId source relatedOrderNo erpGbId "
            + "orderQty senderId struct otherOb createdById createdByName createdTime "
            + "createdByOrgId createdByOrgName");
    private static final Map<String, String> PROJECT = properties(
            "id projNo projName testOrgId testOrgName projAddress entrustOrgId entrustOrgName "
            + "entrustOrgAddress bizCategoryId bizCategoryCode taskNo description registDate "
            + "reportOrgId acceptDate compleDate projAmount monitorCategory entrustDate sendDate "
            + "sendType entrustMethod subPackageDesc ndCal status cancleReason sendUsername taskDate "
            + "testContent qcReq remark entrustOrgContact entrustOrgPhone testOrgAddress testOrgContact "
            + "testOrgPhone monitorOrgName source nextTaskDate startTime endTime cron mobileId projNode "
            + "sample urgent beforeStatus reportEditUserId reportEditUserName qcCheck cancelReason "
            + "schemeReportLineId testOrgEmail testOrgFixedPhone projectQcUserId projectQcUserName "
            + "projectSampleOrgId sampleOrgName projectSampleById projectSampleByName "
            + "projectSampleBeginDate projectSampleEndDate createdById createdByName createdTime "
            + "createdByOrgId createdByOrgName orgId orgName keepDescription remainSampleDeal testsMethod "
            + "servicePay invoiceTitle taxNo payType reportFormat reportNum reportGrantTime reportSendType "
            + "prospect prospectDate opinion safetyMeasures monitorCondition prospecRemark testCycle "
            + "reportDeliveryWay packMethod requiredSamptime orderNum sampleOrderCount erpGbId erpReviewId "
            + "reportAptitude reportUse pretreatment needQcReport qcReportType needSplitReport "
            + "subcontractSupplier reportEnName reportCnName reportEnAddress reportCnAddress testingLab "
            + "testMethodAssigner mergeReport reportPostAddress addressee recipientPhone detectPurpose "
            + "erpOrderNumber reportTitle splitContractId securityLevel reportFee needReport periodType "
            + "period otherReportUse erpContactGbId entrustOrgEmail projCreatedTime labInnerNo "
            + "subcontractorId projType requiredCompleteDate reportIssueTime testProjectName "
            + "businessUserId businessUserName minSampleDuration year toReportEditDate reportAssignStatus "
            + "projectType firstIssueDateTime projectPrice folderRemark projSection customerSource "
            + "remarkOne remarkTwo amountDeterm");

    private OwnedSupplierPropertyProjection() { }

    public static String order(String columnLabel) {
        return ORDER.get(compact(columnLabel));
    }

    public static String project(String columnLabel) {
        return PROJECT.get(compact(columnLabel));
    }

    private static Map<String, String> properties(String values) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String property : values.trim().split("\\s+")) {
            result.put(compact(property), property);
        }
        return Collections.unmodifiableMap(result);
    }

    private static String compact(String value) {
        if (value == null) return "";
        StringBuilder result = new StringBuilder(value.length());
        for (char character : value.toUpperCase(Locale.ROOT).toCharArray()) {
            if (Character.isLetterOrDigit(character)) result.append(character);
        }
        return result.toString();
    }
}
