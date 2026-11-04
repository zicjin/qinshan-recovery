/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.CoreCodeGenerateConfigFieldDao;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.CoreCodeGenerateConfigFieldService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCodeGenerateConfigFieldServiceImpl
implements CoreCodeGenerateConfigFieldService {
    @Autowired
    private CoreCodeGenerateConfigFieldDao fieldDao;

    @Override
    public CoreCodeGenerateConfigFieldDao getDao() {
        return this.fieldDao;
    }
}
