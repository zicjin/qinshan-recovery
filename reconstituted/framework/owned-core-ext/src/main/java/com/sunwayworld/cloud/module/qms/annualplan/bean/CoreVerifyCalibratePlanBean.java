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
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_VERIFY_CALIBRATE_PLAN")
public class CoreVerifyCalibratePlanBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long annualPlanId;
    private String equId;
    private String equCode;
    private String equName;
    private String modelNo;
    private String sn;
    private String mfg;
    private String verifyPeriod;
    private String verifyCorp;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate lastVerifyDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate nextVerifyDate;
    private String equPlace;
    private String manageOrgId;
    private String manageOrgName;
    private String manageUserId;
    private String manageUserName;
    private String inspectUserId;
    private String inspectUserName;
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

    public String getEquId() {
        return this.equId;
    }

    public void setEquId(String equId) {
        this.equId = equId;
    }

    public String getEquCode() {
        return this.equCode;
    }

    public void setEquCode(String equCode) {
        this.equCode = equCode;
    }

    public String getEquName() {
        return this.equName;
    }

    public void setEquName(String equName) {
        this.equName = equName;
    }

    public String getModelNo() {
        return this.modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMfg() {
        return this.mfg;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public String getVerifyPeriod() {
        return this.verifyPeriod;
    }

    public void setVerifyPeriod(String verifyPeriod) {
        this.verifyPeriod = verifyPeriod;
    }

    public String getVerifyCorp() {
        return this.verifyCorp;
    }

    public void setVerifyCorp(String verifyCorp) {
        this.verifyCorp = verifyCorp;
    }

    public LocalDate getLastVerifyDate() {
        return this.lastVerifyDate;
    }

    public void setLastVerifyDate(LocalDate lastVerifyDate) {
        this.lastVerifyDate = lastVerifyDate;
    }

    public LocalDate getNextVerifyDate() {
        return this.nextVerifyDate;
    }

    public void setNextVerifyDate(LocalDate nextVerifyDate) {
        this.nextVerifyDate = nextVerifyDate;
    }

    public String getEquPlace() {
        return this.equPlace;
    }

    public void setEquPlace(String equPlace) {
        this.equPlace = equPlace;
    }

    public String getManageOrgId() {
        return this.manageOrgId;
    }

    public void setManageOrgId(String manageOrgId) {
        this.manageOrgId = manageOrgId;
    }

    public String getManageOrgName() {
        return this.manageOrgName;
    }

    public void setManageOrgName(String manageOrgName) {
        this.manageOrgName = manageOrgName;
    }

    public String getManageUserId() {
        return this.manageUserId;
    }

    public void setManageUserId(String manageUserId) {
        this.manageUserId = manageUserId;
    }

    public String getManageUserName() {
        return this.manageUserName;
    }

    public void setManageUserName(String manageUserName) {
        this.manageUserName = manageUserName;
    }

    public String getInspectUserId() {
        return this.inspectUserId;
    }

    public void setInspectUserId(String inspectUserId) {
        this.inspectUserId = inspectUserId;
    }

    public String getInspectUserName() {
        return this.inspectUserName;
    }

    public void setInspectUserName(String inspectUserName) {
        this.inspectUserName = inspectUserName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
