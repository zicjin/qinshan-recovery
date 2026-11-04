/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.bean;

import com.sunwayworld.cloud.module.nfssi.report.bean.CoreNfssiReportItemDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreNfssiReportDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -3567544549225983532L;
    private String reportNo;
    private String sampleNo;
    private String jdBz;
    private String fxBz;
    private String contactTel;
    private String contact;
    private String contactEmail;
    private String fyPerson;
    private String fyEmail;
    private String fyTel;
    private List<CoreNfssiReportItemDTO> testItemInfo;

    public String getReportNo() {
        return this.reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getSampleNo() {
        return this.sampleNo;
    }

    public void setSampleNo(String sampleNo) {
        this.sampleNo = sampleNo;
    }

    public String getJdBz() {
        return this.jdBz;
    }

    public void setJdBz(String jdBz) {
        this.jdBz = jdBz;
    }

    public String getFxBz() {
        return this.fxBz;
    }

    public void setFxBz(String fxBz) {
        this.fxBz = fxBz;
    }

    public String getContactTel() {
        return this.contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getFyPerson() {
        return this.fyPerson;
    }

    public void setFyPerson(String fyPerson) {
        this.fyPerson = fyPerson;
    }

    public String getFyEmail() {
        return this.fyEmail;
    }

    public void setFyEmail(String fyEmail) {
        this.fyEmail = fyEmail;
    }

    public String getFyTel() {
        return this.fyTel;
    }

    public void setFyTel(String fyTel) {
        this.fyTel = fyTel;
    }

    public List<CoreNfssiReportItemDTO> getTestItemInfo() {
        return this.testItemInfo;
    }

    public void setTestItemInfo(List<CoreNfssiReportItemDTO> testItemInfo) {
        this.testItemInfo = testItemInfo;
    }
}
