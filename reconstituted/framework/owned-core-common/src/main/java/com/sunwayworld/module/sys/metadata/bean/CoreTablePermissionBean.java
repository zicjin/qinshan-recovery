/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_TABLE_PERMISSION")
public class CoreTablePermissionBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -7387388318431900543L;
    @Id
    private Long id;
    private String tableName;
    private String category;
    private String serviceClass;
    private String checkAvailableMethod;
    private String checkUpdatableMethod;
    private String checkAvailable;
    private String checkUpdatable;

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

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getServiceClass() {
        return this.serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getCheckAvailableMethod() {
        return this.checkAvailableMethod;
    }

    public void setCheckAvailableMethod(String checkAvailableMethod) {
        this.checkAvailableMethod = checkAvailableMethod;
    }

    public String getCheckUpdatableMethod() {
        return this.checkUpdatableMethod;
    }

    public void setCheckUpdatableMethod(String checkUpdatableMethod) {
        this.checkUpdatableMethod = checkUpdatableMethod;
    }

    public String getCheckAvailable() {
        return this.checkAvailable;
    }

    public void setCheckAvailable(String checkAvailable) {
        this.checkAvailable = checkAvailable;
    }

    public String getCheckUpdatable() {
        return this.checkUpdatable;
    }

    public void setCheckUpdatable(String checkUpdatable) {
        this.checkUpdatable = checkUpdatable;
    }
}
