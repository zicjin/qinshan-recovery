/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpViewDao;
import com.sunwayworld.cloud.module.lcdp.table.persistent.mapper.LcdpViewMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpViewDaoImpl
extends MybatisDaoSupport<LcdpViewBean, Long>
implements LcdpViewDao {
    @Autowired
    private LcdpViewMapper lcdpViewMapper;

    public LcdpViewMapper getMapper() {
        return this.lcdpViewMapper;
    }
}
