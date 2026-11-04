/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeMenuDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeMenuService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeMenuServiceImpl
implements LcdpBizProcessNodeMenuService {
    @Autowired
    private LcdpBizProcessNodeMenuDao lcdpBizProcessNodeMenuDao;

    @Override
    public LcdpBizProcessNodeMenuDao getDao() {
        return this.lcdpBizProcessNodeMenuDao;
    }
}
