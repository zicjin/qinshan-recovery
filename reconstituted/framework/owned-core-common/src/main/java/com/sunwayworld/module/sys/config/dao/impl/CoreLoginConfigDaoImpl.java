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
import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;
import com.sunwayworld.module.sys.config.dao.CoreLoginConfigDao;
import com.sunwayworld.module.sys.config.mapper.CoreLoginConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreLoginConfigDaoImpl
extends MybatisDaoSupport<CoreLoginConfigBean, String>
implements CoreLoginConfigDao {
    @Autowired
    private CoreLoginConfigMapper coreLoginConfigMapper;

    public CoreLoginConfigMapper getMapper() {
        return this.coreLoginConfigMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_LOGIN_CONFIG"}, key="#id", unless="#result == null")
    public CoreLoginConfigBean selectByIdIfPresent(String id) {
        return (CoreLoginConfigBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_LOGIN_CONFIG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreLoginConfigBean oldItem, CoreLoginConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_LOGIN_CONFIG", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
