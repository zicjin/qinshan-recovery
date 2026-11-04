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

@Table(value="T_CORE_QUALITY_CONTROL_PLAN")
public class CoreQualityControlPlanBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long annualPlanId;
    private String itemName;
    private String qcType;
    private String standard;
    private String acceptCriteria;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate carryDate;
    private String sponsorOrgId;
    private String sponsorOrgName;
    private String carryOrgId;
    private String carryOrgName;
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

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQcType() {
        return this.qcType;
    }

    public void setQcType(String qcType) {
        this.qcType = qcType;
    }

    public String getStandard() {
        return this.standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getAcceptCriteria() {
        return this.acceptCriteria;
    }

    public void setAcceptCriteria(String acceptCriteria) {
        this.acceptCriteria = acceptCriteria;
    }

    public LocalDate getCarryDate() {
        return this.carryDate;
    }

    public void setCarryDate(LocalDate carryDate) {
        this.carryDate = carryDate;
    }

    public String getSponsorOrgId() {
        return this.sponsorOrgId;
    }

    public void setSponsorOrgId(String sponsorOrgId) {
        this.sponsorOrgId = sponsorOrgId;
    }

    public String getSponsorOrgName() {
        return this.sponsorOrgName;
    }

    public void setSponsorOrgName(String sponsorOrgName) {
        this.sponsorOrgName = sponsorOrgName;
    }

    public String getCarryOrgId() {
        return this.carryOrgId;
    }

    public void setCarryOrgId(String carryOrgId) {
        this.carryOrgId = carryOrgId;
    }

    public String getCarryOrgName() {
        return this.carryOrgName;
    }

    public void setCarryOrgName(String carryOrgName) {
        this.carryOrgName = carryOrgName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
