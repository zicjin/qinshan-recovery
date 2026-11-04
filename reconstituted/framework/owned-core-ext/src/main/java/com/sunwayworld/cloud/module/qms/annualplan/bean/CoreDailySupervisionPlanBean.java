/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_DAILY_SUPERVISION_PLAN")
public class CoreDailySupervisionPlanBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long annualPlanId;
    private String supervisePurpose;
    private String superviseContent;
    private String superviseReq;
    private String superviseType;
    private String supervisorId;
    private String supervisorName;
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

    public String getSupervisePurpose() {
        return this.supervisePurpose;
    }

    public void setSupervisePurpose(String supervisePurpose) {
        this.supervisePurpose = supervisePurpose;
    }

    public String getSuperviseContent() {
        return this.superviseContent;
    }

    public void setSuperviseContent(String superviseContent) {
        this.superviseContent = superviseContent;
    }

    public String getSuperviseReq() {
        return this.superviseReq;
    }

    public void setSuperviseReq(String superviseReq) {
        this.superviseReq = superviseReq;
    }

    public String getSuperviseType() {
        return this.superviseType;
    }

    public void setSuperviseType(String superviseType) {
        this.superviseType = superviseType;
    }

    public String getSupervisorId() {
        return this.supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getSupervisorName() {
        return this.supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
