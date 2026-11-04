/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptsource.service.impl;

import com.sunwayworld.cloud.module.lcdp.scriptsource.bean.LcdpScriptSourceBean;
import com.sunwayworld.cloud.module.lcdp.scriptsource.persistent.dao.LcdpScriptSourceDao;
import com.sunwayworld.cloud.module.lcdp.scriptsource.service.LcdpScriptSourceService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpScriptSourceServiceImpl
implements LcdpScriptSourceService {
    @Autowired
    private LcdpScriptSourceDao lcdpScriptSourceDao;

    @Override
    public LcdpScriptSourceDao getDao() {
        return this.lcdpScriptSourceDao;
    }

    @Override
    public LcdpScriptSourceBean selectByFullName(String fullName) {
        LcdpScriptSourceBean filter = new LcdpScriptSourceBean();
        filter.setFullName(fullName);
        return this.getDao().selectFirstIfPresent(filter, new Order[0]);
    }
}
