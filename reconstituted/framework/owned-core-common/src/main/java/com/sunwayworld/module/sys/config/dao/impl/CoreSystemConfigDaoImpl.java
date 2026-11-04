/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.dao.CoreSystemConfigDao;
import com.sunwayworld.module.sys.config.mapper.CoreSystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSystemConfigDaoImpl
extends MybatisDaoSupport<CoreSystemConfigBean, String>
implements CoreSystemConfigDao {
    @Autowired
    private CoreSystemConfigMapper coreSystemConfigMapper;

    public CoreSystemConfigMapper getMapper() {
        return this.coreSystemConfigMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_SYSTEM_CONFIG"}, key="#id", unless="#result == null")
    public CoreSystemConfigBean selectByIdIfPresent(String id) {
        return (CoreSystemConfigBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_SYSTEM_CONFIG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreSystemConfigBean oldItem, CoreSystemConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_SYSTEM_CONFIG", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
