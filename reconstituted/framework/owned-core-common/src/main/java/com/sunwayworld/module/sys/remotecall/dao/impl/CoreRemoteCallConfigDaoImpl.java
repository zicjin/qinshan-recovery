/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallConfigBean;
import com.sunwayworld.module.sys.remotecall.dao.CoreRemoteCallConfigDao;
import com.sunwayworld.module.sys.remotecall.mapper.CoreRemoteCallConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRemoteCallConfigDaoImpl
extends MybatisDaoSupport<CoreRemoteCallConfigBean, String>
implements CoreRemoteCallConfigDao {
    @Autowired
    private CoreRemoteCallConfigMapper mapper;

    @Override
    public GenericMapper<String> getMapper() {
        return this.mapper;
    }

    @Override
    @Cacheable(value={"T_CORE_REMOTE_CALL_CONF"}, key="#id", unless="#result == null")
    public CoreRemoteCallConfigBean selectByIdIfPresent(String id) {
        return (CoreRemoteCallConfigBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_REMOTE_CALL_CONF"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreRemoteCallConfigBean oldItem, CoreRemoteCallConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_REMOTE_CALL_CONF", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
