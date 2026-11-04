/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.dao.CoreAuditConfigDao;
import com.sunwayworld.module.sys.audit.mapper.CoreAuditConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditConfigDaoImpl
extends MybatisDaoSupport<CoreAuditConfigBean, String>
implements CoreAuditConfigDao {
    @Autowired
    private CoreAuditConfigMapper configMapper;

    public CoreAuditConfigMapper getMapper() {
        return this.configMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_AUDIT_CONFIG"}, key="#id", unless="#result == null")
    public CoreAuditConfigBean selectByIdIfPresent(String id) {
        return (CoreAuditConfigBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_AUDIT_CONFIG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreAuditConfigBean oldItem, CoreAuditConfigBean newItem) {
        RedisHelper.evict("AUDIT_TABLE_CONFIG", (String)BeanUtils.getPropertyValue(oldItem, newItem, "ID"));
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_AUDIT_CONFIG", (String)BeanUtils.getPropertyValue(oldItem, newItem, "ID"));
            }
        });
    }
}
