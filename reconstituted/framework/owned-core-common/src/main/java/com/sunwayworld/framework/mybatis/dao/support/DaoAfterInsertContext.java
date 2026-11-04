/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.dao.support;

import com.sunwayworld.framework.support.domain.Persistable;
import java.util.List;

public class DaoAfterInsertContext {
    private final List<? extends Persistable<?>> items;
    private final String tableName;
    private final Class<? extends Persistable<?>> itemClass;

    public DaoAfterInsertContext(List<? extends Persistable<?>> items, String tableName, Class<? extends Persistable<?>> itemClass) {
        this.items = items;
        this.tableName = tableName;
        this.itemClass = itemClass;
    }

    public List<? extends Persistable<?>> getItems() {
        return this.items;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Class<? extends Persistable<?>> getItemClass() {
        return this.itemClass;
    }
}
