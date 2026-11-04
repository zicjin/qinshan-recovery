/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.dao.support;

import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterInsertListener;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DaoAfterInsertSupport {
    @Autowired(required=false)
    private List<DaoAfterInsertListener> daoAfterInsertListeners = Collections.emptyList();

    public void afterInsert(DaoAfterInsertContext context) {
        for (DaoAfterInsertListener daoAfterInsertListener : this.daoAfterInsertListeners) {
            daoAfterInsertListener.afterInsert(context);
        }
    }
}
