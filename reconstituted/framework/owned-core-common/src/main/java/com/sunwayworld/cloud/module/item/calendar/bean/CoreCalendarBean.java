/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.calendar.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_CALENDAR")
public class CoreCalendarBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = -2247939669742730732L;
    @Id
    private String id;
    private String workday;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getWorkday() {
        return this.workday;
    }

    public void setWorkday(String workday) {
        this.workday = workday;
    }
}
