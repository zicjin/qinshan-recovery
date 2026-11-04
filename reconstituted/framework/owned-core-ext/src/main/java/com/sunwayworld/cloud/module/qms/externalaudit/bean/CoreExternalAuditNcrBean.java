/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EXTERNAL_AUDIT_NCR")
public class CoreExternalAuditNcrBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long auditId;
    private Long orderNo;
    private String ncrItemNo;
    private String ncrItemContent;
    private String discoveryOrgId;
    private String discoveryOrgName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discoveryDate;
    private String judgeResult;
    private String beAuditOrgId;
    private String beAuditOrgName;
    private LocalDateTime rectifyDeadline;
    private String auditResultDesc;
    private String ncrItemNos;
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

    public Long getAuditId() {
        return this.auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getNcrItemNo() {
        return this.ncrItemNo;
    }

    public void setNcrItemNo(String ncrItemNo) {
        this.ncrItemNo = ncrItemNo;
    }

    public String getNcrItemContent() {
        return this.ncrItemContent;
    }

    public void setNcrItemContent(String ncrItemContent) {
        this.ncrItemContent = ncrItemContent;
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

    public LocalDateTime getDiscoveryDate() {
        return this.discoveryDate;
    }

    public void setDiscoveryDate(LocalDateTime discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    public String getJudgeResult() {
        return this.judgeResult;
    }

    public void setJudgeResult(String judgeResult) {
        this.judgeResult = judgeResult;
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

    public LocalDateTime getRectifyDeadline() {
        return this.rectifyDeadline;
    }

    public void setRectifyDeadline(LocalDateTime rectifyDeadline) {
        this.rectifyDeadline = rectifyDeadline;
    }

    public String getAuditResultDesc() {
        return this.auditResultDesc;
    }

    public void setAuditResultDesc(String auditResultDesc) {
        this.auditResultDesc = auditResultDesc;
    }

    public String getNcrItemNos() {
        return this.ncrItemNos;
    }

    public void setNcrItemNos(String ncrItemNos) {
        this.ncrItemNos = ncrItemNos;
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

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
