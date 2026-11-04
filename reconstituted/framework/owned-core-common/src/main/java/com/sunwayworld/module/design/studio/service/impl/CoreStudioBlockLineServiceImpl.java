/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioBlockLineDao;
import com.sunwayworld.module.design.studio.service.CoreStudioBlockLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioBlockLineServiceImpl
implements CoreStudioBlockLineService {
    @Autowired
    private CoreStudioBlockLineDao studioBlockLineDao;

    @Override
    public CoreStudioBlockLineDao getDao() {
        return this.studioBlockLineDao;
    }
}
