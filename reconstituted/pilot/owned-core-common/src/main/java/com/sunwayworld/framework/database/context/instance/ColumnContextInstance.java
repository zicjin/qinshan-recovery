/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context.instance;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.io.Serializable;
import java.util.Map;

public class ColumnContextInstance
implements Serializable {
    private static final long serialVersionUID = -5106104499688608621L;
    private final ColumnContext columnContext;
    private boolean active = false;
    private Object value;
    private boolean valueAssigned = true;

    private ColumnContextInstance(ColumnContext columnContext, Object rawValue) {
        this.columnContext = columnContext;
        this.value = columnContext.parse(rawValue);
    }

    public static ColumnContextInstance of(TableContext tableContext, String column, Object entityItem) {
        ColumnContext columnContext = tableContext.getColumnContextList().stream().filter(c -> c.getColumnName().equalsIgnoreCase(column)).findAny().get();
        ColumnContextInstance instance = null;
        if (entityItem instanceof Map) {
            instance = new ColumnContextInstance(columnContext, CollectionUtils.getValueIgnorecase((Map)entityItem, column));
            if (!CollectionUtils.containsIgnoreCase((Map)entityItem, column)) {
                instance.valueAssigned = false;
            } else {
                instance.active = true;
            }
        } else {
            BeanPropertyDescriptor pd = BeanPropertyDescriptor.of(ClassUtils.getRawType(entityItem.getClass()), column);
            if (pd != null) {
                instance = new ColumnContextInstance(columnContext, pd.getPropertyValue(entityItem));
                if (BeanPropertyListener.class.isAssignableFrom(entityItem.getClass())) {
                    instance.active = ((BeanPropertyListener)entityItem).getChangedPropertyEventList().stream().anyMatch(e -> e.getPropertyDescriptor().equals(pd));
                } else if (instance.getValue() != null) {
                    instance.active = true;
                }
            } else {
                String value = ((Persistable)entityItem).getExt$().get(column);
                instance = new ColumnContextInstance(columnContext, value);
                if (((Persistable)entityItem).getExt$().containsKey(column)) {
                    instance.active = true;
                } else {
                    instance.valueAssigned = false;
                }
            }
        }
        return instance;
    }

    public ColumnContext getColumnContext() {
        return this.columnContext;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isValueAssigned() {
        return this.valueAssigned;
    }

    public void setValueAssigned(boolean valueAssigned) {
        this.valueAssigned = valueAssigned;
    }
}
