/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeRoleBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeRoleDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBizProcessNodeRoleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeRoleDaoImpl
extends MybatisDaoSupport<LcdpBizProcessNodeRoleBean, Long>
implements LcdpBizProcessNodeRoleDao {
    @Autowired
    private LcdpBizProcessNodeRoleMapper lcdpBizProcessNodeRoleMapper;

    public LcdpBizProcessNodeRoleMapper getMapper() {
        return this.lcdpBizProcessNodeRoleMapper;
    }
}
