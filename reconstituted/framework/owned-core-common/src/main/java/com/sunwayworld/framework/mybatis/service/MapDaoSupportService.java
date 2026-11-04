/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.service;

import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import java.util.List;

public interface MapDaoSupportService {
    default public void preInsert(TableContextInstance instance) {
    }

    default public void preInsertList(TableContext context, List<TableContextInstance> instanceList) {
    }

    default public void preUpdateById(TableContextInstance instance) {
    }

    default public <ID> void preUpdateByIdList(TableContextInstance instance, List<ID> idList) {
    }

    default public void preUpdateByInstanceList(TableContext context, List<TableContextInstance> instanceList, List<String> updateColumnList) {
    }
}
