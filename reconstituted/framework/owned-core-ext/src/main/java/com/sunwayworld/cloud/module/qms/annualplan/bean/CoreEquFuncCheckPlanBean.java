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

@Table(value="T_CORE_EQU_FUNC_CHECK_PLAN")
public class CoreEquFuncCheckPlanBean
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
    private String checkPeriod;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate lastCheckDate;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate nextCheckDate;
    private String checkUserId;
    private String checkUserName;
    private String checkOrgId;
    private String checkOrgName;
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

    public String getCheckPeriod() {
        return this.checkPeriod;
    }

    public void setCheckPeriod(String checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public LocalDate getLastCheckDate() {
        return this.lastCheckDate;
    }

    public void setLastCheckDate(LocalDate lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }

    public LocalDate getNextCheckDate() {
        return this.nextCheckDate;
    }

    public void setNextCheckDate(LocalDate nextCheckDate) {
        this.nextCheckDate = nextCheckDate;
    }

    public String getCheckUserId() {
        return this.checkUserId;
    }

    public void setCheckUserId(String checkUserId) {
        this.checkUserId = checkUserId;
    }

    public String getCheckUserName() {
        return this.checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public String getCheckOrgId() {
        return this.checkOrgId;
    }

    public void setCheckOrgId(String checkOrgId) {
        this.checkOrgId = checkOrgId;
    }

    public String getCheckOrgName() {
        return this.checkOrgName;
    }

    public void setCheckOrgName(String checkOrgName) {
        this.checkOrgName = checkOrgName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
