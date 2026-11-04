/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreAuditLogColumnVerticalDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -3400178080175724409L;
    private String column;
    private String columnName;
    @Clob
    private String oldValue;
    @Clob
    private String newValue;

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
