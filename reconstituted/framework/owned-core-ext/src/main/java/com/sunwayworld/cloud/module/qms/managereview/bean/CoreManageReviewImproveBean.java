/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_MANAGE_REVIEW_IMPROVE")
public class CoreManageReviewImproveBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long manageReviewId;
    private String problems;
    private String improvePlan;
    private String improveOrgId;
    private String improveOrgName;
    private String deadline;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getManageReviewId() {
        return this.manageReviewId;
    }

    public void setManageReviewId(Long manageReviewId) {
        this.manageReviewId = manageReviewId;
    }

    public String getProblems() {
        return this.problems;
    }

    public void setProblems(String problems) {
        this.problems = problems;
    }

    public String getImprovePlan() {
        return this.improvePlan;
    }

    public void setImprovePlan(String improvePlan) {
        this.improvePlan = improvePlan;
    }

    public String getImproveOrgId() {
        return this.improveOrgId;
    }

    public void setImproveOrgId(String improveOrgId) {
        this.improveOrgId = improveOrgId;
    }

    public String getImproveOrgName() {
        return this.improveOrgName;
    }

    public void setImproveOrgName(String improveOrgName) {
        this.improveOrgName = improveOrgName;
    }

    public String getDeadline() {
        return this.deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
