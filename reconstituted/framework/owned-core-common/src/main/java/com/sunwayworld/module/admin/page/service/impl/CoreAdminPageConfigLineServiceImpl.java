/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageConfigLineDao;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageConfigLineServiceImpl
implements CoreAdminPageConfigLineService {
    @Autowired
    private CoreAdminPageConfigLineDao dao;

    @Override
    public CoreAdminPageConfigLineDao getDao() {
        return this.dao;
    }
}
