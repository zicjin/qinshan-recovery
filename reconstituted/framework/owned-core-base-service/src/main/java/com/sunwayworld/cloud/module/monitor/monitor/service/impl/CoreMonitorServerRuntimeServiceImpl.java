/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerRuntimeBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorServerRuntimeDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorServerRuntimeService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorServerRuntimeServiceImpl
implements CoreMonitorServerRuntimeService {
    @Autowired
    private CoreMonitorServerRuntimeDao coreMonitorServerRuntimeDao;

    @Override
    public CoreMonitorServerRuntimeDao getDao() {
        return this.coreMonitorServerRuntimeDao;
    }

    @Override
    @Transactional
    public void insertServerRuntime(List<CoreMonitorServerRuntimeBean> serverRuntimeList) {
        serverRuntimeList.forEach(e -> ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(serverRuntimeList);
    }
}
