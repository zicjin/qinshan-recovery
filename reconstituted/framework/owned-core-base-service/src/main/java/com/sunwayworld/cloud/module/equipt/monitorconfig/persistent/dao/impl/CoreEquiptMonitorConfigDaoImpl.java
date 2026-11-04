/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.dao.impl;

import com.sunwayworld.cloud.module.equipt.monitorconfig.bean.CoreEquiptMonitorConfigBean;
import com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.dao.CoreEquiptMonitorConfigDao;
import com.sunwayworld.cloud.module.equipt.monitorconfig.persistent.mapper.CoreEquiptMonitorConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptMonitorConfigDaoImpl
extends MybatisDaoSupport<CoreEquiptMonitorConfigBean, Long>
implements CoreEquiptMonitorConfigDao {
    @Autowired
    private CoreEquiptMonitorConfigMapper coreEquiptMonitorConfigMapper;

    public CoreEquiptMonitorConfigMapper getMapper() {
        return this.coreEquiptMonitorConfigMapper;
    }
}
