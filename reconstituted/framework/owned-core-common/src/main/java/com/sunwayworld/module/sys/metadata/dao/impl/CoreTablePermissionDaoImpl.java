/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.metadata.bean.CoreTablePermissionBean;
import com.sunwayworld.module.sys.metadata.dao.CoreTablePermissionDao;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTablePermissionDaoImpl
extends MybatisDaoSupport<CoreTablePermissionBean, Long>
implements CoreTablePermissionDao {
    @Override
    @CacheEvict(value={"T_CORE_TABLE_PERMISSION.BY_TABLE_CATEGORY"}, keyGenerator="tablePermissionByTableCategoryKeyGenerator")
    public void cacheEvict(CoreTablePermissionBean oldItem, CoreTablePermissionBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_TABLE_PERMISSION.BY_TABLE_CATEGORY", this.getRedisKey(oldItem, newItem));
            }
        });
    }

    @Bean(value={"tablePermissionByTableCategoryKeyGenerator"})
    public KeyGenerator tablePermissionByTableCategoryKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CoreTablePermissionBean)p[0], (CoreTablePermissionBean)p[1]);
    }

    private String getRedisKey(CoreTablePermissionBean oldItem, CoreTablePermissionBean newItem) {
        CoreTablePermissionBean item = Optional.ofNullable(oldItem).orElse(newItem);
        return item.getTableName() + "." + item.getCategory();
    }
}
