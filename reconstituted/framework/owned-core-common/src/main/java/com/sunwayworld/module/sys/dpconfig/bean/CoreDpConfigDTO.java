/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreDpConfigDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 3488956240896158939L;
    private String id;
    private String dpConfigName;
    private String tableName;
    private String uniqueFields;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDpConfigName() {
        return this.dpConfigName;
    }

    public void setDpConfigName(String dpConfigName) {
        this.dpConfigName = dpConfigName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getUniqueFields() {
        return this.uniqueFields;
    }

    public void setUniqueFields(String uniqueFields) {
        this.uniqueFields = uniqueFields;
    }
}
