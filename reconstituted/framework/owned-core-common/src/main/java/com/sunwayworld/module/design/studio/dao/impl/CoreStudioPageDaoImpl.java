/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioPageDao;
import com.sunwayworld.module.design.studio.mapper.CoreStudioPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreStudioPageDaoImpl
extends MybatisDaoSupport<CoreStudioPageBean, Long>
implements CoreStudioPageDao {
    @Autowired
    private CoreStudioPageMapper pageMapper;

    public CoreStudioPageMapper getMapper() {
        return this.pageMapper;
    }

    @Override
    public void cacheEvict(CoreStudioPageBean oldItem, CoreStudioPageBean newItem) {
        String url = oldItem == null ? newItem.getUrl() : oldItem.getUrl();
        RedisHelper.evict("T_CORE_STUDIO_PAGE", url);
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_STUDIO_PAGE", url);
            }
        });
    }
}
