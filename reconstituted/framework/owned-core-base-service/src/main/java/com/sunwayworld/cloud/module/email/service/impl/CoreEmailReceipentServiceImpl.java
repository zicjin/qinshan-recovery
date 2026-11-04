/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailReceipentDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailReceipentService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEmailReceipentServiceImpl
implements CoreEmailReceipentService {
    @Autowired
    private CoreEmailReceipentDao emailReceipentDao;

    @Override
    public CoreEmailReceipentDao getDao() {
        return this.emailReceipentDao;
    }
}
