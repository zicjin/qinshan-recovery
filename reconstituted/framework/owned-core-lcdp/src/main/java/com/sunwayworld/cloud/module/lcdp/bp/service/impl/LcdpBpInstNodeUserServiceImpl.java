/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstNodeUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstNodeUserService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstNodeUserServiceImpl
implements LcdpBpInstNodeUserService {
    @Autowired
    private LcdpBpInstNodeUserDao lcdpBpInstNodeUserDao;

    @Override
    public LcdpBpInstNodeUserDao getDao() {
        return this.lcdpBpInstNodeUserDao;
    }
}
