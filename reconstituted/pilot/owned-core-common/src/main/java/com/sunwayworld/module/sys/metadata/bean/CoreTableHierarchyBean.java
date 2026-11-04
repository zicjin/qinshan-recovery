/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_TABLE_HIERARCHY")
public class CoreTableHierarchyBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -6157635559493863765L;
    @Id
    private Long id;
    private String tableName;
    private String masterTableName;
    private String referColumn;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMasterTableName() {
        return this.masterTableName;
    }

    public void setMasterTableName(String masterTableName) {
        this.masterTableName = masterTableName;
    }

    public String getReferColumn() {
        return this.referColumn;
    }

    public void setReferColumn(String referColumn) {
        this.referColumn = referColumn;
    }
}
