/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorAppRuntimeDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorAppRuntimeService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorAppRuntimeServiceImpl
implements CoreMonitorAppRuntimeService {
    @Autowired
    private CoreMonitorAppRuntimeDao coreMonitorAppRuntimeDao;

    @Override
    public CoreMonitorAppRuntimeDao getDao() {
        return this.coreMonitorAppRuntimeDao;
    }

    @Override
    @Transactional
    public void insertAppRuntime(List<CoreMonitorAppRuntimeBean> appRuntimeList) {
        appRuntimeList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(appRuntimeList);
    }
}
