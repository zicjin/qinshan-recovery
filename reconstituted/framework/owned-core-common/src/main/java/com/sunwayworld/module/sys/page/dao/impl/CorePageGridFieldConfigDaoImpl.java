/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import com.sunwayworld.module.sys.page.dao.CorePageGridFieldConfigDao;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageGridFieldConfigDaoImpl
extends MybatisDaoSupport<CorePageGridFieldConfigBean, Long>
implements CorePageGridFieldConfigDao {
    @Override
    @CacheEvict(value={"CORE_PAGE_CONFIG"}, keyGenerator="corePageConfigKeyGenerator")
    public void cacheEvict(CorePageGridFieldConfigBean oldItem, CorePageGridFieldConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("CORE_PAGE_CONFIG", this.getRedisKey(oldItem, newItem));
            }
        });
    }

    @Bean(value={"corePageConfigKeyGenerator"})
    public KeyGenerator corePageConfigKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CorePageGridFieldConfigBean)p[0], (CorePageGridFieldConfigBean)p[1]);
    }

    private String getRedisKey(CorePageGridFieldConfigBean oldItem, CorePageGridFieldConfigBean newItem) {
        CorePageGridFieldConfigBean config = Optional.ofNullable(newItem).orElse(oldItem);
        return config.getUserId() + "." + config.getPageId();
    }
}
