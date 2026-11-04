/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleBean;
import com.sunwayworld.module.sys.rounding.dao.CoreRoundingRuleDao;
import com.sunwayworld.module.sys.rounding.mapper.CoreRoundingRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreRoundingRuleDaoImpl
extends MybatisDaoSupport<CoreRoundingRuleBean, String>
implements CoreRoundingRuleDao {
    @Autowired
    private CoreRoundingRuleMapper mapper;

    public CoreRoundingRuleMapper getMapper() {
        return this.mapper;
    }

    @Override
    @Cacheable(value={"T_CORE_ROUNDING_RULE"}, key="#id", unless="#result == null")
    public CoreRoundingRuleBean selectByIdIfPresent(String id) {
        return (CoreRoundingRuleBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_ROUNDING_RULE"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreRoundingRuleBean oldItem, CoreRoundingRuleBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ROUNDING_RULE", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
