/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context;

import com.sunwayworld.framework.database.context.EntityColumnContext;
import java.io.Serializable;
import java.util.List;

public class EntityContext
implements Serializable {
    private static final long serialVersionUID = 7530861832329381453L;
    private String tableName;
    private EntityColumnContext idContext;
    private EntityColumnContext versionContext;
    private List<EntityColumnContext> columnContextList;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public EntityColumnContext getIdContext() {
        return this.idContext;
    }

    public void setIdContext(EntityColumnContext idContext) {
        this.idContext = idContext;
    }

    public EntityColumnContext getVersionContext() {
        return this.versionContext;
    }

    public void setVersionContext(EntityColumnContext versionContext) {
        this.versionContext = versionContext;
    }

    public List<EntityColumnContext> getColumnContextList() {
        return this.columnContextList;
    }

    public void setColumnContextList(List<EntityColumnContext> columnContextList) {
        this.columnContextList = columnContextList;
    }

    public boolean isNumberId() {
        return Number.class.isAssignableFrom(this.idContext.getType());
    }
}
