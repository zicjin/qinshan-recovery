/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpTableViewDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = 8936490317045748554L;
    private String tableOrViewName;
    private String dbStructureType;

    public String getTableOrViewName() {
        return this.tableOrViewName;
    }

    public void setTableOrViewName(String tableOrViewName) {
        this.tableOrViewName = tableOrViewName;
    }

    public String getDbStructureType() {
        return this.dbStructureType;
    }

    public void setDbStructureType(String dbStructureType) {
        this.dbStructureType = dbStructureType;
    }
}
