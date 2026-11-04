/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutconfig.bean.LcdpCheckoutConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.persistent.dao.LcdpCheckoutConfigDao;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.persistent.mapper.LcdpCheckoutConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCheckoutConfigDaoImpl
extends MybatisDaoSupport<LcdpCheckoutConfigBean, Long>
implements LcdpCheckoutConfigDao {
    @Autowired
    private LcdpCheckoutConfigMapper lcdpCheckoutConfigMapper;

    public LcdpCheckoutConfigMapper getMapper() {
        return this.lcdpCheckoutConfigMapper;
    }
}
