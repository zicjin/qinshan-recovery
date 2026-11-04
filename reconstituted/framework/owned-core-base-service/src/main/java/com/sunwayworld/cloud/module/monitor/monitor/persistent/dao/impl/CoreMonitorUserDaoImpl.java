/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorUserBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorUserDao;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.mapper.CoreMonitorUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorUserDaoImpl
extends MybatisDaoSupport<CoreMonitorUserBean, Long>
implements CoreMonitorUserDao {
    @Autowired
    private CoreMonitorUserMapper coreMonitorUserMapper;

    public CoreMonitorUserMapper getMapper() {
        return this.coreMonitorUserMapper;
    }
}
