/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigLineBean;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailConfigLineDao;
import com.sunwayworld.module.sys.at.mapper.CoreAuditTrailConfigLineMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditTrailConfigLineDaoImpl
extends MybatisDaoSupport<CoreAuditTrailConfigLineBean, Long>
implements CoreAuditTrailConfigLineDao {
    @Autowired
    private CoreAuditTrailConfigLineMapper configLineMapper;

    public CoreAuditTrailConfigLineMapper getMapper() {
        return this.configLineMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_AT_CONFIG_LINE.BY_MASTER"}, keyGenerator="auditTrailCacheKeyGenerator")
    public void cacheEvict(CoreAuditTrailConfigLineBean oldItem, CoreAuditTrailConfigLineBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_AT_CONFIG_LINE.BY_MASTER", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "configId"), String.class));
            }
        });
    }

    @Bean(value={"auditTrailCacheKeyGenerator"})
    public KeyGenerator auditTrailCacheKeyGenerator() {
        return (o, m, p) -> {
            CoreAuditTrailConfigLineBean oldItem = (CoreAuditTrailConfigLineBean)p[0];
            CoreAuditTrailConfigLineBean newItem = (CoreAuditTrailConfigLineBean)p[1];
            return Optional.ofNullable(oldItem).orElse(newItem).getConfigId();
        };
    }
}
