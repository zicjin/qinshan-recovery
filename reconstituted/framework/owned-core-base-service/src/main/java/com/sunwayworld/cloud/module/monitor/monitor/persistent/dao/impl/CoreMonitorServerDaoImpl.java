/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorServerBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorServerDao;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.mapper.CoreMonitorServerMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorServerDaoImpl
extends MybatisDaoSupport<CoreMonitorServerBean, String>
implements CoreMonitorServerDao {
    @Autowired
    private CoreMonitorServerMapper coreMonitorServerMapper;

    public CoreMonitorServerMapper getMapper() {
        return this.coreMonitorServerMapper;
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_MONITOR_SERVER"}, key="'ALL'"), @CacheEvict(value={"T_CORE_MONITOR_SERVER"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")})
    public void cacheEvict(CoreMonitorServerBean oldItem, CoreMonitorServerBean newItem) {
    }
}
