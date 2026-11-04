/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resourceversion.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.persistent.dao.LcdpResourceVersionDao;
import com.sunwayworld.cloud.module.lcdp.resourceversion.persistent.mapper.LcdpResourceVersionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceVersionDaoImpl
extends MybatisDaoSupport<LcdpResourceVersionBean, Long>
implements LcdpResourceVersionDao {
    @Autowired
    private LcdpResourceVersionMapper lcdpResourceVersionMapper;

    public LcdpResourceVersionMapper getMapper() {
        return this.lcdpResourceVersionMapper;
    }
}
