/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.page.bean.CorePageViewConfigBean;
import com.sunwayworld.module.sys.page.dao.CorePageViewConfigDao;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageViewConfigDaoImpl
extends MybatisDaoSupport<CorePageViewConfigBean, String>
implements CorePageViewConfigDao {
    @Override
    @Cacheable(value={"T_CORE_PAGE_VIEW_CONFIG"}, key="#id", unless="#result == null")
    public CorePageViewConfigBean selectByIdIfPresent(String id) {
        return (CorePageViewConfigBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_PAGE_VIEW_CONFIG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CorePageViewConfigBean oldItem, CorePageViewConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_PAGE_VIEW_CONFIG", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
