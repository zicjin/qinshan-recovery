/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpPageI18nCodeDao;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpPageI18nCodeService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpPageI18nCodeServiceImpl
implements LcdpPageI18nCodeService {
    @Autowired
    private LcdpPageI18nCodeDao lcdpPageI18nCodeDao;

    @Override
    public LcdpPageI18nCodeDao getDao() {
        return this.lcdpPageI18nCodeDao;
    }
}
