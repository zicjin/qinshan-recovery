/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpBizSignUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpBizSignUserService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpBizSignUserServiceImpl
implements LcdpBpBizSignUserService {
    @Autowired
    private LcdpBpBizSignUserDao lcdpBpBizSignUserDao;

    @Override
    public LcdpBpBizSignUserDao getDao() {
        return this.lcdpBpBizSignUserDao;
    }
}
