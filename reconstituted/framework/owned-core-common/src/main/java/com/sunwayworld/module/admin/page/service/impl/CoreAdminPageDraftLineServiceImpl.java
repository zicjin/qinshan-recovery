/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageDraftLineDao;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageDraftLineServiceImpl
implements CoreAdminPageDraftLineService {
    @Autowired
    private CoreAdminPageDraftLineDao dao;

    @Override
    public CoreAdminPageDraftLineDao getDao() {
        return this.dao;
    }
}
