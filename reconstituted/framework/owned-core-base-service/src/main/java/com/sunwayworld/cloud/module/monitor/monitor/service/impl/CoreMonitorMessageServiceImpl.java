/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.service.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorMessageBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorMessageDao;
import com.sunwayworld.cloud.module.monitor.monitor.service.CoreMonitorMessageService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreMonitorMessageServiceImpl
implements CoreMonitorMessageService {
    @Autowired
    private CoreMonitorMessageDao coreMonitorMessageDao;

    @Override
    public CoreMonitorMessageDao getDao() {
        return this.coreMonitorMessageDao;
    }

    @Override
    public List<CoreMonitorMessageBean> selectLastMessages() {
        return this.getDao().selectLastMessages(new MapperParameter()).stream().map(e -> PersistableHelper.mapToPersistable(e, CoreMonitorMessageBean.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void insertMonitorMessage(List<CoreMonitorMessageBean> monitorMessageList) {
        monitorMessageList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(monitorMessageList);
    }
}
