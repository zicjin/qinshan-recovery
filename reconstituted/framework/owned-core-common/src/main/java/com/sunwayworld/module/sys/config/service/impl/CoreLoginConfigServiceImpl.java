/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;
import com.sunwayworld.module.sys.config.dao.CoreLoginConfigDao;
import com.sunwayworld.module.sys.config.service.CoreLoginConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLoginConfigServiceImpl
implements CoreLoginConfigService {
    private static final String LOGIN_CONFIG_ID = "login-config";
    @Autowired
    private CoreLoginConfigDao coreLoginConfigDao;
    @Lazy
    @Autowired
    private CoreLoginConfigService proxyInstance;

    @Override
    public CoreLoginConfigDao getDao() {
        return this.coreLoginConfigDao;
    }

    @Override
    public CoreLoginConfigBean selectLoginConfig() {
        return (CoreLoginConfigBean)this.getDao().selectById(LOGIN_CONFIG_ID);
    }

    @Override
    public CoreLoginConfigBean selectDetail(String id) {
        return this.selectLoginConfig();
    }
}
