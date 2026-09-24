package com.sunwayworld.emc.module.bp.bp.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 样品测试
 *
 * @author magh@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_ORDER_TASK")
public class EmcOrderTaskBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -4465679374350662131L;
    @Id
    private Long id;// 主键

    private Long folderId;// 点位编码

    private Long orderId;// 样品编码

    private Long projId;// 项目编码

    private Long orderContainerId; // 样品容器ID

    private String schemeName;// 方案名称

    private Long testId;// 测试编码

    private String testName;// 测试名称

    private Long testMethodId;// 测试方法编码

    private Long methodId;// 方法编码

    private String methodName;// 方法名称

    private Long methodVersionId;// 方法版本编码

    private String testMethodBasis;// 检测方法及依据

    private Long containerCategoryId;// 容器类型主键

    private String containerCategoryCode; // 容器类型编码

    private String containerCategoryName;// 容器类型名称

    private String containerGroup;// 容器组

    private String fixative;// 固定剂

    private Long qcCategoryId;// QC类型主键

    @NotNull(defaultValue = "N/A")
    private String qcCategoryCode; // QC类型编码

    @NotNull(defaultValue = "N/A")
    private String qcName; // QC类型中文

    private String subPackageNo;// 分包单号

    private Long subcontractorId;// 分包商ID

    @NotNull(defaultValue = Constant.NO)
    private String dataSharing;// 是否数据共享

    private String analysisType;// 分析类型
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisDate; // 分析日期

    private Long equiptId;// 现场仪器

    private String analysisUserId;// 分析员编码

    private String analysisUserName;// 分析员

    private String partakeUserId;// 共同完成人编码

    private String partakeUserName;// 共同完成人

    private String assistEquiptId;// 辅助仪器

    private String assistEquiptName;// 辅助仪器名称

    private Long cupNo;// 杯号

    private String runId;// 批号

    private String equiptAcquNo;// 仪器采集编号

    private String monitorTmplId;// 监测模板ID

    private String orderCategory;// 样品类型

    private String analysisOrgId;// 分析科室编码

    private String analysisOrgName;// 分析科室名称

    @NotNull(defaultValue = EmcBizConstant.ORDERTASK_STATUS_DRAFT)
    private String status;// 状态:(draft、preLogged、logged、approve、audit、done)

    private Long stockId;// 库存ID

    private String stockBatchNo;// 库存批号

    private Long indexCategoryId;// 指标分类id

    private String indexId;// 样品指标id

    private String inputUserId; // 录入人编码

    private String inputUserName; // 录入人名称

    private Long parentId; // 父样编码

    private String source; // 来源

    private String confirmUserName; // 采样确认人名称

    private String receiveUserName; // 样品接收人名称

    private String mobileId; // 移动端样品测试ID

    @NotNull(defaultValue = Constant.NO)
    private String scanned; // 已扫描

    private String remarks; // 备注

    private String samplingEquiptId; // 采样设备编号

    private String samplingEquiptName; // 采样设备名称

    private String originalOrderNo; // 原样品编号

    private String storeCondition; // 保存条件

    private String samplingSubpackage; // 是否为现场分包

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    /**
     * 广州分析测试中心项目
     */
    @NotNull(defaultValue = Constant.NO)
    private String customerSpecified;// 是否客户指定

    private String cusModifyReason;// 客户指定在分析或采样中修改方法原因
    // 广州分析测试中心项目

    /**
     * 江苏新锐
     */
    @NotNull(defaultValue = Constant.NO)
    private String sampleReceiverFlag;//分析员领用状态(新锐)

    private String sampleReceiverId;// 领样人编码

    private String sampleReceiverName;// 领样人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime sampleReceiverDate;// 领样日期
    private String sampleReceiverTime;// 领样时间
    private String calibrationEquiptId;// 校准仪器id
    private String calibrationEquiptName;// 校准仪器名称
    //江苏新锐

    /**
     * 上海水文
     */
    @NotNull(defaultValue = Constant.NO)
    private String damage;// 瓶子是否损坏
    //上海水文字段

    /**
     * 秋毫检测
     */
    @NotNull(defaultValue = Constant.NO)
    private String requirements;// 是否符合要求

    private String deviationCauses;//偏离原因
    //秋毫检测字段

    @NotNull(defaultValue = Constant.NO)
    private String assign;// 是否分配

    private String pretreatWay;// 前处理方式

    private String specifyMethod;// 指定方法

    private String taskLowLimit;// 任务下限

    private String taskHighLimit;// 任务上限


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

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getOrderContainerId() {
        return orderContainerId;
    }

    public void setOrderContainerId(Long orderContainerId) {
        this.orderContainerId = orderContainerId;
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

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getContainerCategoryId() {
        return containerCategoryId;
    }

    public void setContainerCategoryId(Long containerCategoryId) {
        this.containerCategoryId = containerCategoryId;
    }

    public String getContainerGroup() {
        return containerGroup;
    }

    public void setContainerGroup(String containerGroup) {
        this.containerGroup = containerGroup;
    }

    public String getFixative() {
        return fixative;
    }

    public void setFixative(String fixative) {
        this.fixative = fixative;
    }

    public Long getMethodVersionId() {
        return methodVersionId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public void setMethodVersionId(Long methodVersionId) {
        this.methodVersionId = methodVersionId;
    }

    public Long getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getSubPackageNo() {
        return subPackageNo;
    }

    public Long getSubcontractorId() {
        return subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public void setSubPackageNo(String subPackageNo) {
        this.subPackageNo = subPackageNo;
    }

    public String getDataSharing() {
        return dataSharing;
    }

    public void setDataSharing(String dataSharing) {
        this.dataSharing = dataSharing;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getTestMethodBasis() {
        return testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getContainerCategoryName() {
        return containerCategoryName;
    }

    public void setContainerCategoryName(String containerCategoryName) {
        this.containerCategoryName = containerCategoryName;
    }

    public String getContainerCategoryCode() {
        return containerCategoryCode;
    }

    public void setContainerCategoryCode(String containerCategoryCode) {
        this.containerCategoryCode = containerCategoryCode;
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

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getAnalysisUserId() {
        return analysisUserId;
    }

    public void setAnalysisUserId(String analysisUserId) {
        this.analysisUserId = analysisUserId;
    }

    public String getAnalysisUserName() {
        return analysisUserName;
    }

    public void setAnalysisUserName(String analysisUserName) {
        this.analysisUserName = analysisUserName;
    }

    public String getPartakeUserId() {
        return partakeUserId;
    }

    public void setPartakeUserId(String partakeUserId) {
        this.partakeUserId = partakeUserId;
    }

    public String getPartakeUserName() {
        return partakeUserName;
    }

    public void setPartakeUserName(String partakeUserName) {
        this.partakeUserName = partakeUserName;
    }

    public String getAssistEquiptId() {
        return assistEquiptId;
    }

    public void setAssistEquiptId(String assistEquiptId) {
        this.assistEquiptId = assistEquiptId;
    }

    public String getAssistEquiptName() {
        return assistEquiptName;
    }

    public void setAssistEquiptName(String assistEquiptName) {
        this.assistEquiptName = assistEquiptName;
    }

    public Long getCupNo() {
        return cupNo;
    }

    public void setCupNo(Long cupNo) {
        this.cupNo = cupNo;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getEquiptAcquNo() {
        return equiptAcquNo;
    }

    public void setEquiptAcquNo(String equiptAcquNo) {
        this.equiptAcquNo = equiptAcquNo;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getAnalysisOrgId() {
        return analysisOrgId;
    }

    public void setAnalysisOrgId(String analysisOrgId) {
        this.analysisOrgId = analysisOrgId;
    }

    public String getAnalysisOrgName() {
        return analysisOrgName;
    }

    public void setAnalysisOrgName(String analysisOrgName) {
        this.analysisOrgName = analysisOrgName;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getStockBatchNo() {
        return stockBatchNo;
    }

    public void setStockBatchNo(String stockBatchNo) {
        this.stockBatchNo = stockBatchNo;
    }

    public Long getIndexCategoryId() {
        return indexCategoryId;
    }

    public void setIndexCategoryId(Long indexCategoryId) {
        this.indexCategoryId = indexCategoryId;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getInputUserId() {
        return inputUserId;
    }

    public void setInputUserId(String inputUserId) {
        this.inputUserId = inputUserId;
    }

    public String getInputUserName() {
        return inputUserName;
    }

    public void setInputUserName(String inputUserName) {
        this.inputUserName = inputUserName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getConfirmUserName() {
        return confirmUserName;
    }

    public void setConfirmUserName(String confirmUserName) {
        this.confirmUserName = confirmUserName;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getScanned() {
        return scanned;
    }

    public void setScanned(String scanned) {
        this.scanned = scanned;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSamplingEquiptId() {
        return samplingEquiptId;
    }

    public void setSamplingEquiptId(String samplingEquiptId) {
        this.samplingEquiptId = samplingEquiptId;
    }

    public String getSamplingEquiptName() {
        return samplingEquiptName;
    }

    public void setSamplingEquiptName(String samplingEquiptName) {
        this.samplingEquiptName = samplingEquiptName;
    }

    public String getOriginalOrderNo() {
        return originalOrderNo;
    }

    public void setOriginalOrderNo(String originalOrderNo) {
        this.originalOrderNo = originalOrderNo;
    }

    public String getStoreCondition() {
        return storeCondition;
    }

    public void setStoreCondition(String storeCondition) {
        this.storeCondition = storeCondition;
    }

    public String getSamplingSubpackage() {
        return samplingSubpackage;
    }

    public void setSamplingSubpackage(String samplingSubpackage) {
        this.samplingSubpackage = samplingSubpackage;
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

    public String getCustomerSpecified() {
        return customerSpecified;
    }

    public void setCustomerSpecified(String customerSpecified) {
        this.customerSpecified = customerSpecified;
    }

    public String getCusModifyReason() {
        return cusModifyReason;
    }

    public void setCusModifyReason(String cusModifyReason) {
        this.cusModifyReason = cusModifyReason;
    }

    public String getCalibrationEquiptId() {
        return calibrationEquiptId;
    }

    public void setCalibrationEquiptId(String calibrationEquiptId) {
        this.calibrationEquiptId = calibrationEquiptId;
    }

    public String getCalibrationEquiptName() {
        return calibrationEquiptName;
    }

    public void setCalibrationEquiptName(String calibrationEquiptName) {
        this.calibrationEquiptName = calibrationEquiptName;
    }

    public String getSampleReceiverFlag() {
        return sampleReceiverFlag;
    }

    public void setSampleReceiverFlag(String sampleReceiverFlag) {
        this.sampleReceiverFlag = sampleReceiverFlag;
    }

	public String getDamage() {
		return damage;
	}

	public void setDamage(String damage) {
		this.damage = damage;
	}

	public String getRequirements() {
		return requirements;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public String getDeviationCauses() {
		return deviationCauses;
	}

	public void setDeviationCauses(String deviationCauses) {
		this.deviationCauses = deviationCauses;
	}

    public String getSampleReceiverId() {
        return sampleReceiverId;
    }

    public void setSampleReceiverId(String sampleReceiverId) {
        this.sampleReceiverId = sampleReceiverId;
    }

    public String getSampleReceiverName() {
        return sampleReceiverName;
    }

    public void setSampleReceiverName(String sampleReceiverName) {
        this.sampleReceiverName = sampleReceiverName;
    }

    public LocalDateTime getSampleReceiverDate() {
        return sampleReceiverDate;
    }

    public void setSampleReceiverDate(LocalDateTime sampleReceiverDate) {
        this.sampleReceiverDate = sampleReceiverDate;
    }

    public String getSampleReceiverTime() {
        return sampleReceiverTime;
    }

    public void setSampleReceiverTime(String sampleReceiverTime) {
        this.sampleReceiverTime = sampleReceiverTime;
    }

    public String getAssign() {
        return assign;
    }

    public void setAssign(String assign) {
        this.assign = assign;
    }

    public String getPretreatWay() {
        return pretreatWay;
    }

    public void setPretreatWay(String pretreatWay) {
        this.pretreatWay = pretreatWay;
    }

    public String getSpecifyMethod() {
        return specifyMethod;
    }

    public void setSpecifyMethod(String specifyMethod) {
        this.specifyMethod = specifyMethod;
    }

    public String getTaskLowLimit() {
        return taskLowLimit;
    }

    public void setTaskLowLimit(String taskLowLimit) {
        this.taskLowLimit = taskLowLimit;
    }

    public String getTaskHighLimit() {
        return taskHighLimit;
    }

    public void setTaskHighLimit(String taskHighLimit) {
        this.taskHighLimit = taskHighLimit;
    }
}
