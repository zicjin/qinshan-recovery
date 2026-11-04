/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeActionDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeActionService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeActionServiceImpl
implements LcdpBizProcessNodeActionService {
    @Autowired
    private LcdpBizProcessNodeActionDao lcdpBizProcessNodeActionDao;

    @Override
    public LcdpBizProcessNodeActionDao getDao() {
        return this.lcdpBizProcessNodeActionDao;
    }
}
