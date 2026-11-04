/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_RISK_IDENTIFICA_FACTOR")
public class CoreRiskIdentificaFactorBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = 720987336892380610L;
    @Id
    private Long id;
    private Long riskRegistId;
    private Long riskFactorId;
    private String riskDesc;
    private String riskResolution;
    private Long riskRate;
    private Long severity;
    private Long incidence;
    private String refWorkFlowType;
    private String refWorkFlowId;
    private String beAuditOrgId;
    private String beAuditOrgName;
    private String carryMonitorRecord;
    private String createdByName;
    private String createdById;
    private String carryVerify;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getRiskRegistId() {
        return this.riskRegistId;
    }

    public void setRiskRegistId(Long riskRegistId) {
        this.riskRegistId = riskRegistId;
    }

    public Long getRiskFactorId() {
        return this.riskFactorId;
    }

    public void setRiskFactorId(Long riskFactorId) {
        this.riskFactorId = riskFactorId;
    }

    public String getRiskDesc() {
        return this.riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public String getRiskResolution() {
        return this.riskResolution;
    }

    public void setRiskResolution(String riskResolution) {
        this.riskResolution = riskResolution;
    }

    public Long getRiskRate() {
        return this.riskRate;
    }

    public void setRiskRate(Long riskRate) {
        this.riskRate = riskRate;
    }

    public Long getSeverity() {
        return this.severity;
    }

    public void setSeverity(Long severity) {
        this.severity = severity;
    }

    public Long getIncidence() {
        return this.incidence;
    }

    public void setIncidence(Long incidence) {
        this.incidence = incidence;
    }

    public String getRefWorkFlowType() {
        return this.refWorkFlowType;
    }

    public void setRefWorkFlowType(String refWorkFlowType) {
        this.refWorkFlowType = refWorkFlowType;
    }

    public String getRefWorkFlowId() {
        return this.refWorkFlowId;
    }

    public void setRefWorkFlowId(String refWorkFlowId) {
        this.refWorkFlowId = refWorkFlowId;
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

    public String getCarryMonitorRecord() {
        return this.carryMonitorRecord;
    }

    public void setCarryMonitorRecord(String carryMonitorRecord) {
        this.carryMonitorRecord = carryMonitorRecord;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCarryVerify() {
        return this.carryVerify;
    }

    public void setCarryVerify(String carryVerify) {
        this.carryVerify = carryVerify;
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
}
