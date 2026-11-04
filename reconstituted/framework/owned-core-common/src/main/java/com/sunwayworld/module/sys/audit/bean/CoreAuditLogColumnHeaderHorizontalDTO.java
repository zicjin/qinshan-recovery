/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreAuditLogColumnHeaderHorizontalDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -7340222355665146637L;
    private String column;
    private String columnName;

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
}
