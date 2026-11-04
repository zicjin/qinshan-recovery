/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.BeanUtils;
import java.io.Serializable;
import java.util.List;

public interface GenericImportService<T extends Persistable<ID>, ID extends Serializable> {
    default public void setProperty(T item) {
        BeanUtils.setPropertyValue(item, "ID", ApplicationContextHelper.getNextIdentity());
    }

    default public void setProperty(T item, boolean dataRepeatInsertFlag) {
        BeanUtils.setPropertyValue(item, "ID", ApplicationContextHelper.getNextIdentity());
    }

    default public void postImport(List<T> itemList) {
    }
}
