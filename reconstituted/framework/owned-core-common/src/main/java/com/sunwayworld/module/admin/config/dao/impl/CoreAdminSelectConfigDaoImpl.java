/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.config.bean.CoreAdminSelectConfigBean;
import com.sunwayworld.module.admin.config.dao.CoreAdminSelectConfigDao;
import com.sunwayworld.module.admin.config.mapper.CoreAdminSelectConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminSelectConfigDaoImpl
extends MybatisDaoSupport<CoreAdminSelectConfigBean, Long>
implements CoreAdminSelectConfigDao {
    @Autowired
    private CoreAdminSelectConfigMapper selectConfigMapper;

    public CoreAdminSelectConfigMapper getMapper() {
        return this.selectConfigMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ADMIN_SELECT_CONFIG"}, key="'ALL'")
    public void cacheEvict(CoreAdminSelectConfigBean oldItem, CoreAdminSelectConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ADMIN_SELECT_CONFIG", "ALL");
            }
        });
    }
}
