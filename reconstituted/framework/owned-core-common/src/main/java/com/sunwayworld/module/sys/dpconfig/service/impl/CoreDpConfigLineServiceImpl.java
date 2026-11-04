/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.dpconfig.dao.CoreDpConfigLineDao;
import com.sunwayworld.module.sys.dpconfig.service.CoreDpConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDpConfigLineServiceImpl
implements CoreDpConfigLineService {
    @Autowired
    private CoreDpConfigLineDao coreDpConfigLineDao;

    @Override
    public CoreDpConfigLineDao getDao() {
        return this.coreDpConfigLineDao;
    }
}
