/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.io.Serializable;

public class CoreCustomColumnDTO
extends AbstractInsertable<String>
implements Serializable {
    private static final long serialVersionUID = -317241226361850976L;
    private String columnName;
    private String aliasName;

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {
    }
}
