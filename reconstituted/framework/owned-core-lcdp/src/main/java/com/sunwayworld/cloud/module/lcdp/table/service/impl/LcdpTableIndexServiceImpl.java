/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.service.impl;

import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpTableIndexDao;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableIndexService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpTableIndexServiceImpl
implements LcdpTableIndexService {
    @Autowired
    private LcdpTableIndexDao lcdpTableIndexDao;

    @Override
    public LcdpTableIndexDao getDao() {
        return this.lcdpTableIndexDao;
    }
}
