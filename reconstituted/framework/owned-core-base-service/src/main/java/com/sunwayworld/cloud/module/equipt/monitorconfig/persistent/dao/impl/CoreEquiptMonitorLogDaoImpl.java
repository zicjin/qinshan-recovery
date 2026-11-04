/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorLogBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.dao.CoreEquiptMonitorLogDao;
import com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.mapper.CoreEquiptMonitorLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptMonitorLogDaoImpl
extends MybatisDaoSupport<CoreEquiptMonitorLogBean, Long>
implements CoreEquiptMonitorLogDao {
    @Autowired
    private CoreEquiptMonitorLogMapper coreEquiptMonitorLogMapper;

    public CoreEquiptMonitorLogMapper getMapper() {
        return this.coreEquiptMonitorLogMapper;
    }
}
