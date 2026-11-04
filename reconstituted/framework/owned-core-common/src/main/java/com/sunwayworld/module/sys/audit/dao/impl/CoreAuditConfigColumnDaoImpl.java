/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.dao.CoreAuditConfigColumnDao;
import com.sunwayworld.module.sys.audit.mapper.CoreAuditConfigColumnMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditConfigColumnDaoImpl
extends MybatisDaoSupport<CoreAuditConfigColumnBean, Long>
implements CoreAuditConfigColumnDao {
    @Autowired
    private CoreAuditConfigColumnMapper configColumnMapper;
    @Lazy
    @Autowired
    private CoreAuditConfigColumnDao proxyInstance;

    public CoreAuditConfigColumnMapper getMapper() {
        return this.configColumnMapper;
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_AUDIT_CONFIG_COLUMN_LIST.BY_CONFIGID"}, keyGenerator="auditConfigColumnListByConfigIdCacheKey"), @CacheEvict(value={"T_CORE_AUDIT_CONFIG_COLUMN_AUDITABLE_LIST.BY_CONFIGID"}, keyGenerator="auditConfigColumnListByConfigIdCacheKey")})
    public void cacheEvict(CoreAuditConfigColumnBean oldItem, CoreAuditConfigColumnBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                String redisKey = this.getRedisKey(oldItem, newItem);
                RedisHelper.evict("T_CORE_AUDIT_CONFIG_COLUMN_LIST.BY_CONFIGID", redisKey);
                RedisHelper.evict("T_CORE_AUDIT_CONFIG_COLUMN_AUDITABLE_LIST.BY_CONFIGID", redisKey);
            }
        });
    }

    @Bean(value={"auditConfigColumnListByConfigIdCacheKey"})
    public KeyGenerator auditConfigColumnListByConfigIdCacheKey() {
        return (t, m, p) -> this.getRedisKey((CoreAuditConfigColumnBean)p[0], (CoreAuditConfigColumnBean)p[1]);
    }

    private String getRedisKey(CoreAuditConfigColumnBean oldItem, CoreAuditConfigColumnBean newItem) {
        return Optional.ofNullable(oldItem).orElse(newItem).getConfigId();
    }
}
