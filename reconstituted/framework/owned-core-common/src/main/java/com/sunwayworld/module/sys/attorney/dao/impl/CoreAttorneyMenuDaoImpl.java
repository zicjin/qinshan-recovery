/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import com.sunwayworld.module.sys.attorney.dao.CoreAttorneyMenuDao;
import com.sunwayworld.module.sys.attorney.mapper.CoreAttorneyMenuMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAttorneyMenuDaoImpl
extends MybatisDaoSupport<CoreAttorneyMenuBean, Long>
implements CoreAttorneyMenuDao {
    @Autowired
    private CoreAttorneyMenuMapper attorneyMenuMapper;

    public CoreAttorneyMenuMapper getMapper() {
        return this.attorneyMenuMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ATTORNEY_MENU.BY_ATTORNEYID"}, keyGenerator="attorneyMenuKeyGenerator")
    public void cacheEvict(CoreAttorneyMenuBean oldItem, CoreAttorneyMenuBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ATTORNEY_MENU.BY_ATTORNEYID", this.getRedisKey(oldItem, newItem));
            }
        });
    }

    @Bean(value={"attorneyMenuKeyGenerator"})
    public KeyGenerator attorneyMenuKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CoreAttorneyMenuBean)p[0], (CoreAttorneyMenuBean)p[1]);
    }

    private String getRedisKey(CoreAttorneyMenuBean oldItem, CoreAttorneyMenuBean newItem) {
        return "" + Optional.ofNullable(oldItem).orElse(newItem).getAttorneyId();
    }
}
