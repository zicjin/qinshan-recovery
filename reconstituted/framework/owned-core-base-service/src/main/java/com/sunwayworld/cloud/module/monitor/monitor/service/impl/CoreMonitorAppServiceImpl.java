/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorAppDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorAppService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorAppServiceImpl
implements CoreMonitorAppService {
    @Autowired
    private CoreMonitorAppDao coreMonitorAppDao;

    @Override
    public CoreMonitorAppDao getDao() {
        return this.coreMonitorAppDao;
    }

    @Override
    @Cacheable(value={"T_CORE_MONITOR_APP"}, key="'ALL'")
    public List<CoreMonitorAppBean> selectAll() {
        return this.getDao().selectAll();
    }
}
