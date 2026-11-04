/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioPageDraftDao;
import com.sunwayworld.module.design.studio.service.CoreStudioPageDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioPageDraftServiceImpl
implements CoreStudioPageDraftService {
    @Autowired
    private CoreStudioPageDraftDao pageDraftDao;

    @Override
    public CoreStudioPageDraftDao getDao() {
        return this.pageDraftDao;
    }
}
