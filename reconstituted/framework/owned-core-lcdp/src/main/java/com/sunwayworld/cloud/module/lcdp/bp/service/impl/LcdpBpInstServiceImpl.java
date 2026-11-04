/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstServiceImpl
implements LcdpBpInstService {
    @Autowired
    private LcdpBpInstDao lcdpBpInstDao;

    @Override
    public LcdpBpInstDao getDao() {
        return this.lcdpBpInstDao;
    }
}
