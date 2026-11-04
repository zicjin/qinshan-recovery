/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpResourceHistoryDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpResourceHistoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceHistoryDaoImpl
extends MybatisDaoSupport<LcdpResourceHistoryBean, Long>
implements LcdpResourceHistoryDao {
    @Autowired
    private LcdpResourceHistoryMapper lcdpResourceHistoryMapper;

    public LcdpResourceHistoryMapper getMapper() {
        return this.lcdpResourceHistoryMapper;
    }
}
