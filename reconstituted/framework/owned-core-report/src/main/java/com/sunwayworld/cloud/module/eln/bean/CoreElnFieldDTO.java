/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreElnFieldDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 3000672166269758471L;
    private Object val;
    private String tableName;
    private String columnName;
    private Object bizId;

    public Object getVal() {
        return this.val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getBizId() {
        return this.bizId;
    }

    public void setBizId(Object bizId) {
        this.bizId = bizId;
    }
}
