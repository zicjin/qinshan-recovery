/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_LAUNCH_NEW_PROJECT")
public class CoreLaunchNewProjectBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
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
    @NotNull(defaultValue="draft")
    private String processStatus;
    private String projectNo;
    private String projectName;
    @Clob
    private String reason;
    @Clob
    private String resources;
    private String carryOrgId;
    private String carryOrgName;
    @Clob
    private String groupReviewAdvice;
    @Clob
    private String groupReviewResult;
    @Clob
    private String qmAdvice;
    @Clob
    private String carryTestContent;
    @Clob
    private String carryTestResult;
    @Clob
    private String reviewContent;
    @Clob
    private String reviewImprovedContent;
    @Clob
    private String reviewTechnicalAdvice;
    @Clob
    private String reviewQmAdvice;
    @Clob
    private String reviewRemark;
    private String confirmStandardName;
    private String confirmStandardNo;
    @Clob
    private String confirmContent;
    @Clob
    private String confirmResult;
    @Clob
    private String confirmQmAdvice;
    @Clob
    private String confirmLeaderAdvice;
    @Clob
    private String confirmRemark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String getProcessStatus() {
        return this.processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getProjectNo() {
        return this.projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResources() {
        return this.resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getCarryOrgId() {
        return this.carryOrgId;
    }

    public void setCarryOrgId(String carryOrgId) {
        this.carryOrgId = carryOrgId;
    }

    public String getCarryOrgName() {
        return this.carryOrgName;
    }

    public void setCarryOrgName(String carryOrgName) {
        this.carryOrgName = carryOrgName;
    }

    public String getGroupReviewAdvice() {
        return this.groupReviewAdvice;
    }

    public void setGroupReviewAdvice(String groupReviewAdvice) {
        this.groupReviewAdvice = groupReviewAdvice;
    }

    public String getGroupReviewResult() {
        return this.groupReviewResult;
    }

    public void setGroupReviewResult(String groupReviewResult) {
        this.groupReviewResult = groupReviewResult;
    }

    public String getQmAdvice() {
        return this.qmAdvice;
    }

    public void setQmAdvice(String qmAdvice) {
        this.qmAdvice = qmAdvice;
    }

    public String getCarryTestContent() {
        return this.carryTestContent;
    }

    public void setCarryTestContent(String carryTestContent) {
        this.carryTestContent = carryTestContent;
    }

    public String getCarryTestResult() {
        return this.carryTestResult;
    }

    public void setCarryTestResult(String carryTestResult) {
        this.carryTestResult = carryTestResult;
    }

    public String getReviewContent() {
        return this.reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewImprovedContent() {
        return this.reviewImprovedContent;
    }

    public void setReviewImprovedContent(String reviewImprovedContent) {
        this.reviewImprovedContent = reviewImprovedContent;
    }

    public String getReviewTechnicalAdvice() {
        return this.reviewTechnicalAdvice;
    }

    public void setReviewTechnicalAdvice(String reviewTechnicalAdvice) {
        this.reviewTechnicalAdvice = reviewTechnicalAdvice;
    }

    public String getReviewQmAdvice() {
        return this.reviewQmAdvice;
    }

    public void setReviewQmAdvice(String reviewQmAdvice) {
        this.reviewQmAdvice = reviewQmAdvice;
    }

    public String getReviewRemark() {
        return this.reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public String getConfirmStandardName() {
        return this.confirmStandardName;
    }

    public void setConfirmStandardName(String confirmStandardName) {
        this.confirmStandardName = confirmStandardName;
    }

    public String getConfirmStandardNo() {
        return this.confirmStandardNo;
    }

    public void setConfirmStandardNo(String confirmStandardNo) {
        this.confirmStandardNo = confirmStandardNo;
    }

    public String getConfirmContent() {
        return this.confirmContent;
    }

    public void setConfirmContent(String confirmContent) {
        this.confirmContent = confirmContent;
    }

    public String getConfirmResult() {
        return this.confirmResult;
    }

    public void setConfirmResult(String confirmResult) {
        this.confirmResult = confirmResult;
    }

    public String getConfirmQmAdvice() {
        return this.confirmQmAdvice;
    }

    public void setConfirmQmAdvice(String confirmQmAdvice) {
        this.confirmQmAdvice = confirmQmAdvice;
    }

    public String getConfirmLeaderAdvice() {
        return this.confirmLeaderAdvice;
    }

    public void setConfirmLeaderAdvice(String confirmLeaderAdvice) {
        this.confirmLeaderAdvice = confirmLeaderAdvice;
    }

    public String getConfirmRemark() {
        return this.confirmRemark;
    }

    public void setConfirmRemark(String confirmRemark) {
        this.confirmRemark = confirmRemark;
    }
}
