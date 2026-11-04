/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_MANAGE_REVIEW")
public class CoreManageReviewBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long annualPlanId;
    private String annualPlanNo;
    private String reviewNo;
    private String reviewPurpose;
    private String reviewContent;
    private String reviewReq;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate reviewDate;
    private String reviewUserId;
    private String reviewUserName;
    private String reviewStandard;
    private String reviewPlace;
    private String reviewMethod;
    private String chairUserId;
    private String chairUserName;
    private String reviewDescription;
    private String reviewResult;
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

    public Long getAnnualPlanId() {
        return this.annualPlanId;
    }

    public void setAnnualPlanId(Long annualPlanId) {
        this.annualPlanId = annualPlanId;
    }

    public String getAnnualPlanNo() {
        return this.annualPlanNo;
    }

    public void setAnnualPlanNo(String annualPlanNo) {
        this.annualPlanNo = annualPlanNo;
    }

    public String getReviewNo() {
        return this.reviewNo;
    }

    public void setReviewNo(String reviewNo) {
        this.reviewNo = reviewNo;
    }

    public String getReviewPurpose() {
        return this.reviewPurpose;
    }

    public void setReviewPurpose(String reviewPurpose) {
        this.reviewPurpose = reviewPurpose;
    }

    public String getReviewContent() {
        return this.reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewReq() {
        return this.reviewReq;
    }

    public void setReviewReq(String reviewReq) {
        this.reviewReq = reviewReq;
    }

    public LocalDate getReviewDate() {
        return this.reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
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

    public String getReviewStandard() {
        return this.reviewStandard;
    }

    public void setReviewStandard(String reviewStandard) {
        this.reviewStandard = reviewStandard;
    }

    public String getReviewPlace() {
        return this.reviewPlace;
    }

    public void setReviewPlace(String reviewPlace) {
        this.reviewPlace = reviewPlace;
    }

    public String getReviewMethod() {
        return this.reviewMethod;
    }

    public void setReviewMethod(String reviewMethod) {
        this.reviewMethod = reviewMethod;
    }

    public String getChairUserId() {
        return this.chairUserId;
    }

    public void setChairUserId(String chairUserId) {
        this.chairUserId = chairUserId;
    }

    public String getChairUserName() {
        return this.chairUserName;
    }

    public void setChairUserName(String chairUserName) {
        this.chairUserName = chairUserName;
    }

    public String getReviewDescription() {
        return this.reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }

    public String getReviewResult() {
        return this.reviewResult;
    }

    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
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
