/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context;

import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import java.io.Serializable;
import java.util.List;

public class TableContext
implements Serializable {
    private static final long serialVersionUID = 8374848185614999636L;
    private String tableName;
    private List<ColumnContext> columnContextList;
    private ColumnContext idColumnContext;
    private boolean orderColumnFlag;

    public static TableContext of(String table) {
        return DatabaseManager.getTableContext(table);
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnContext> getColumnContextList() {
        return this.columnContextList;
    }

    public void setColumnContextList(List<ColumnContext> columnContextList) {
        this.columnContextList = columnContextList;
    }

    public ColumnContext getIdColumnContext() {
        return this.idColumnContext;
    }

    public void setIdColumnContext(ColumnContext idColumnContext) {
        this.idColumnContext = idColumnContext;
    }

    public void setOrderColumnFlag(boolean orderColumnFlag) {
        this.orderColumnFlag = orderColumnFlag;
    }

    public boolean isNumberId() {
        return Number.class.isAssignableFrom(this.idColumnContext.getType());
    }

    public boolean hasOrderColumn() {
        return this.orderColumnFlag;
    }
}
