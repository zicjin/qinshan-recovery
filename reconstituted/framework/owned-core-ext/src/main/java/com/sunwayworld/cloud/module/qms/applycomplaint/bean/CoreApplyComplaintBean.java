/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_APPLY_COMPLAINT")
public class CoreApplyComplaintBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String applyComplaintNo;
    private String applyComplaintCorp;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyComplaintDate;
    private String folderNo;
    private String matName;
    private String delegateName;
    private String delegatePhone;
    private String batchNo;
    private String producCorp;
    private String sampledCorp;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportPrintDate;
    private String applyComplaintType;
    @Clob
    private String investigationResult;
    @Clob
    private String suggestion;
    @Clob
    private String qmResult;
    @Clob
    private String feedback;
    @Clob
    private String applyComplaintContent;
    private String investigationUser;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime investigationDate;
    private String beAuditOrgNo;
    private String beAuditOrgName;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyComplaintNo() {
        return this.applyComplaintNo;
    }

    public void setApplyComplaintNo(String applyComplaintNo) {
        this.applyComplaintNo = applyComplaintNo;
    }

    public String getApplyComplaintCorp() {
        return this.applyComplaintCorp;
    }

    public void setApplyComplaintCorp(String applyComplaintCorp) {
        this.applyComplaintCorp = applyComplaintCorp;
    }

    public LocalDateTime getApplyComplaintDate() {
        return this.applyComplaintDate;
    }

    public void setApplyComplaintDate(LocalDateTime applyComplaintDate) {
        this.applyComplaintDate = applyComplaintDate;
    }

    public String getFolderNo() {
        return this.folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public String getMatName() {
        return this.matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getDelegateName() {
        return this.delegateName;
    }

    public void setDelegateName(String delegateName) {
        this.delegateName = delegateName;
    }

    public String getDelegatePhone() {
        return this.delegatePhone;
    }

    public void setDelegatePhone(String delegatePhone) {
        this.delegatePhone = delegatePhone;
    }

    public String getBatchNo() {
        return this.batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getProducCorp() {
        return this.producCorp;
    }

    public void setProducCorp(String producCorp) {
        this.producCorp = producCorp;
    }

    public String getSampledCorp() {
        return this.sampledCorp;
    }

    public void setSampledCorp(String sampledCorp) {
        this.sampledCorp = sampledCorp;
    }

    public LocalDateTime getReportPrintDate() {
        return this.reportPrintDate;
    }

    public void setReportPrintDate(LocalDateTime reportPrintDate) {
        this.reportPrintDate = reportPrintDate;
    }

    public String getApplyComplaintType() {
        return this.applyComplaintType;
    }

    public void setApplyComplaintType(String applyComplaintType) {
        this.applyComplaintType = applyComplaintType;
    }

    public String getInvestigationResult() {
        return this.investigationResult;
    }

    public void setInvestigationResult(String investigationResult) {
        this.investigationResult = investigationResult;
    }

    public String getSuggestion() {
        return this.suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getQmResult() {
        return this.qmResult;
    }

    public void setQmResult(String qmResult) {
        this.qmResult = qmResult;
    }

    public String getFeedback() {
        return this.feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getApplyComplaintContent() {
        return this.applyComplaintContent;
    }

    public void setApplyComplaintContent(String applyComplaintContent) {
        this.applyComplaintContent = applyComplaintContent;
    }

    public String getInvestigationUser() {
        return this.investigationUser;
    }

    public void setInvestigationUser(String investigationUser) {
        this.investigationUser = investigationUser;
    }

    public LocalDateTime getInvestigationDate() {
        return this.investigationDate;
    }

    public void setInvestigationDate(LocalDateTime investigationDate) {
        this.investigationDate = investigationDate;
    }

    public String getBeAuditOrgNo() {
        return this.beAuditOrgNo;
    }

    public void setBeAuditOrgNo(String beAuditOrgNo) {
        this.beAuditOrgNo = beAuditOrgNo;
    }

    public String getBeAuditOrgName() {
        return this.beAuditOrgName;
    }

    public void setBeAuditOrgName(String beAuditOrgName) {
        this.beAuditOrgName = beAuditOrgName;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
