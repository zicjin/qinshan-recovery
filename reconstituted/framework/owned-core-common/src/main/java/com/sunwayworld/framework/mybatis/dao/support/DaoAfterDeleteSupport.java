/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.dao.support;

import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteContext;
import com.sunwayworld.framework.mybatis.dao.support.DaoAfterDeleteListener;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DaoAfterDeleteSupport {
    @Autowired(required=false)
    private List<DaoAfterDeleteListener> daoAfterDeleteListeners = Collections.emptyList();

    public void afterDelete(DaoAfterDeleteContext context) {
        for (DaoAfterDeleteListener daoAfterDeleteListener : this.daoAfterDeleteListeners) {
            daoAfterDeleteListener.afterDelete(context);
        }
    }
}
