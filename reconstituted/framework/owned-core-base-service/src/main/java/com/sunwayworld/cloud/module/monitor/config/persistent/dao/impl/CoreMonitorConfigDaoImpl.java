/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.config.persistent.dao.impl;

import com.sunwayworld.cloud.module.monitor.config.bean.CoreMonitorConfigBean;
import com.sunwayworld.cloud.module.monitor.config.persistent.dao.CoreMonitorConfigDao;
import com.sunwayworld.cloud.module.monitor.config.persistent.mapper.CoreMonitorConfigMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreMonitorConfigDaoImpl
extends MybatisDaoSupport<CoreMonitorConfigBean, String>
implements CoreMonitorConfigDao {
    @Autowired
    private CoreMonitorConfigMapper coreMonitorConfigMapper;

    public CoreMonitorConfigMapper getMapper() {
        return this.coreMonitorConfigMapper;
    }

    @Override
    @CacheEvict(value={"CORE-MONITOR-CONFIG"}, key="'system'")
    public void cacheEvict(CoreMonitorConfigBean oldItem, CoreMonitorConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("CORE-MONITOR-CONFIG", "'system'");
            }
        });
    }
}
