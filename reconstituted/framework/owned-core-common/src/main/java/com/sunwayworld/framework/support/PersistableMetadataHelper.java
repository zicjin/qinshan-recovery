/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.data.annotation.Blob;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.ReadOnly;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Timestamp;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.exception.database.NoIdPropertyJdbcException;
import com.sunwayworld.framework.support.domain.Orderable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.StringUtils;

public class PersistableMetadataHelper {
    public static String getTableName(Class<?> persistableClass) {
        Table table = (persistableClass = ClassUtils.getRawType(persistableClass)).getAnnotation(Table.class);
        if (table != null && !StringUtils.isEmpty(table.value())) {
            return table.value().toUpperCase();
        }
        return StringUtils.removeEnd(persistableClass.getSimpleName().toUpperCase(), "BEAN");
    }

    public static boolean isModifiedTable(String table) {
        return StringUtils.contains(table, "_M_");
    }

    public static BeanPropertyDescriptor getIdPropertyDescriptor(Class<? extends Persistable<?>> persistableClass) {
        return BeanPropertyHelper.getBeanPropertyDescriptorList(persistableClass).stream().filter(s -> PersistableMetadataHelper.isIdPropertyDescriptor(s)).findFirst().orElseThrow(() -> new NoIdPropertyJdbcException(persistableClass));
    }

    public static BeanPropertyDescriptor getCreatedByOrgIdPropertyDescriptor(Class<? extends Persistable<?>> persistableClass) {
        return BeanPropertyHelper.getBeanPropertyDescriptorList(persistableClass).stream().filter(s -> PersistableMetadataHelper.isCreatedByOrgIdPropertyDescriptor(s)).findFirst().orElse(null);
    }

    public static BeanPropertyDescriptor getExt$PropertyDescriptor(Class<? extends Persistable<?>> persistableClass) {
        return BeanPropertyHelper.getBeanPropertyDescriptorList(persistableClass).stream().filter(s -> PersistableMetadataHelper.isExt$PropertyDescriptor(s)).findFirst().orElse(null);
    }

    public static BeanPropertyDescriptor getOrderNoPropertyDescriptor(Class<? extends Persistable<?>> persistableClass) {
        if (Orderable.class.isAssignableFrom(persistableClass)) {
            return BeanPropertyHelper.getBeanPropertyDescriptorList(persistableClass).stream().filter(s -> PersistableMetadataHelper.isOrderNoPropertyDescriptor(s)).findFirst().orElse(null);
        }
        return null;
    }

    public static boolean isIdPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(Id.class);
    }

    public static boolean isReadOnlyPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(ReadOnly.class);
    }

    public static boolean isTransientPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(Transient.class);
    }

    public static boolean isNotNullPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(NotNull.class);
    }

    public static boolean isClobPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(Clob.class);
    }

    public static boolean isBlobPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(Blob.class);
    }

    public static boolean isTimestampPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isAnnotationPresent(Timestamp.class);
    }

    public static boolean isExt$PropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isExt$BeanProperty();
    }

    public static boolean isInsertablePropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return !PersistableMetadataHelper.isTransientPropertyDescriptor(propertyDescriptor);
    }

    public static boolean isOrderNoPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return "orderNo".equalsIgnoreCase(propertyDescriptor.getName());
    }

    public static boolean isCreatedByIdPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return "createdById".equalsIgnoreCase(propertyDescriptor.getName());
    }

    public static boolean isCreatedByNamePropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return "createdByName".equalsIgnoreCase(propertyDescriptor.getName());
    }

    public static boolean isCreatedTimePropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return "createdTime".equalsIgnoreCase(propertyDescriptor.getName());
    }

    public static boolean isCreatedByOrgIdPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return "createdByOrgId".equalsIgnoreCase(propertyDescriptor.getName());
    }

    public static boolean isCreatedByOrgNamePropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return "createdByOrgName".equalsIgnoreCase(propertyDescriptor.getName());
    }

    public static boolean isUpdatablePropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return (PersistableMetadataHelper.isExt$PropertyDescriptor(propertyDescriptor) || !PersistableMetadataHelper.isTransientPropertyDescriptor(propertyDescriptor)) && !PersistableMetadataHelper.isReadOnlyPropertyDescriptor(propertyDescriptor) && !PersistableMetadataHelper.isCreatedByIdPropertyDescriptor(propertyDescriptor) && !PersistableMetadataHelper.isCreatedByNamePropertyDescriptor(propertyDescriptor) && !PersistableMetadataHelper.isCreatedTimePropertyDescriptor(propertyDescriptor) && !PersistableMetadataHelper.isCreatedByOrgIdPropertyDescriptor(propertyDescriptor) && !PersistableMetadataHelper.isCreatedByOrgNamePropertyDescriptor(propertyDescriptor) && !PersistableMetadataHelper.isIdPropertyDescriptor(propertyDescriptor);
    }

    public static boolean isSearchTermsPropertyDescriptor(BeanPropertyDescriptor propertyDescriptor) {
        return (PersistableMetadataHelper.isExt$PropertyDescriptor(propertyDescriptor) || !PersistableMetadataHelper.isTransientPropertyDescriptor(propertyDescriptor)) && !PersistableMetadataHelper.isReadOnlyPropertyDescriptor(propertyDescriptor);
    }
}
