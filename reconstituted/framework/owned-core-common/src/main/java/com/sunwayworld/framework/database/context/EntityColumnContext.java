/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.support.domain.BaseData;
import java.io.Serializable;

public class EntityColumnContext
implements Serializable {
    private static final long serialVersionUID = 2514050087058095578L;
    private Class<?> entityClass;
    private String columnName;
    private Class<?> type;
    private boolean clob = false;
    private boolean blob = false;
    private boolean timestamp = false;

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public BeanPropertyDescriptor getPropertyDescriptor() {
        BeanPropertyDescriptor propertyDescriptor = BeanPropertyHelper.getBeanPropertyDescriptor(this.entityClass, this.columnName);
        if (propertyDescriptor == null) {
            return BeanPropertyHelper.getBeanPropertyDescriptor(this.entityClass, "ext$");
        }
        return propertyDescriptor;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isClob() {
        return this.clob;
    }

    public void setClob(boolean clob) {
        this.clob = clob;
    }

    public boolean isBlob() {
        return this.blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
    }

    public boolean isTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
    }

    public <T> Object getValue(T item) {
        if (this.getPropertyDescriptor().isExt$BeanProperty()) {
            return ((BaseData)item).getExt$().get(this.columnName);
        }
        return this.getPropertyDescriptor().getPropertyValue(item);
    }
}
