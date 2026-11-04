/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpTableIndexDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = -1803430602559287678L;
    private String tableName;
    private String indexName;
    private String indexType;
    private String indexField;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexType() {
        return this.indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getIndexField() {
        return this.indexField;
    }

    public void setIndexField(String indexField) {
        this.indexField = indexField;
    }
}
