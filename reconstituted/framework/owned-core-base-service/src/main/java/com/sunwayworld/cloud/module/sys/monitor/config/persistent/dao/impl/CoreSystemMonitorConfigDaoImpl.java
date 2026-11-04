/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.config.persistent.dao.impl;

import com.sunwayworld.cloud.module.sys.monitor.config.bean.CoreSystemMonitorConfigBean;
import com.sunwayworld.cloud.module.sys.monitor.config.persistent.dao.CoreSystemMonitorConfigDao;
import com.sunwayworld.cloud.module.sys.monitor.config.persistent.mapper.CoreSystemMonitorConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSystemMonitorConfigDaoImpl
extends MybatisDaoSupport<CoreSystemMonitorConfigBean, String>
implements CoreSystemMonitorConfigDao {
    @Autowired
    private CoreSystemMonitorConfigMapper coreSystemMonitorConfigMapper;

    public CoreSystemMonitorConfigMapper getMapper() {
        return this.coreSystemMonitorConfigMapper;
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_SYSTEM_MONITOR_CONFIG"}, key="'ALL'"), @CacheEvict(value={"T_CORE_SYSTEM_MONITOR_CONFIG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")})
    public void cacheEvict(CoreSystemMonitorConfigBean oldItem, CoreSystemMonitorConfigBean newItem) {
    }
}
