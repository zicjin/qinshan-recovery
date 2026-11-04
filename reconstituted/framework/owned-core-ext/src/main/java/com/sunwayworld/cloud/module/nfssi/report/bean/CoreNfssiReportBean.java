/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_NFSSI_REPORT")
public class CoreNfssiReportBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private String id;
    private String orderId;
    @Clob
    private String superviseReportRemark;
    @Clob
    private String riskReportRemark;
    private String inspectOrgContactMobile;
    private String inspectOrgContact;
    private String inspectOrgContactEmail;
    private String sealSampler;
    private String sealSamplerEmail;
    private String sealSamplerMobile;
    private String state;
    @Clob
    private String errorInfo;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSuperviseReportRemark() {
        return this.superviseReportRemark;
    }

    public void setSuperviseReportRemark(String superviseReportRemark) {
        this.superviseReportRemark = superviseReportRemark;
    }

    public String getRiskReportRemark() {
        return this.riskReportRemark;
    }

    public void setRiskReportRemark(String riskReportRemark) {
        this.riskReportRemark = riskReportRemark;
    }

    public String getInspectOrgContactMobile() {
        return this.inspectOrgContactMobile;
    }

    public void setInspectOrgContactMobile(String inspectOrgContactMobile) {
        this.inspectOrgContactMobile = inspectOrgContactMobile;
    }

    public String getInspectOrgContact() {
        return this.inspectOrgContact;
    }

    public void setInspectOrgContact(String inspectOrgContact) {
        this.inspectOrgContact = inspectOrgContact;
    }

    public String getInspectOrgContactEmail() {
        return this.inspectOrgContactEmail;
    }

    public void setInspectOrgContactEmail(String inspectOrgContactEmail) {
        this.inspectOrgContactEmail = inspectOrgContactEmail;
    }

    public String getSealSampler() {
        return this.sealSampler;
    }

    public void setSealSampler(String sealSampler) {
        this.sealSampler = sealSampler;
    }

    public String getSealSamplerEmail() {
        return this.sealSamplerEmail;
    }

    public void setSealSamplerEmail(String sealSamplerEmail) {
        this.sealSamplerEmail = sealSamplerEmail;
    }

    public String getSealSamplerMobile() {
        return this.sealSamplerMobile;
    }

    public void setSealSamplerMobile(String sealSamplerMobile) {
        this.sealSamplerMobile = sealSamplerMobile;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getErrorInfo() {
        return this.errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
