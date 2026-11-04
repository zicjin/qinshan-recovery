/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context.instance;

import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.ColumnContextInstance;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TableContextInstance
implements Serializable {
    private static final long serialVersionUID = -5930647240699785502L;
    private Object item;
    private TableContext context;
    private List<ColumnContextInstance> columnInstanceList;
    private ColumnContextInstance idColumnInstance;

    private TableContextInstance() {
    }

    public static TableContextInstance of(TableContext context, Object item) {
        TableContextInstance instance = new TableContextInstance();
        instance.item = item;
        instance.context = context;
        instance.columnInstanceList = new ArrayList<ColumnContextInstance>();
        context.getColumnContextList().forEach(c -> {
            ColumnContextInstance columnInstance = ColumnContextInstance.of(context, c.getColumnName(), item);
            instance.columnInstanceList.add(columnInstance);
            if ("id".equalsIgnoreCase(c.getColumnName())) {
                instance.idColumnInstance = columnInstance;
            }
        });
        return instance;
    }

    public boolean isUpdatable() {
        return this.columnInstanceList.stream().anyMatch(c -> c.isActive() && !"ID".equalsIgnoreCase(c.getColumnContext().getColumnName()));
    }

    public Object getItem() {
        return this.item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public TableContext getContext() {
        return this.context;
    }

    public void setContext(TableContext context) {
        this.context = context;
    }

    public List<ColumnContextInstance> getColumnInstanceList() {
        return this.columnInstanceList;
    }

    public void setColumnInstanceList(List<ColumnContextInstance> columnInstanceList) {
        this.columnInstanceList = columnInstanceList;
    }

    public ColumnContextInstance getIdColumnInstance() {
        return this.idColumnInstance;
    }

    public void setIdColumnInstance(ColumnContextInstance idColumnInstance) {
        this.idColumnInstance = idColumnInstance;
    }
}
