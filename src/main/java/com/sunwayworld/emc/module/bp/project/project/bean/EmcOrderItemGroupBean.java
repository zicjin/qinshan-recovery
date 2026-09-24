package com.sunwayworld.emc.module.bp.project.project.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table("T_EMC_ORDER_ITEM_GROUP")
    public class EmcOrderItemGroupBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 6073654199714691276L;

    @Id
    private Long id;// 主键

    private Long orderId;// 样品编码

    private String schemeName;// 方案名称

    private Long testId;// 测试编码

    private String testName;// 测试名称

    private Long testMethodId;// 测试方法编码

    private Long methodId;// 方法编码

    private String methodName;// 方法名称

    private Long methodVersionId;// 方法版本编码

    private String testMethodBasis;// 检测方法及依据

    private Long itemGroupId;// 分析项组编码

    private String itemGroup;// 分析项组

    private String analysisOrgId;// 分析科室编码

    private String analysisOrgName;// 分析科室名称

    private Long qcCategoryId;// QC类型主键

    private String qcCategoryCode; // QC类型编码

    private String qcName; // QC类型中文

    private String subPackageNo;// 分包单号

    private Long subcontractorId;// 分包商ID

    @NotNull(defaultValue = Constant.NO)
    private String dataSharing;// 是否数据共享

    private String inputUserId; // 录入人编码

    private String inputUserName; // 录入人名称

    private String monitorTmplId; // 监测模板ID

    private String orderCategory; // 样品类型

    private Long containerCategoryId;// 容器类型主键

    private String containerCategoryCode; // 容器类型编码

    private String containerCategoryName;// 容器类型名称

    private String containerGroup;// 容器组

    private String fixative;// 固定剂

    private String originalOrderNo; // 原样品编号

    private String storeCondition; // 保存条件

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

    private String batchNo;//批号

    private String mobileId; // 移动端样品分析项组ID

    private Long sortNum; // 排序码

    private String qualification; // 是否有资质

    private Long indexCategoryId;// 指标分类id

    private String indexId;// 样品指标id

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public Long getItemGroupId() {
        return itemGroupId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
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

    public String getContainerCategoryCode() {
        return containerCategoryCode;
    }

    public void setContainerCategoryCode(String containerCategoryCode) {
        this.containerCategoryCode = containerCategoryCode;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getContainerCategoryName() {
        return containerCategoryName;
    }

    public void setContainerCategoryName(String containerCategoryName) {
        this.containerCategoryName = containerCategoryName;
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

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Long getSubcontractorId() {
        return subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getDataSharing() {
        return dataSharing;
    }

    public void setDataSharing(String dataSharing) {
        this.dataSharing = dataSharing;
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

    public String getTestMethodBasis() {
        return testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public Long getMethodVersionId() {
        return methodVersionId;
    }

    public void setMethodVersionId(Long methodVersionId) {
        this.methodVersionId = methodVersionId;
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

    public Long getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getSubPackageNo() {
        return subPackageNo;
    }

    public void setSubPackageNo(String subPackageNo) {
        this.subPackageNo = subPackageNo;
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

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getCustomerSpecified() {
        return customerSpecified;
    }

    public void setCustomerSpecified(String customerSpecified) {
        this.customerSpecified = customerSpecified;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
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

    private String abnormal;
    private String specifyMethod;
    private String taskLowLimit;
    private String taskHighLimit;

    public String getSpecifyMethod() {
        return this.specifyMethod;
    }
    public void setSpecifyMethod(String specifyMethod) {
        this.specifyMethod = specifyMethod;
    }
    public String getAbnormal() {
        return this.abnormal;
    }
    public void setAbnormal(String abnormal) {
        this.abnormal = abnormal;
    }
    public String getTaskLowLimit() {
        return this.taskLowLimit;
    }
    public void setTaskLowLimit(String taskLowLimit) {
        this.taskLowLimit = taskLowLimit;
    }
    public String getTaskHighLimit() {
        return this.taskHighLimit;
    }
    public void setTaskHighLimit(String taskHighLimit) {
        this.taskHighLimit = taskHighLimit;
    }
}
