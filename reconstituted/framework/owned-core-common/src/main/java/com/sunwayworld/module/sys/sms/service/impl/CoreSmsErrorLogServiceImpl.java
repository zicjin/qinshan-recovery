/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.sms.dao.CoreSmsErrorLogDao;
import com.sunwayworld.module.sys.sms.service.CoreSmsErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreSmsErrorLogServiceImpl
implements CoreSmsErrorLogService {
    @Autowired
    private CoreSmsErrorLogDao smsErrorLogDao;

    @Override
    public CoreSmsErrorLogDao getDao() {
        return this.smsErrorLogDao;
    }
}
