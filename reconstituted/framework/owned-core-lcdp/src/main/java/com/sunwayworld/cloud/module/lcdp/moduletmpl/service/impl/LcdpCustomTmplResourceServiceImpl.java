/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.service.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpCustomTmplResourceDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.service.LcdpCustomTmplResourceService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCustomTmplResourceServiceImpl
implements LcdpCustomTmplResourceService {
    @Autowired
    private LcdpCustomTmplResourceDao lcdpCustomTmplResourceDao;

    @Override
    public LcdpCustomTmplResourceDao getDao() {
        return this.lcdpCustomTmplResourceDao;
    }
}
