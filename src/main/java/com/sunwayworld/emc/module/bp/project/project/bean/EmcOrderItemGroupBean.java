/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.project.project.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_ORDER_ITEM_GROUP")
public class EmcOrderItemGroupBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 6073654199714691276L;
    @Id
    private Long id;
    private Long orderId;
    private String schemeName;
    private Long testId;
    private String testName;
    private Long testMethodId;
    private Long methodId;
    private String methodName;
    private Long methodVersionId;
    private String testMethodBasis;
    private Long itemGroupId;
    private String itemGroup;
    private String analysisOrgId;
    private String analysisOrgName;
    private Long qcCategoryId;
    private String qcCategoryCode;
    private String qcName;
    private String subPackageNo;
    private Long subcontractorId;
    @NotNull(defaultValue="0")
    private String dataSharing;
    private String inputUserId;
    private String inputUserName;
    private String monitorTmplId;
    private String orderCategory;
    private Long containerCategoryId;
    private String containerCategoryCode;
    private String containerCategoryName;
    private String containerGroup;
    private String fixative;
    private String originalOrderNo;
    private String storeCondition;
    private String abnormal;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    @NotNull(defaultValue="0")
    private String customerSpecified;
    private String batchNo;
    private String mobileId;
    private Long sortNum;
    private String qualification;
    private Long indexCategoryId;
    private String indexId;
    private String specifyMethod;
    private String taskLowLimit;
    private String taskHighLimit;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMobileId() {
        return this.mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getTestMethodId() {
        return this.testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public Long getItemGroupId() {
        return this.itemGroupId;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getQcCategoryCode() {
        return this.qcCategoryCode;
    }

    public void setQcCategoryCode(String qcCategoryCode) {
        this.qcCategoryCode = qcCategoryCode;
    }

    public String getQcName() {
        return this.qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public String getContainerCategoryCode() {
        return this.containerCategoryCode;
    }

    public void setContainerCategoryCode(String containerCategoryCode) {
        this.containerCategoryCode = containerCategoryCode;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getContainerCategoryName() {
        return this.containerCategoryName;
    }

    public void setContainerCategoryName(String containerCategoryName) {
        this.containerCategoryName = containerCategoryName;
    }

    public Long getContainerCategoryId() {
        return this.containerCategoryId;
    }

    public void setContainerCategoryId(Long containerCategoryId) {
        this.containerCategoryId = containerCategoryId;
    }

    public String getContainerGroup() {
        return this.containerGroup;
    }

    public void setContainerGroup(String containerGroup) {
        this.containerGroup = containerGroup;
    }

    public String getFixative() {
        return this.fixative;
    }

    public void setFixative(String fixative) {
        this.fixative = fixative;
    }

    public Long getTestId() {
        return this.testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getItemGroup() {
        return this.itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Long getSubcontractorId() {
        return this.subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getDataSharing() {
        return this.dataSharing;
    }

    public void setDataSharing(String dataSharing) {
        this.dataSharing = dataSharing;
    }

    public String getInputUserId() {
        return this.inputUserId;
    }

    public void setInputUserId(String inputUserId) {
        this.inputUserId = inputUserId;
    }

    public String getInputUserName() {
        return this.inputUserName;
    }

    public void setInputUserName(String inputUserName) {
        this.inputUserName = inputUserName;
    }

    public String getMonitorTmplId() {
        return this.monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return this.orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Long getMethodId() {
        return this.methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTestMethodBasis() {
        return this.testMethodBasis;
    }

    public void setTestMethodBasis(String testMethodBasis) {
        this.testMethodBasis = testMethodBasis;
    }

    public Long getMethodVersionId() {
        return this.methodVersionId;
    }

    public void setMethodVersionId(Long methodVersionId) {
        this.methodVersionId = methodVersionId;
    }

    public String getAnalysisOrgId() {
        return this.analysisOrgId;
    }

    public void setAnalysisOrgId(String analysisOrgId) {
        this.analysisOrgId = analysisOrgId;
    }

    public String getAnalysisOrgName() {
        return this.analysisOrgName;
    }

    public void setAnalysisOrgName(String analysisOrgName) {
        this.analysisOrgName = analysisOrgName;
    }

    public Long getQcCategoryId() {
        return this.qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getSubPackageNo() {
        return this.subPackageNo;
    }

    public void setSubPackageNo(String subPackageNo) {
        this.subPackageNo = subPackageNo;
    }

    public String getOriginalOrderNo() {
        return this.originalOrderNo;
    }

    public void setOriginalOrderNo(String originalOrderNo) {
        this.originalOrderNo = originalOrderNo;
    }

    public String getStoreCondition() {
        return this.storeCondition;
    }

    public void setStoreCondition(String storeCondition) {
        this.storeCondition = storeCondition;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getCustomerSpecified() {
        return this.customerSpecified;
    }

    public void setCustomerSpecified(String customerSpecified) {
        this.customerSpecified = customerSpecified;
    }

    public String getBatchNo() {
        return this.batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Long getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getQualification() {
        return this.qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Long getIndexCategoryId() {
        return this.indexCategoryId;
    }

    public void setIndexCategoryId(Long indexCategoryId) {
        this.indexCategoryId = indexCategoryId;
    }

    public String getIndexId() {
        return this.indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

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

