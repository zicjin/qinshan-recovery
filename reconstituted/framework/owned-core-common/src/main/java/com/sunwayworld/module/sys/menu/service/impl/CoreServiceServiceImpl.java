/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.dao.CoreServiceDao;
import com.sunwayworld.module.sys.menu.service.CoreServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreServiceServiceImpl
implements CoreServiceService {
    @Autowired
    private CoreServiceDao serviceDao;

    @Override
    public CoreServiceDao getDao() {
        return this.serviceDao;
    }
}
