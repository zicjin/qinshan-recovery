/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorAppRuntimeDao;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.mapper.CoreMonitorAppRuntimeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorAppRuntimeDaoImpl
extends MybatisDaoSupport<CoreMonitorAppRuntimeBean, Long>
implements CoreMonitorAppRuntimeDao {
    @Autowired
    private CoreMonitorAppRuntimeMapper coreMonitorAppRuntimeMapper;

    public CoreMonitorAppRuntimeMapper getMapper() {
        return this.coreMonitorAppRuntimeMapper;
    }
}
