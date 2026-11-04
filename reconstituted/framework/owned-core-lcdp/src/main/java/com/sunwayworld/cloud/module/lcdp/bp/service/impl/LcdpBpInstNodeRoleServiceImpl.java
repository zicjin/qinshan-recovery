/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstNodeRoleDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBpInstNodeRoleService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstNodeRoleServiceImpl
implements LcdpBpInstNodeRoleService {
    @Autowired
    private LcdpBpInstNodeRoleDao lcdpBpInstNodeRoleDao;

    @Override
    public LcdpBpInstNodeRoleDao getDao() {
        return this.lcdpBpInstNodeRoleDao;
    }
}
