/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorServerRuntimeDao;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.mapper.CoreMonitorServerRuntimeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorServerRuntimeDaoImpl
extends MybatisDaoSupport<CoreMonitorServerRuntimeBean, Long>
implements CoreMonitorServerRuntimeDao {
    @Autowired
    private CoreMonitorServerRuntimeMapper coreMonitorServerRuntimeMapper;

    public CoreMonitorServerRuntimeMapper getMapper() {
        return this.coreMonitorServerRuntimeMapper;
    }
}
