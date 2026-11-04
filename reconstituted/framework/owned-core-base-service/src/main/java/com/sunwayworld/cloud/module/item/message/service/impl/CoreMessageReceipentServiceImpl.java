/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.message.service.impl;

import com.sunwayworld.cloud.module.item.message.persistent.dao.CoreMessageReceipentDao;
import com.sunwayworld.cloud.module.item.message.service.CoreMessageReceipentService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMessageReceipentServiceImpl
implements CoreMessageReceipentService {
    @Autowired
    private CoreMessageReceipentDao coreMessageReceipentDao;

    @Override
    public CoreMessageReceipentDao getDao() {
        return this.coreMessageReceipentDao;
    }
}
