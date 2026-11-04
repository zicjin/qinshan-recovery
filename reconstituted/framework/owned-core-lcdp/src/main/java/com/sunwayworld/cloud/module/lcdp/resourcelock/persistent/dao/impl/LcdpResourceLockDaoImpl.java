/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourcelock.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.persistent.dao.LcdpResourceLockDao;
import com.sunwayworld.cloud.module.lcdp.resourcelock.persistent.mapper.LcdpResourceLockMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceLockDaoImpl
extends MybatisDaoSupport<LcdpResourceLockBean, Long>
implements LcdpResourceLockDao {
    @Autowired
    private LcdpResourceLockMapper lcdpResourceLockMapper;

    public LcdpResourceLockMapper getMapper() {
        return this.lcdpResourceLockMapper;
    }
}
