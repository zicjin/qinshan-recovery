/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.config.service.CoreMonitorConfigService;
import com.sunwayworld.cloud.module.monitor.core.support.CoreMonitorHelper;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppBean;
import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorServerDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorAppService;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorServerServiceImpl
implements CoreMonitorServerService {
    @Autowired
    private CoreMonitorServerDao coreMonitorServerDao;
    @Autowired
    private CoreMonitorAppService coreMonitorAppService;
    @Autowired
    @Lazy
    private CoreMonitorConfigService coreMonitorConfigService;

    @Override
    public CoreMonitorServerDao getDao() {
        return this.coreMonitorServerDao;
    }

    @Override
    @Transactional
    public void insertServerAndApp() {
        CoreMonitorServerBean coreServer = CoreMonitorHelper.getServerInfo();
        if (BeanUtils.isEmpty(this.getDao().selectByIdIfPresent(coreServer.getId()))) {
            this.getDao().insert(coreServer);
        }
        CoreMonitorAppBean coreApp = CoreMonitorHelper.getAppInfo();
        if (BeanUtils.isEmpty(this.coreMonitorAppService.getDao().selectByIdIfPresent((String)coreApp.getId()))) {
            this.coreMonitorAppService.getDao().insert((CoreMonitorAppBean)coreApp);
        }
    }

    @Override
    @Cacheable(value={"T_CORE_MONITOR_SERVER"}, key="'ALL'")
    public List<CoreMonitorServerBean> selectAll() {
        return this.getDao().selectAll();
    }
}
