/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeUserService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeUserServiceImpl
implements LcdpBizProcessNodeUserService {
    @Autowired
    private LcdpBizProcessNodeUserDao lcdpBizProcessNodeUserDao;

    @Override
    public LcdpBizProcessNodeUserDao getDao() {
        return this.lcdpBizProcessNodeUserDao;
    }
}
