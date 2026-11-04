/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.service.impl;

import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpTableFieldDao;
import com.sunwayworld.cloud.module.lcdp.table.service.LcdpTableFieldService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpTableFieldServiceImpl
implements LcdpTableFieldService {
    @Autowired
    private LcdpTableFieldDao lcdpTableFieldDao;

    @Override
    public LcdpTableFieldDao getDao() {
        return this.lcdpTableFieldDao;
    }
}
