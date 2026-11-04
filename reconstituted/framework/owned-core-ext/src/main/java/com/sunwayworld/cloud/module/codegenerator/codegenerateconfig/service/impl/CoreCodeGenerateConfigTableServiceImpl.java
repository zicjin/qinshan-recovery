/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.CoreCodeGenerateConfigTableDao;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.CoreCodeGenerateConfigTableService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCodeGenerateConfigTableServiceImpl
implements CoreCodeGenerateConfigTableService {
    @Autowired
    private CoreCodeGenerateConfigTableDao tableDao;

    @Override
    public CoreCodeGenerateConfigTableDao getDao() {
        return this.tableDao;
    }
}
