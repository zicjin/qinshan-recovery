/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.dao.CorePasswordPolicyDao;
import com.sunwayworld.module.sys.password.mapper.CorePasswordPolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePasswordPolicyDaoImpl
extends MybatisDaoSupport<CorePasswordPolicyBean, Long>
implements CorePasswordPolicyDao {
    @Autowired
    private CorePasswordPolicyMapper policyMapper;

    public CorePasswordPolicyMapper getMapper() {
        return this.policyMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_PASSWORD_POLICY"}, key="'' + #id", unless="#result == null")
    public CorePasswordPolicyBean selectByIdIfPresent(Long id) {
        return (CorePasswordPolicyBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_PASSWORD_POLICY"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CorePasswordPolicyBean oldItem, CorePasswordPolicyBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_PASSWORD_POLICY", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
