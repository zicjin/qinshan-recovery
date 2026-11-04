/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpResourceCheckoutRecordDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpResourceCheckoutRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceCheckoutRecordDaoImpl
extends MybatisDaoSupport<LcdpResourceCheckoutRecordBean, Long>
implements LcdpResourceCheckoutRecordDao {
    @Autowired
    private LcdpResourceCheckoutRecordMapper lcdpResourceCheckoutRecordMapper;

    public LcdpResourceCheckoutRecordMapper getMapper() {
        return this.lcdpResourceCheckoutRecordMapper;
    }
}
