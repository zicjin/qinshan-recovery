/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.config.service.impl;

import com.sunwayworld.cloud.module.monitor.config.bean.CoreMonitorConfigBean;
import com.sunwayworld.cloud.module.monitor.config.persistent.dao.CoreMonitorConfigDao;
import com.sunwayworld.cloud.module.monitor.config.service.CoreMonitorConfigService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorConfigServiceImpl
implements CoreMonitorConfigService {
    @Autowired
    private CoreMonitorConfigDao coreMonitorConfigDao;

    @Override
    public CoreMonitorConfigDao getDao() {
        return this.coreMonitorConfigDao;
    }

    @Override
    @Transactional
    public String insert(RestJsonWrapperBean jsonWrapper) {
        CoreMonitorConfigBean coreMonitorConfig = jsonWrapper.parseUnique(CoreMonitorConfigBean.class);
        coreMonitorConfig.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        this.getDao().insert(coreMonitorConfig);
        return coreMonitorConfig.getId();
    }

    @Override
    @Cacheable(value={"CORE-MONITOR-CONFIG"}, key="'system'")
    public CoreMonitorConfigBean selectById(String id) {
        return (CoreMonitorConfigBean)this.getDao().selectById(id);
    }
}
