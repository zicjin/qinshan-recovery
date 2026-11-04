/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorUserDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorUserService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorUserServiceImpl
implements CoreMonitorUserService {
    @Autowired
    private CoreMonitorUserDao coreSystemUserDao;

    @Override
    public CoreMonitorUserDao getDao() {
        return this.coreSystemUserDao;
    }

    @Override
    @Transactional
    public void insertMonitorUser(List<CoreMonitorUserBean> monitorUserList) {
        monitorUserList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(monitorUserList);
    }
}
