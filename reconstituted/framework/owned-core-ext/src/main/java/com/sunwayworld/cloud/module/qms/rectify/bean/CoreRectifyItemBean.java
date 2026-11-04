/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_RECTIFY_ITEM")
public class CoreRectifyItemBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private Long rectifyId;
    private Long orderNo;
    private LocalDate discoveryDate;
    private String beAuditOrgId;
    private String beAuditOrgName;
    private String rectifyGrade;
    private LocalDate deadline;
    private String promDesc;
    private String riskAssessment;
    private String rectifyGuidance;
    private String deviationReason;
    private LocalDate expectedDate;
    private String causeanAlysis;
    private String rectifyCarryPlan;
    private String rectifyResultDesc;
    private String remark;
    private String sourceType;
    private String processNo;
    private String reviewResult;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Long getRectifyId() {
        return this.rectifyId;
    }

    public void setRectifyId(Long rectifyId) {
        this.rectifyId = rectifyId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public LocalDate getDiscoveryDate() {
        return this.discoveryDate;
    }

    public void setDiscoveryDate(LocalDate discoveryDate) {
        this.discoveryDate = discoveryDate;
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

    public String getRectifyGrade() {
        return this.rectifyGrade;
    }

    public void setRectifyGrade(String rectifyGrade) {
        this.rectifyGrade = rectifyGrade;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getPromDesc() {
        return this.promDesc;
    }

    public void setPromDesc(String promDesc) {
        this.promDesc = promDesc;
    }

    public String getRiskAssessment() {
        return this.riskAssessment;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public String getRectifyGuidance() {
        return this.rectifyGuidance;
    }

    public void setRectifyGuidance(String rectifyGuidance) {
        this.rectifyGuidance = rectifyGuidance;
    }

    public String getDeviationReason() {
        return this.deviationReason;
    }

    public void setDeviationReason(String deviationReason) {
        this.deviationReason = deviationReason;
    }

    public LocalDate getExpectedDate() {
        return this.expectedDate;
    }

    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getCauseanAlysis() {
        return this.causeanAlysis;
    }

    public void setCauseanAlysis(String causeanAlysis) {
        this.causeanAlysis = causeanAlysis;
    }

    public String getRectifyCarryPlan() {
        return this.rectifyCarryPlan;
    }

    public void setRectifyCarryPlan(String rectifyCarryPlan) {
        this.rectifyCarryPlan = rectifyCarryPlan;
    }

    public String getRectifyResultDesc() {
        return this.rectifyResultDesc;
    }

    public void setRectifyResultDesc(String rectifyResultDesc) {
        this.rectifyResultDesc = rectifyResultDesc;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getProcessNo() {
        return this.processNo;
    }

    public void setProcessNo(String processNo) {
        this.processNo = processNo;
    }

    public String getReviewResult() {
        return this.reviewResult;
    }

    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
    }
}
