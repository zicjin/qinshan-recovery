/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeRoleDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessNodeRoleService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeRoleServiceImpl
implements LcdpBizProcessNodeRoleService {
    @Autowired
    private LcdpBizProcessNodeRoleDao lcdpBizProcessNodeRoleDao;

    @Override
    public LcdpBizProcessNodeRoleDao getDao() {
        return this.lcdpBizProcessNodeRoleDao;
    }
}
