/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.monitor.bean.CoreMonitorAppBean;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.dao.CoreMonitorAppDao;
import com.sunwayworld.cloud.module.monitor.monitor.persistent.mapper.CoreMonitorAppMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorAppDaoImpl
extends MybatisDaoSupport<CoreMonitorAppBean, String>
implements CoreMonitorAppDao {
    @Autowired
    private CoreMonitorAppMapper coreMonitorAppMapper;

    public CoreMonitorAppMapper getMapper() {
        return this.coreMonitorAppMapper;
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_MONITOR_APP"}, key="'ALL'"), @CacheEvict(value={"T_CORE_MONITOR_APP"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")})
    public void cacheEvict(CoreMonitorAppBean oldItem, CoreMonitorAppBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_MONITOR_APP", "ALL");
                RedisHelper.evict("T_CORE_MONITOR_APP", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            }
        });
    }
}
