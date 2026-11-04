/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.submitlog.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.submitlog.bean.LcdpSubmitLogBean;
import com.sunwayworld.cloud.module.lcdp.submitlog.persistent.dao.LcdpSubmitLogDao;
import com.sunwayworld.cloud.module.lcdp.submitlog.persistent.mapper.LcdpSubmitLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpSubmitLogDaoImpl
extends MybatisDaoSupport<LcdpSubmitLogBean, Long>
implements LcdpSubmitLogDao {
    @Autowired
    private LcdpSubmitLogMapper lcdpSubmitLogMapper;

    public LcdpSubmitLogMapper getMapper() {
        return this.lcdpSubmitLogMapper;
    }
}
