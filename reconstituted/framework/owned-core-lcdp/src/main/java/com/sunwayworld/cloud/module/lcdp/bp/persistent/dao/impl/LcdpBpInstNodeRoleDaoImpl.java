/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstNodeRoleBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstNodeRoleDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBpInstNodeRoleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstNodeRoleDaoImpl
extends MybatisDaoSupport<LcdpBpInstNodeRoleBean, Long>
implements LcdpBpInstNodeRoleDao {
    @Autowired
    private LcdpBpInstNodeRoleMapper lcdpBpInstNodeRoleMapper;

    public LcdpBpInstNodeRoleMapper getMapper() {
        return this.lcdpBpInstNodeRoleMapper;
    }
}
