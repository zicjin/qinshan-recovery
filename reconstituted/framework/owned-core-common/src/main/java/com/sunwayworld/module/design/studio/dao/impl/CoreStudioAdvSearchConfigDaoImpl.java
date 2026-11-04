/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import com.sunwayworld.module.design.studio.cache.CoreStudioCacheConfiguration;
import com.sunwayworld.module.design.studio.dao.CoreStudioAdvSearchConfigDao;
import com.sunwayworld.module.design.studio.mapper.CoreStudioAdvSearchConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioAdvSearchConfigDaoImpl
extends MybatisDaoSupport<CoreStudioAdvSearchConfigBean, Long>
implements CoreStudioAdvSearchConfigDao {
    @Autowired
    private CoreStudioAdvSearchConfigMapper configMapper;

    public CoreStudioAdvSearchConfigMapper getMapper() {
        return this.configMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_STUDIO_ADVSEARCH_CONFIG"}, keyGenerator="studioAdvancedConfigKeyGenerator")
    public void cacheEvict(CoreStudioAdvSearchConfigBean oldItem, CoreStudioAdvSearchConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_STUDIO_ADVSEARCH_CONFIG", CoreStudioCacheConfiguration.getRedisKey(oldItem, newItem));
            }
        });
    }
}
