/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableFieldBean;
import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpTableFieldDao;
import com.sunwayworld.cloud.module.lcdp.table.persistent.mapper.LcdpTableFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpTableFieldDaoImpl
extends MybatisDaoSupport<LcdpTableFieldBean, Long>
implements LcdpTableFieldDao {
    @Autowired
    private LcdpTableFieldMapper lcdpTableFieldMapper;

    public LcdpTableFieldMapper getMapper() {
        return this.lcdpTableFieldMapper;
    }
}
