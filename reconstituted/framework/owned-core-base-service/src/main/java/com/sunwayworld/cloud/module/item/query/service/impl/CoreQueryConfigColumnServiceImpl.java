/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.query.service.impl;

import com.sunwayworld.cloud.module.item.query.persistent.dao.CoreQueryConfigColumnDao;
import com.sunwayworld.cloud.module.item.query.service.CoreQueryConfigColumnService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreQueryConfigColumnServiceImpl
implements CoreQueryConfigColumnService {
    @Autowired
    private CoreQueryConfigColumnDao configDao;

    @Override
    public CoreQueryConfigColumnDao getDao() {
        return this.configDao;
    }
}
