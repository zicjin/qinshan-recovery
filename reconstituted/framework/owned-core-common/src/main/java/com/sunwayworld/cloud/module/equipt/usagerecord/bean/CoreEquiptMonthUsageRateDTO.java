/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreEquiptMonthUsageRateDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = 3501256693867350690L;
    private String equiptName;
    private String equiptCategory;
    private String yearMonth;
    private Long usageTime;
    private Double planUseTime;
    private String rate;

    public String getEquiptName() {
        return this.equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getEquiptCategory() {
        return this.equiptCategory;
    }

    public void setEquiptCategory(String equiptCategory) {
        this.equiptCategory = equiptCategory;
    }

    public String getYearMonth() {
        return this.yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public Long getUsageTime() {
        return this.usageTime;
    }

    public void setUsageTime(Long usageTime) {
        this.usageTime = usageTime;
    }

    public Double getPlanUseTime() {
        return this.planUseTime;
    }

    public void setPlanUseTime(Double planUseTime) {
        this.planUseTime = planUseTime;
    }

    public String getRate() {
        return this.rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
