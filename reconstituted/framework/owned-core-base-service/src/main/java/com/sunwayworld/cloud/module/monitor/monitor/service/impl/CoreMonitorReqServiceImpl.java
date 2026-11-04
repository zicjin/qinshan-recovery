/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorReqBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorReqDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorReqService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorReqServiceImpl
implements CoreMonitorReqService {
    @Autowired
    private CoreMonitorReqDao coreMonitorReqDao;

    @Override
    public CoreMonitorReqDao getDao() {
        return this.coreMonitorReqDao;
    }

    @Override
    @Transactional
    public void insertMonitorReq(List<CoreMonitorReqBean> monitorReqList) {
        monitorReqList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(monitorReqList);
    }
}
