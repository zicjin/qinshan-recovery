/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.dao.LcdpCrdLogDao;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.mapper.LcdpCrdLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCrdLogDaoImpl
extends MybatisDaoSupport<LcdpCrdLogBean, Long>
implements LcdpCrdLogDao {
    @Autowired
    private LcdpCrdLogMapper lcdpCrdLogMapper;

    public LcdpCrdLogMapper getMapper() {
        return this.lcdpCrdLogMapper;
    }
}
