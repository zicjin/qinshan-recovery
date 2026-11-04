/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.calendar.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreCalendarRepresentation
implements Serializable {
    @Transient
    private static final long serialVersionUID = -5141900785383946133L;
    private String ymd;
    private String isWorkDay;

    public String getYmd() {
        return this.ymd;
    }

    public void setYmd(String ymd) {
        this.ymd = ymd;
    }

    public String getIsWorkDay() {
        return this.isWorkDay;
    }

    public void setIsWorkDay(String isWorkDay) {
        this.isWorkDay = isWorkDay;
    }
}
