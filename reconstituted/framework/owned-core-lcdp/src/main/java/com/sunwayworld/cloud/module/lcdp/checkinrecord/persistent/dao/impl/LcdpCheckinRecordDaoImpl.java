/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.checkinrecord.bean.LcdpCheckinRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.persistent.dao.LcdpCheckinRecordDao;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.persistent.mapper.LcdpCheckinRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCheckinRecordDaoImpl
extends MybatisDaoSupport<LcdpCheckinRecordBean, Long>
implements LcdpCheckinRecordDao {
    @Autowired
    private LcdpCheckinRecordMapper lcdpCheckinRecordMapper;

    public LcdpCheckinRecordMapper getMapper() {
        return this.lcdpCheckinRecordMapper;
    }
}
