/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.service.impl;

import com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.dao.CoreEquiptMonitorLogDao;
import com.sunwayworld.cloud.module.equipt.monitorconfig.service.CoreEquiptMonitorLogService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptMonitorLogServiceImpl
implements CoreEquiptMonitorLogService {
    @Autowired
    private CoreEquiptMonitorLogDao coreEquiptMonitorLogDao;

    @Override
    public CoreEquiptMonitorLogDao getDao() {
        return this.coreEquiptMonitorLogDao;
    }
}
