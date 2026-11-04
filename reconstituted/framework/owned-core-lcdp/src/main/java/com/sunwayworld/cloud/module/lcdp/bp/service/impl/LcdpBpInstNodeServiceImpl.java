/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstNodeDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstNodeService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstNodeServiceImpl
implements LcdpBpInstNodeService {
    @Autowired
    private LcdpBpInstNodeDao lcdpBpInstNodeDao;

    @Override
    public LcdpBpInstNodeDao getDao() {
        return this.lcdpBpInstNodeDao;
    }
}
