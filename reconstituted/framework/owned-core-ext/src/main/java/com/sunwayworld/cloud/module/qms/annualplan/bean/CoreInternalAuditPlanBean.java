/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_INTERNAL_AUDIT_PLAN")
public class CoreInternalAuditPlanBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long annualPlanId;
    private String auditPurpose;
    private String auditContent;
    private String auditRule;
    private String auditMethod;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;
    private String auditUserAndWork;
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

    public String getAuditPurpose() {
        return this.auditPurpose;
    }

    public void setAuditPurpose(String auditPurpose) {
        this.auditPurpose = auditPurpose;
    }

    public String getAuditContent() {
        return this.auditContent;
    }

    public void setAuditContent(String auditContent) {
        this.auditContent = auditContent;
    }

    public String getAuditRule() {
        return this.auditRule;
    }

    public void setAuditRule(String auditRule) {
        this.auditRule = auditRule;
    }

    public String getAuditMethod() {
        return this.auditMethod;
    }

    public void setAuditMethod(String auditMethod) {
        this.auditMethod = auditMethod;
    }

    public LocalDateTime getAuditDate() {
        return this.auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditUserAndWork() {
        return this.auditUserAndWork;
    }

    public void setAuditUserAndWork(String auditUserAndWork) {
        this.auditUserAndWork = auditUserAndWork;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
