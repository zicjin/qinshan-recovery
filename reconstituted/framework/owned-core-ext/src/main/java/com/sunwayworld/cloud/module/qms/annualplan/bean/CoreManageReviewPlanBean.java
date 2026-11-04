/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_MANAGE_REVIEW_PLAN")
public class CoreManageReviewPlanBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long annualPlanId;
    private String reviewPurpose;
    private String reviewContent;
    private String reviewReq;
    private String reviewerId;
    private String reviewer;
    private String reviewSchedule;
    private String reviewReport;
    private String reviewFrequency;
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

    public String getReviewer() {
        return this.reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReviewSchedule() {
        return this.reviewSchedule;
    }

    public void setReviewSchedule(String reviewSchedule) {
        this.reviewSchedule = reviewSchedule;
    }

    public String getReviewReport() {
        return this.reviewReport;
    }

    public void setReviewReport(String reviewReport) {
        this.reviewReport = reviewReport;
    }

    public String getReviewFrequency() {
        return this.reviewFrequency;
    }

    public void setReviewFrequency(String reviewFrequency) {
        this.reviewFrequency = reviewFrequency;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReviewerId() {
        return this.reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }
}
