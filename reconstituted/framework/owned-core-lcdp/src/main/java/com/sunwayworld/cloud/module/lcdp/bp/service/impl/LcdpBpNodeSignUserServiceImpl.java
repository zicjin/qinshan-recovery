/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpNodeSignUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpNodeSignUserService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpNodeSignUserServiceImpl
implements LcdpBpNodeSignUserService {
    @Autowired
    private LcdpBpNodeSignUserDao lcdpBpNodeSignUserDao;

    @Override
    public LcdpBpNodeSignUserDao getDao() {
        return this.lcdpBpNodeSignUserDao;
    }
}
