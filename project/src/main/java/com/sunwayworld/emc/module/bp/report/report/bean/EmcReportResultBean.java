package com.sunwayworld.emc.module.bp.report.report.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 报告结果表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@Table("T_EMC_REPORT_RESULT")
public class EmcReportResultBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 3521939298314160723L;

    @Id
    private Long id;// 主键
    private Long reportId;// 报告编码
    private Long folderId;// 点位编码
    private Long orderId;// 样品编码
    private Long projId;// 项目ID
    private Long orderTaskId;// 样品测试编码
    private String runId; // 批号
    private String folderNo;// 点位编号
    private String orderNo;// 样品编号
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sampleBeginDate;// 采样开始日期
    private String sampleBeginTime;// 采样开始时间
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sampleCompleteDate;// 采样完成日期
    private String sampleCompleteTime;// 采样完成时间
    private String monitorTmplId;// 监测模板ID
    private String monitorPointNo;// 监测点编号
    private String monitorPointName;// 监测点名称
    private String clientOrderName;// 客户样品名称
    private Long sampleId;// 采样单编码
    private String orderDesc;// 样品描述
    private Long cycle;// 周期
    private Long frequency;// 频次
    private String orderCategory;// 样品类型
    private String schemeName;// 方案名称
    private Long testMethodId;// 测试方法编码
    private String testName;// 测试名称
    private Long testId;// 方法id
    private Long methodId;// 方法编码
    private String methodName;// 方法名称
    private Long methodVersionId;// 方法版本编码
    private Long itemId;// 分析项编码
    private Long itemOrderNo;// 分析项排序码
    private String itemName;// 分析项
    private String itemAliasName;// 分析项别名
    private String revisionRule;// 修约规则
    private String unitName;// 计量单位<存计量单位名称>
    private Long repeatNo;// 重复数（1,2,3...）
    private Long repeatQty;// 重复项
    private String originalResult;// 原始结果
    private String reportResult;// 报出结果
    private String resultStatus;// 结果状态
    private String analysisType;// 分析类型
    private String lowLimit;// 检出低限
    private String highLimit;// 检出高限
    private String indexLowLimit;// 指标低限
    private String indexHighLimit;// 指标高限
    private String indexLimitUnit; // 指标高低限单位
    private String dataType; // 数据类型
    private String reportDisplay; // 报告中显示
    private Long indexId; // 指标编码
    private String exceedStandard;// 是否超标
    private String esMultiple;// 超标倍数
    private String esAmount;// 超标量
    private String invalidResult; //无效结果（针对油烟小于最大值四分之一的即为无效结果）

    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
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

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getFolderNo() {
        return folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public LocalDate getSampleBeginDate() {
        return sampleBeginDate;
    }

    public void setSampleBeginDate(LocalDate sampleBeginDate) {
        this.sampleBeginDate = sampleBeginDate;
    }

    public LocalDate getSampleCompleteDate() {
        return sampleCompleteDate;
    }

    public void setSampleCompleteDate(LocalDate sampleCompleteDate) {
        this.sampleCompleteDate = sampleCompleteDate;
    }

    public String getSampleBeginTime() {
        return sampleBeginTime;
    }

    public void setSampleBeginTime(String sampleBeginTime) {
        this.sampleBeginTime = sampleBeginTime;
    }

    public String getSampleCompleteTime() {
        return sampleCompleteTime;
    }

    public void setSampleCompleteTime(String sampleCompleteTime) {
        this.sampleCompleteTime = sampleCompleteTime;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getMonitorPointNo() {
        return monitorPointNo;
    }

    public void setMonitorPointNo(String monitorPointNo) {
        this.monitorPointNo = monitorPointNo;
    }

    public String getMonitorPointName() {
        return monitorPointName;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public String getClientOrderName() {
        return clientOrderName;
    }

    public void setClientOrderName(String clientOrderName) {
        this.clientOrderName = clientOrderName;
    }

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
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

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getReportDisplay() {
        return reportDisplay;
    }

    public void setReportDisplay(String reportDisplay) {
        this.reportDisplay = reportDisplay;
    }

    public Long getIndexId() {
        return indexId;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
    }

    public String getExceedStandard() {
        return exceedStandard;
    }

    public void setExceedStandard(String exceedStandard) {
        this.exceedStandard = exceedStandard;
    }

    public String getEsMultiple() {
        return esMultiple;
    }

    public void setEsMultiple(String esMultiple) {
        this.esMultiple = esMultiple;
    }

    public String getEsAmount() {
        return esAmount;
    }

    public void setEsAmount(String esAmount) {
        this.esAmount = esAmount;
    }

    public String getInvalidResult() {
        return invalidResult;
    }

    public void setInvalidResult(String invalidResult) {
        this.invalidResult = invalidResult;
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

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

}
