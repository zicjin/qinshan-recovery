/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpTableDao;
import com.sunwayworld.cloud.module.lcdp.table.persistent.mapper.LcdpTableMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpTableDaoImpl
extends MybatisDaoSupport<LcdpTableBean, Long>
implements LcdpTableDao {
    @Autowired
    private LcdpTableMapper lcdpTableMapper;

    public LcdpTableMapper getMapper() {
        return this.lcdpTableMapper;
    }
}
