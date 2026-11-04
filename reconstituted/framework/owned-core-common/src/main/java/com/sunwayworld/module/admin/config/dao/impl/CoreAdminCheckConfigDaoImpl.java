/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.config.bean.CoreAdminCheckConfigBean;
import com.sunwayworld.module.admin.config.dao.CoreAdminCheckConfigDao;
import com.sunwayworld.module.admin.config.mapper.CoreAdminCheckConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminCheckConfigDaoImpl
extends MybatisDaoSupport<CoreAdminCheckConfigBean, String>
implements CoreAdminCheckConfigDao {
    @Autowired
    private CoreAdminCheckConfigMapper checkConfigMapper;

    public CoreAdminCheckConfigMapper getMapper() {
        return this.checkConfigMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ADMIN_CHECK_CONFIG"}, key="'ALL'")
    public void cacheEvict(CoreAdminCheckConfigBean oldItem, CoreAdminCheckConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ADMIN_CHECK_CONFIG", "ALL");
            }
        });
    }
}
