/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_QUALITY_CONTROL")
public class CoreQualityControlBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    @NotNull(defaultValue="draft")
    private String processStatus;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String qcNo;
    private String qcType;
    private String qcCorpName;
    private LocalDate carryDate;
    private String qcContent;
    private String qcBasis;
    private String qcResult;
    private LocalDate qcDeadline;
    private String reviewUserId;
    private String reviewUserName;
    private String leaderUserId;
    private String leaderUserName;
    private String evnCondition;
    private String resourceConfig;
    private String qcPlanId;
    private String planFeasibility;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getProcessStatus() {
        return this.processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
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

    public String getQcNo() {
        return this.qcNo;
    }

    public void setQcNo(String qcNo) {
        this.qcNo = qcNo;
    }

    public String getQcType() {
        return this.qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public String getQcCorpName() {
        return this.qcCorpName;
    }

    public void setQcCorpName(String qcCorpName) {
        this.qcCorpName = qcCorpName;
    }

    public LocalDate getCarryDate() {
        return this.carryDate;
    }

    public void setCarryDate(LocalDate carryDate) {
        this.carryDate = carryDate;
    }

    public String getQcContent() {
        return this.qcContent;
    }

    public void setQcContent(String qcContent) {
        this.qcContent = qcContent;
    }

    public String getQcBasis() {
        return this.qcBasis;
    }

    public void setQcBasis(String qcBasis) {
        this.qcBasis = qcBasis;
    }

    public String getQcResult() {
        return this.qcResult;
    }

    public void setQcResult(String qcResult) {
        this.qcResult = qcResult;
    }

    public LocalDate getQcDeadline() {
        return this.qcDeadline;
    }

    public void setQcDeadline(LocalDate qcDeadline) {
        this.qcDeadline = qcDeadline;
    }

    public String getReviewUserId() {
        return this.reviewUserId;
    }

    public void setReviewUserId(String reviewUserId) {
        this.reviewUserId = reviewUserId;
    }

    public String getReviewUserName() {
        return this.reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }

    public String getLeaderUserId() {
        return this.leaderUserId;
    }

    public void setLeaderUserId(String leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getLeaderUserName() {
        return this.leaderUserName;
    }

    public void setLeaderUserName(String leaderUserName) {
        this.leaderUserName = leaderUserName;
    }

    public String getEvnCondition() {
        return this.evnCondition;
    }

    public void setEvnCondition(String evnCondition) {
        this.evnCondition = evnCondition;
    }

    public String getResourceConfig() {
        return this.resourceConfig;
    }

    public void setResourceConfig(String resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPlanFeasibility() {
        return this.planFeasibility;
    }

    public void setPlanFeasibility(String planFeasibility) {
        this.planFeasibility = planFeasibility;
    }

    public String getQcPlanId() {
        return this.qcPlanId;
    }

    public void setQcPlanId(String qcPlanId) {
        this.qcPlanId = qcPlanId;
    }
}
