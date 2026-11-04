/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.cron.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

public class CoreCronDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = -3568077247168176302L;
    private String cron;
    private String description;

    public String getCron() {
        return this.cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
