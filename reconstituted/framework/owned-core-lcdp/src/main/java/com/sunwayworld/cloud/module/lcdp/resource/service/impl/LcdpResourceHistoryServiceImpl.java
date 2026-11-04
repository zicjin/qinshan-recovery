/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpResourceHistoryDao;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceHistoryServiceImpl
implements LcdpResourceHistoryService {
    @Autowired
    private LcdpResourceHistoryDao lcdpResourceHistoryDao;

    @Override
    public LcdpResourceHistoryDao getDao() {
        return this.lcdpResourceHistoryDao;
    }
}
