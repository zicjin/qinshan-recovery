/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.base.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import java.util.List;
import java.util.Map;

public interface GenericMapImportService {
    default public void setProperty(Map item) {
        item.put("ID", ApplicationContextHelper.getNextIdentity());
    }

    default public void setProperty(Map item, boolean dataRepeatInsertFlag) {
        item.put("ID", ApplicationContextHelper.getNextIdentity());
    }

    default public void postImport(List<Map> itemList) {
    }

    default public void setProperty(Map item, boolean dataRepeatInsertFlag, List<Map> mapList) {
        item.put("ID", ApplicationContextHelper.getNextIdentity());
    }
}
