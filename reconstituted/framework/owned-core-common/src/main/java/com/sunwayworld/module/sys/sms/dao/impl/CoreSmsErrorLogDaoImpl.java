/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sms.bean.CoreSmsErrorLogBean;
import com.sunwayworld.module.sys.sms.dao.CoreSmsErrorLogDao;
import com.sunwayworld.module.sys.sms.mapper.CoreSmsErrorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreSmsErrorLogDaoImpl
extends MybatisDaoSupport<CoreSmsErrorLogBean, Long>
implements CoreSmsErrorLogDao {
    @Autowired
    private CoreSmsErrorLogMapper smsErrorLogMapper;

    public CoreSmsErrorLogMapper getMapper() {
        return this.smsErrorLogMapper;
    }
}
