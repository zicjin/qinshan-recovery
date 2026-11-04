/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableIndexBean;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpTableIndexDao;
import com.sunwayworld.cloud.module.lcdp.table.persistent.mapper.LcdpTableIndexMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpTableIndexDaoImpl
extends MybatisDaoSupport<LcdpTableIndexBean, Long>
implements LcdpTableIndexDao {
    @Autowired
    private LcdpTableIndexMapper lcdpTableIndexMapper;

    public LcdpTableIndexMapper getMapper() {
        return this.lcdpTableIndexMapper;
    }
}
