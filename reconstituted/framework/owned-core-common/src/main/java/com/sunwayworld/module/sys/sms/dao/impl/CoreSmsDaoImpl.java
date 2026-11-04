/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sms.bean.CoreSmsBean;
import com.sunwayworld.module.sys.sms.dao.CoreSmsDao;
import com.sunwayworld.module.sys.sms.mapper.CoreSmsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreSmsDaoImpl
extends MybatisDaoSupport<CoreSmsBean, Long>
implements CoreSmsDao {
    @Autowired
    private CoreSmsMapper smsMapper;

    public CoreSmsMapper getMapper() {
        return this.smsMapper;
    }
}
