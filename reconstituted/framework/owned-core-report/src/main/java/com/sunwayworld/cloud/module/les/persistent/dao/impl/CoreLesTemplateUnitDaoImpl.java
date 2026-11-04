/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.persistent.dao.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateUnitDao;
import com.sunwayworld.cloud.module.les.persistent.mapper.CoreLesTemplateUnitMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLesTemplateUnitDaoImpl
extends MybatisDaoSupport<CoreLesTemplateUnitBean, Long>
implements CoreLesTemplateUnitDao {
    @Autowired
    private CoreLesTemplateUnitMapper mapper;

    public CoreLesTemplateUnitMapper getMapper() {
        return this.mapper;
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_LES_UNIT_BY_TEMPLATE_ID"}, keyGenerator="lesTempleateIdCategoryKeyGenerator")})
    public void cacheEvict(CoreLesTemplateUnitBean oldItem, CoreLesTemplateUnitBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_LES_UNIT_BY_TEMPLATE_ID", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            }
        });
    }

    @Bean(value={"lesTempleateIdCategoryKeyGenerator"})
    public KeyGenerator lesTempleateIdCategoryKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CoreLesTemplateUnitBean)p[0], (CoreLesTemplateUnitBean)p[1]);
    }

    private String getRedisKey(CoreLesTemplateUnitBean oldItem, CoreLesTemplateUnitBean newItem) {
        CoreLesTemplateUnitBean item = Optional.ofNullable(oldItem).orElse(newItem);
        return String.valueOf(item.getTemplateId());
    }
}
