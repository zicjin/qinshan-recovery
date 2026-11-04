/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.dao.impl;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalCompConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.dao.LcdpGlobalCompConfigDao;
import com.sunwayworld.cloud.module.lcdp.config.mapper.LcdpGlobalCompConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpGlobalCompConfigDaoImpl
extends MybatisDaoSupport<LcdpGlobalCompConfigBean, Long>
implements LcdpGlobalCompConfigDao {
    @Autowired
    private LcdpGlobalCompConfigMapper lcdpGlobalCompConfigMapper;

    public LcdpGlobalCompConfigMapper getMapper() {
        return this.lcdpGlobalCompConfigMapper;
    }
}
