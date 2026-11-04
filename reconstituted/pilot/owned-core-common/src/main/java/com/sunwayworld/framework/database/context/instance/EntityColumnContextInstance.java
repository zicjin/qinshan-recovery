/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.context.instance;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.EntityColumnContext;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.io.Serializable;
import java.time.LocalDateTime;

public class EntityColumnContextInstance
implements Serializable {
    private static final long serialVersionUID = 4128823138613633488L;
    private EntityColumnContext columnContext;
    private boolean active = false;
    private Object value;
    private boolean valueAssigned = true;
    private static final boolean kingbase = ApplicationContextHelper.getEnvironment().getProperty("spring.datasource.driver-class-name").contains("kingbase");

    private EntityColumnContextInstance() {
    }

    public static EntityColumnContextInstance of(EntityColumnContext columnContext, Persistable<?> entityValue) {
        EntityColumnContextInstance instance = new EntityColumnContextInstance();
        instance.columnContext = columnContext;
        BeanPropertyDescriptor propertyDescriptor = columnContext.getPropertyDescriptor();
        if (propertyDescriptor.isExt$BeanProperty()) {
            if (!entityValue.getExt$().containsKey(columnContext.getColumnName())) {
                instance.valueAssigned = false;
            }
            instance.value = ConvertUtils.convert(entityValue.getExt$().get(columnContext.getColumnName()), columnContext.getType());
        } else {
            instance.value = ReflectionUtils.invokeMethod(propertyDescriptor.getReadMethod(), entityValue);
        }
        if (kingbase) {
            if (instance.value == null) {
                instance.value = "";
            }
            if (columnContext.getType() == LocalDateTime.class) {
                instance.value = instance.value + "";
            }
        }
        return instance;
    }

    public void setNewValue(Object newValue) {
        this.active = true;
        this.valueAssigned = true;
        this.value = newValue;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public EntityColumnContext getColumnContext() {
        return this.columnContext;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isValueAssigned() {
        return this.valueAssigned;
    }
}
