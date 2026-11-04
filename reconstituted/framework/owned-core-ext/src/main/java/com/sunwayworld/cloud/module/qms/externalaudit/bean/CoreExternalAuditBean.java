/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EXTERNAL_AUDIT")
public class CoreExternalAuditBean
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
    private String auditNo;
    private String auditPurpose;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;
    private String auditAddress;
    private String auditUserName;
    private String auditOrgName;
    private String orgManager;
    private String orgAddress;
    private String orgPostcode;
    private String orgPhone;
    private String orgFax;
    private String rectifyResultDesc;
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

    public String getAuditNo() {
        return this.auditNo;
    }

    public void setAuditNo(String auditNo) {
        this.auditNo = auditNo;
    }

    public String getAuditPurpose() {
        return this.auditPurpose;
    }

    public void setAuditPurpose(String auditPurpose) {
        this.auditPurpose = auditPurpose;
    }

    public LocalDateTime getAuditDate() {
        return this.auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditAddress() {
        return this.auditAddress;
    }

    public void setAuditAddress(String auditAddress) {
        this.auditAddress = auditAddress;
    }

    public String getAuditUserName() {
        return this.auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public String getAuditOrgName() {
        return this.auditOrgName;
    }

    public void setAuditOrgName(String auditOrgName) {
        this.auditOrgName = auditOrgName;
    }

    public String getOrgManager() {
        return this.orgManager;
    }

    public void setOrgManager(String orgManager) {
        this.orgManager = orgManager;
    }

    public String getOrgAddress() {
        return this.orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getOrgPostcode() {
        return this.orgPostcode;
    }

    public void setOrgPostcode(String orgPostcode) {
        this.orgPostcode = orgPostcode;
    }

    public String getOrgPhone() {
        return this.orgPhone;
    }

    public void setOrgPhone(String orgPhone) {
        this.orgPhone = orgPhone;
    }

    public String getOrgFax() {
        return this.orgFax;
    }

    public void setOrgFax(String orgFax) {
        this.orgFax = orgFax;
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
}
