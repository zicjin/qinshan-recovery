/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service.impl;

import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.persistent.dao.CoreSqlGenerateFieldDao;
import com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.service.CoreSqlGenerateFieldService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSqlGenerateFieldServiceImpl
implements CoreSqlGenerateFieldService {
    @Autowired
    private CoreSqlGenerateFieldDao sqlFieldDao;

    @Override
    public CoreSqlGenerateFieldDao getDao() {
        return this.sqlFieldDao;
    }
}
