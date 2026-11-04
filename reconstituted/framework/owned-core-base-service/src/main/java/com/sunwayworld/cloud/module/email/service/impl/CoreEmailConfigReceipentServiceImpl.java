/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigReceipentDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigReceipentService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailConfigReceipentServiceImpl
implements CoreEmailConfigReceipentService {
    @Autowired
    private CoreEmailConfigReceipentDao configReceipentDao;

    @Override
    public CoreEmailConfigReceipentDao getDao() {
        return this.configReceipentDao;
    }
}
