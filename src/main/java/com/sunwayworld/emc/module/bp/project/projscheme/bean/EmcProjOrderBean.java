/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.project.projscheme.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_PROJ_ORDER")
public class EmcProjOrderBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 6428765035601708948L;
    @Id
    private Long id;
    private Long projSchemeId;
    private Long sampleNum;
    private Long testId;
    private String schemeName;
    private Long methodId;
    private String subPackageName;
    private Long itemGroupId;
    private String itemGroup;
    private Long indexCategoryId;
    private String indexId;
    private Long subcontractorId;
    private String subPackageNo;
    @NotNull(defaultValue="0")
    private String dataSharing;
    private Double sampleFee;
    private Double analysisFee;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String indexSetId;
    private String emissionLowlimit;
    private String emissionHighlimit;
    @NotNull(defaultValue="0")
    private String customerSpecified;
    private Long sortNum;
    private String abnormal;
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

    public Long getProjSchemeId() {
        return this.projSchemeId;
    }

    public void setProjSchemeId(Long projSchemeId) {
        this.projSchemeId = projSchemeId;
    }

    public Long getSampleNum() {
        return this.sampleNum;
    }

    public void setSampleNum(Long sampleNum) {
        this.sampleNum = sampleNum;
    }

    public Long getTestId() {
        return this.testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Long getMethodId() {
        return this.methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getSubPackageName() {
        return this.subPackageName;
    }

    public void setSubPackageName(String subPackageName) {
        this.subPackageName = subPackageName;
    }

    public Long getItemGroupId() {
        return this.itemGroupId;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getItemGroup() {
        return this.itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
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

    public Double getSampleFee() {
        return this.sampleFee;
    }

    public void setSampleFee(Double sampleFee) {
        this.sampleFee = sampleFee;
    }

    public Double getAnalysisFee() {
        return this.analysisFee;
    }

    public void setAnalysisFee(Double analysisFee) {
        this.analysisFee = analysisFee;
    }

    public Long getSubcontractorId() {
        return this.subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getSubPackageNo() {
        return this.subPackageNo;
    }

    public void setSubPackageNo(String subPackageNo) {
        this.subPackageNo = subPackageNo;
    }

    public String getDataSharing() {
        return this.dataSharing;
    }

    public void setDataSharing(String dataSharing) {
        this.dataSharing = dataSharing;
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

    public String getEmissionLowlimit() {
        return this.emissionLowlimit;
    }

    public void setEmissionLowlimit(String emissionLowlimit) {
        this.emissionLowlimit = emissionLowlimit;
    }

    public String getEmissionHighlimit() {
        return this.emissionHighlimit;
    }

    public void setEmissionHighlimit(String emissionHighlimit) {
        this.emissionHighlimit = emissionHighlimit;
    }

    public String getIndexSetId() {
        return this.indexSetId;
    }

    public void setIndexSetId(String indexSetId) {
        this.indexSetId = indexSetId;
    }

    public Long getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getAbnormal() {
        return this.abnormal;
    }

    public void setAbnormal(String abnormal) {
        this.abnormal = abnormal;
    }

    public String getSpecifyMethod() {
        return this.specifyMethod;
    }

    public void setSpecifyMethod(String specifyMethod) {
        this.specifyMethod = specifyMethod;
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

