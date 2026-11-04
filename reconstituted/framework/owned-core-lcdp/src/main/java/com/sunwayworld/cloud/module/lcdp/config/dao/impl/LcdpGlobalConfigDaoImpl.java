/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.dao.impl;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.dao.LcdpGlobalConfigDao;
import com.sunwayworld.cloud.module.lcdp.config.mapper.LcdpGlobalConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpGlobalConfigDaoImpl
extends MybatisDaoSupport<LcdpGlobalConfigBean, Long>
implements LcdpGlobalConfigDao {
    @Autowired
    private LcdpGlobalConfigMapper lcdpGlobalConfigMapper;

    public LcdpGlobalConfigMapper getMapper() {
        return this.lcdpGlobalConfigMapper;
    }
}
