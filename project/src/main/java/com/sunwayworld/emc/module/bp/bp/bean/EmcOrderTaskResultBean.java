package com.sunwayworld.emc.module.bp.bp.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 样品结果
 *
 * @author magh@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_ORDER_TASK_RESULT")
public class EmcOrderTaskResultBean extends AbstractPersistable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 5381252410446715899L;

    @Id
    private Long id;// 主键

    private Long folderId;// 点位编码

    private Long orderId;// 样品编码

    private String orderNo;// 样品编号

    private String parentOrderNo;// 父样编号

    private Long projId;// 项目编码

    private Long orderTaskId;// 样品测试编码

    private Long cycle;// 周期

    private Long frequency;// 频次

    private Long testId;// 测试编码

    private Long testMethodId;// 测试方法编码

    private Long methodId;// 方法编码

    private Long methodVersionId;// 方法版本编码

    private Long itemId;// 分析项编码

    private Long itemOrderNo;// 分析项排序码

    private String itemName;// 分析项

    private String itemAliasName;// 分析项别名

    private Long qcCategoryId;// QC类型主键

    @NotNull(defaultValue = "N/A")
    private String qcCategoryCode;// QC类型编码

    @NotNull(defaultValue = "N/A")
    private String qcName;// QC类型中文

    @NotNull(defaultValue = "0")
    private String scriptFlag;// 是否存在公式

    private String resultCategory;// 结果类型

    private String revisionRule;// 修约规则

    private String unitName;// 计量单位<存计量单位名称>

    private Long repeatNo;// 重复数（1,2,3...）

    private Long repeatQty;// 重复项

    private String originalResult;// 原始结果

    private String reportResult;// 报出结果

    private String reportDisplay; // 在报告中显示

    private String resultStatus;// 结果状态

    private String analysisType;// 分析类型

    private String lowLimit;// 检出低限

    private String highLimit;// 检出高限

    private String indexLowLimit;// 指标低限

    private String indexHighLimit;// 指标高限

    private String indexLimitUnit; // 指标高低限单位

    private String reason; // 原因

    private String runId; // 批号

    private String avgResult;// 均值

    private String standLowLimit;// 标准低限

    private String standHighLimit;// 标准高限

    private String standQty;// 标准物量

    @NotNull(defaultValue = Constant.NO)
    private String submitFlag;// 是否已提交（0，1）

    private String rn1; // RN1

    private String rn2; // RN2

    private String rn3; // RN3

    private String rn4; // RN4

    private String rn5; // RN5

    private String rn6; // RN6

    private String rn7; // RN7

    private String rn8; // RN8

    private String rn9; // RN9

    private String rn10; // RN10

    private String rn11; // RN11

    private String rn12; // RN12

    private String rn13; // RN13

    private String rn14; // RN14

    private String rn15; // RN15

    private String rn16; // RN16

    private String rn17; // RN17(江苏新锐)

    private String rn18; // RN18(江苏新锐)

    private String rn19; // RN19(江苏新锐)

    private String rn20; // RN20(江苏新锐)

    private String rn21; // RN21(江苏新锐)

    private String source; // 来源

    private String mobileId; // 移动端样品测试结果ID

    @Value("1")
    private String correlationValidateFlag; //数据相关性校验审核结果（1:通过;0:未通过;）

    private String resultRule; // 结果规则

    private String convertFactor; // 转换系数

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String sceneEquiptNo;// 现场仪器编号

    private String sceneFileNo;// 现场文件编号

    private String qcDecisionCategory;// 质控判定类别

    public String getQcDecisionCategory() {
        return qcDecisionCategory;
    }

    public void setQcDecisionCategory(String qcDecisionCategory) {
        this.qcDecisionCategory = qcDecisionCategory;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getParentOrderNo() {
        return parentOrderNo;
    }

    public void setParentOrderNo(String parentOrderNo) {
        this.parentOrderNo = parentOrderNo;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(Long orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getMethodVersionId() {
        return methodVersionId;
    }

    public void setMethodVersionId(Long methodVersionId) {
        this.methodVersionId = methodVersionId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemOrderNo() {
        return itemOrderNo;
    }

    public void setItemOrderNo(Long itemOrderNo) {
        this.itemOrderNo = itemOrderNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemAliasName() {
        return itemAliasName;
    }

    public void setItemAliasName(String itemAliasName) {
        this.itemAliasName = itemAliasName;
    }

    public String getRevisionRule() {
        return revisionRule;
    }

    public void setRevisionRule(String revisionRule) {
        this.revisionRule = revisionRule;
    }

    public String getAvgResult() {
        return avgResult;
    }

    public void setAvgResult(String avgResult) {
        this.avgResult = avgResult;
    }

    public String getStandLowLimit() {
        return standLowLimit;
    }

    public void setStandLowLimit(String standLowLimit) {
        this.standLowLimit = standLowLimit;
    }

    public String getStandHighLimit() {
        return standHighLimit;
    }

    public void setStandHighLimit(String standHighLimit) {
        this.standHighLimit = standHighLimit;
    }

    public String getStandQty() {
        return standQty;
    }

    public void setStandQty(String standQty) {
        this.standQty = standQty;
    }

    public String getRn1() {
        return rn1;
    }

    public void setRn1(String rn1) {
        this.rn1 = rn1;
    }

    public String getRn2() {
        return rn2;
    }

    public void setRn2(String rn2) {
        this.rn2 = rn2;
    }

    public String getRn3() {
        return rn3;
    }

    public void setRn3(String rn3) {
        this.rn3 = rn3;
    }

    public String getRn4() {
        return rn4;
    }

    public void setRn4(String rn4) {
        this.rn4 = rn4;
    }

    public String getRn5() {
        return rn5;
    }

    public void setRn5(String rn5) {
        this.rn5 = rn5;
    }

    public String getRn6() {
        return rn6;
    }

    public void setRn6(String rn6) {
        this.rn6 = rn6;
    }

    public String getRn7() {
        return rn7;
    }

    public void setRn7(String rn7) {
        this.rn7 = rn7;
    }

    public String getRn8() {
        return rn8;
    }

    public void setRn8(String rn8) {
        this.rn8 = rn8;
    }

    public String getRn9() {
        return rn9;
    }

    public void setRn9(String rn9) {
        this.rn9 = rn9;
    }

    public String getRn10() {
        return rn10;
    }

    public void setRn10(String rn10) {
        this.rn10 = rn10;
    }

    public String getRn11() {
        return rn11;
    }

    public void setRn11(String rn11) {
        this.rn11 = rn11;
    }

    public String getRn12() {
        return rn12;
    }

    public void setRn12(String rn12) {
        this.rn12 = rn12;
    }

    public String getRn13() {
        return rn13;
    }

    public void setRn13(String rn13) {
        this.rn13 = rn13;
    }

    public String getRn14() {
        return rn14;
    }

    public void setRn14(String rn14) {
        this.rn14 = rn14;
    }

    public String getRn15() {
        return rn15;
    }

    public void setRn15(String rn15) {
        this.rn15 = rn15;
    }

    public String getRn16() {
        return rn16;
    }

    public void setRn16(String rn16) {
        this.rn16 = rn16;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getRepeatNo() {
        return repeatNo;
    }

    public void setRepeatNo(Long repeatNo) {
        this.repeatNo = repeatNo;
    }

    public Long getRepeatQty() {
        return repeatQty;
    }

    public void setRepeatQty(Long repeatQty) {
        this.repeatQty = repeatQty;
    }

    public String getOriginalResult() {
        return originalResult;
    }

    public void setOriginalResult(String originalResult) {
        this.originalResult = originalResult;
    }

    public String getReportResult() {
        return reportResult;
    }

    public void setReportResult(String reportResult) {
        this.reportResult = reportResult;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public String getReportDisplay() {
        return reportDisplay;
    }

    public void setReportDisplay(String reportDisplay) {
        this.reportDisplay = reportDisplay;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getIndexLowLimit() {
        return indexLowLimit;
    }

    public void setIndexLowLimit(String indexLowLimit) {
        this.indexLowLimit = indexLowLimit;
    }

    public String getIndexHighLimit() {
        return indexHighLimit;
    }

    public void setIndexHighLimit(String indexHighLimit) {
        this.indexHighLimit = indexHighLimit;
    }

    public String getIndexLimitUnit() {
        return indexLimitUnit;
    }

    public void setIndexLimitUnit(String indexLimitUnit) {
        this.indexLimitUnit = indexLimitUnit;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCycle() {
        return cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getQcCategoryCode() {
        return qcCategoryCode;
    }

    public void setQcCategoryCode(String qcCategoryCode) {
        this.qcCategoryCode = qcCategoryCode;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public String getScriptFlag() {
        return scriptFlag;
    }

    public void setScriptFlag(String scriptFlag) {
        this.scriptFlag = scriptFlag;
    }

    public String getResultCategory() {
        return resultCategory;
    }

    public void setResultCategory(String resultCategory) {
        this.resultCategory = resultCategory;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getSubmitFlag() {
        return submitFlag;
    }

    public void setSubmitFlag(String submitFlag) {
        this.submitFlag = submitFlag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getCorrelationValidateFlag() {
        return correlationValidateFlag;
    }

    public void setCorrelationValidateFlag(String correlationValidateFlag) {
        this.correlationValidateFlag = correlationValidateFlag;
    }

    public String getResultRule() {
        return resultRule;
    }

    public void setResultRule(String resultRule) {
        this.resultRule = resultRule;
    }

    public String getConvertFactor() {
        return convertFactor;
    }

    public void setConvertFactor(String convertFactor) {
        this.convertFactor = convertFactor;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getRn17() {
        return rn17;
    }

    public void setRn17(String rn17) {
        this.rn17 = rn17;
    }

    public String getRn18() {
        return rn18;
    }

    public void setRn18(String rn18) {
        this.rn18 = rn18;
    }

    public String getRn19() {
        return rn19;
    }

    public void setRn19(String rn19) {
        this.rn19 = rn19;
    }

    public String getRn20() {
        return rn20;
    }

    public void setRn20(String rn20) {
        this.rn20 = rn20;
    }

    public String getRn21() {
        return rn21;
    }

    public void setRn21(String rn21) {
        this.rn21 = rn21;
    }

    public String getSceneEquiptNo() {
        return sceneEquiptNo;
    }

    public void setSceneEquiptNo(String sceneEquiptNo) {
        this.sceneEquiptNo = sceneEquiptNo;
    }

    public String getSceneFileNo() {
        return sceneFileNo;
    }

    public void setSceneFileNo(String sceneFileNo) {
        this.sceneFileNo = sceneFileNo;
    }
}
