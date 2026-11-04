/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.bean;

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

@Table(value="T_CORE_NCR")
public class CoreNcrBean
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
    private String ncrNo;
    private String ncrContent;
    private String ncrStandard;
    private String ncrGrade;
    private String ncrRemark;
    private String beAuditOrgId;
    private String beAuditOrgName;
    private String beAuditUserId;
    private String beAuditUserName;
    private String ncrSource;
    private String ncrSourceNo;
    private String discoveryUserId;
    private String discoveryUserName;
    private LocalDate discoveryDate;
    private String discoveryOrgId;
    private String discoveryOrgName;
    private LocalDate deadline;
    private LocalDate checkStartDate;
    private LocalDate checkEndDate;
    private String checkType;
    private String measuresReq;
    private String ncrConfirm;
    private String ncrReason;
    private String rectifyPlan;
    private String rectifyCarry;
    private String verifyDesc;
    private String verifyResult;
    private String nextVerifyResult;
    private LocalDate nextVerifyDeadline;
    private String verifyUserId;
    private String verifyUserName;
    private LocalDate verifyDate;
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

    public String getNcrNo() {
        return this.ncrNo;
    }

    public void setNcrNo(String ncrNo) {
        this.ncrNo = ncrNo;
    }

    public String getNcrContent() {
        return this.ncrContent;
    }

    public void setNcrContent(String ncrContent) {
        this.ncrContent = ncrContent;
    }

    public String getNcrStandard() {
        return this.ncrStandard;
    }

    public void setNcrStandard(String ncrStandard) {
        this.ncrStandard = ncrStandard;
    }

    public String getNcrGrade() {
        return this.ncrGrade;
    }

    public void setNcrGrade(String ncrGrade) {
        this.ncrGrade = ncrGrade;
    }

    public String getNcrRemark() {
        return this.ncrRemark;
    }

    public void setNcrRemark(String ncrRemark) {
        this.ncrRemark = ncrRemark;
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

    public String getBeAuditUserId() {
        return this.beAuditUserId;
    }

    public void setBeAuditUserId(String beAuditUserId) {
        this.beAuditUserId = beAuditUserId;
    }

    public String getBeAuditUserName() {
        return this.beAuditUserName;
    }

    public void setBeAuditUserName(String beAuditUserName) {
        this.beAuditUserName = beAuditUserName;
    }

    public String getNcrSource() {
        return this.ncrSource;
    }

    public void setNcrSource(String ncrSource) {
        this.ncrSource = ncrSource;
    }

    public String getNcrSourceNo() {
        return this.ncrSourceNo;
    }

    public void setNcrSourceNo(String ncrSourceNo) {
        this.ncrSourceNo = ncrSourceNo;
    }

    public String getDiscoveryUserId() {
        return this.discoveryUserId;
    }

    public void setDiscoveryUserId(String discoveryUserId) {
        this.discoveryUserId = discoveryUserId;
    }

    public String getDiscoveryUserName() {
        return this.discoveryUserName;
    }

    public void setDiscoveryUserName(String discoveryUserName) {
        this.discoveryUserName = discoveryUserName;
    }

    public LocalDate getDiscoveryDate() {
        return this.discoveryDate;
    }

    public void setDiscoveryDate(LocalDate discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    public String getDiscoveryOrgId() {
        return this.discoveryOrgId;
    }

    public void setDiscoveryOrgId(String discoveryOrgId) {
        this.discoveryOrgId = discoveryOrgId;
    }

    public String getDiscoveryOrgName() {
        return this.discoveryOrgName;
    }

    public void setDiscoveryOrgName(String discoveryOrgName) {
        this.discoveryOrgName = discoveryOrgName;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getCheckStartDate() {
        return this.checkStartDate;
    }

    public void setCheckStartDate(LocalDate checkStartDate) {
        this.checkStartDate = checkStartDate;
    }

    public LocalDate getCheckEndDate() {
        return this.checkEndDate;
    }

    public void setCheckEndDate(LocalDate checkEndDate) {
        this.checkEndDate = checkEndDate;
    }

    public String getCheckType() {
        return this.checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getMeasuresReq() {
        return this.measuresReq;
    }

    public void setMeasuresReq(String measuresReq) {
        this.measuresReq = measuresReq;
    }

    public String getNcrConfirm() {
        return this.ncrConfirm;
    }

    public void setNcrConfirm(String ncrConfirm) {
        this.ncrConfirm = ncrConfirm;
    }

    public String getNcrReason() {
        return this.ncrReason;
    }

    public void setNcrReason(String ncrReason) {
        this.ncrReason = ncrReason;
    }

    public String getRectifyPlan() {
        return this.rectifyPlan;
    }

    public void setRectifyPlan(String rectifyPlan) {
        this.rectifyPlan = rectifyPlan;
    }

    public String getRectifyCarry() {
        return this.rectifyCarry;
    }

    public void setRectifyCarry(String rectifyCarry) {
        this.rectifyCarry = rectifyCarry;
    }

    public String getVerifyDesc() {
        return this.verifyDesc;
    }

    public void setVerifyDesc(String verifyDesc) {
        this.verifyDesc = verifyDesc;
    }

    public String getVerifyResult() {
        return this.verifyResult;
    }

    public void setVerifyResult(String verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getNextVerifyResult() {
        return this.nextVerifyResult;
    }

    public void setNextVerifyResult(String nextVerifyResult) {
        this.nextVerifyResult = nextVerifyResult;
    }

    public LocalDate getNextVerifyDeadline() {
        return this.nextVerifyDeadline;
    }

    public void setNextVerifyDeadline(LocalDate nextVerifyDeadline) {
        this.nextVerifyDeadline = nextVerifyDeadline;
    }

    public String getVerifyUserId() {
        return this.verifyUserId;
    }

    public void setVerifyUserId(String verifyUserId) {
        this.verifyUserId = verifyUserId;
    }

    public String getVerifyUserName() {
        return this.verifyUserName;
    }

    public void setVerifyUserName(String verifyUserName) {
        this.verifyUserName = verifyUserName;
    }

    public LocalDate getVerifyDate() {
        return this.verifyDate;
    }

    public void setVerifyDate(LocalDate verifyDate) {
        this.verifyDate = verifyDate;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
