/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.dao.LcdpCheckoutRecordDao;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.mapper.LcdpCheckoutRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCheckoutRecordDaoImpl
extends MybatisDaoSupport<LcdpCheckoutRecordBean, Long>
implements LcdpCheckoutRecordDao {
    @Autowired
    private LcdpCheckoutRecordMapper lcdpCheckoutRecordMapper;

    public LcdpCheckoutRecordMapper getMapper() {
        return this.lcdpCheckoutRecordMapper;
    }
}
