/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreOfficeWriteBackDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -7178914615498994869L;
    private Object bizId;
    private String tableName;
    private String columnName;
    private Object val;

    public CoreOfficeWriteBackDTO(Object bizId, String tableName, String columnName, Object val) {
        this.bizId = bizId;
        this.tableName = tableName;
        this.columnName = columnName;
        this.val = val;
    }

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
