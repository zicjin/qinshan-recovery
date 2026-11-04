/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_CUSTOM_SRT_SURVEY")
public class CoreCustomSrtSurveyBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String surveyNo;
    private String auditUserId;
    private String auditUserName;
    private LocalDate auditDate;
    @Clob
    private String qsOrgAdvice;
    @Clob
    private String qmAdvice;
    private String remark;
    private String beAuditOrgId;
    private String beAuditOrgName;
    @Clob
    private String beAuditOrgAdvice;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getSurveyNo() {
        return this.surveyNo;
    }

    public void setSurveyNo(String surveyNo) {
        this.surveyNo = surveyNo;
    }

    public String getAuditUserId() {
        return this.auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return this.auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public LocalDate getAuditDate() {
        return this.auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
    }

    public String getQsOrgAdvice() {
        return this.qsOrgAdvice;
    }

    public void setQsOrgAdvice(String qsOrgAdvice) {
        this.qsOrgAdvice = qsOrgAdvice;
    }

    public String getQmAdvice() {
        return this.qmAdvice;
    }

    public void setQmAdvice(String qmAdvice) {
        this.qmAdvice = qmAdvice;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBeAuditOrgId() {
        return this.beAuditOrgId;
    }

    public void setBeAuditOrgId(String beAuditOrgId) {
        this.beAuditOrgId = beAuditOrgId;
    }

    public String getBeAuditOrgName() {
        return this.beAuditOrgName;
    }

    public void setBeAuditOrgName(String beAuditOrgName) {
        this.beAuditOrgName = beAuditOrgName;
    }

    public String getBeAuditOrgAdvice() {
        return this.beAuditOrgAdvice;
    }

    public void setBeAuditOrgAdvice(String beAuditOrgAdvice) {
        this.beAuditOrgAdvice = beAuditOrgAdvice;
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
}
