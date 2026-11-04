/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeServiceImpl
implements LcdpBizProcessNodeService {
    @Autowired
    private LcdpBizProcessNodeDao lcdpBizProcessNodeDao;

    @Override
    public LcdpBizProcessNodeDao getDao() {
        return this.lcdpBizProcessNodeDao;
    }
}
