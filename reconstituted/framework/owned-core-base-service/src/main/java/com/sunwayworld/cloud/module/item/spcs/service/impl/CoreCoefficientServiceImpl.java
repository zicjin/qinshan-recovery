/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.service.impl;

import com.sunwayworld.cloud.module.item.spcs.persistent.dao.CoreCoefficientDao;
import com.sunwayworld.cloud.module.item.spcs.service.CoreCoefficientService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCoefficientServiceImpl
implements CoreCoefficientService {
    @Autowired
    private CoreCoefficientDao coefficientDao;

    @Override
    public CoreCoefficientDao getDao() {
        return this.coefficientDao;
    }
}
