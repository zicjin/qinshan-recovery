/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.config.bean.CoreAdminChooseConfigBean;
import com.sunwayworld.module.admin.config.dao.CoreAdminChooseConfigDao;
import com.sunwayworld.module.admin.config.mapper.CoreAdminChooseConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminChooseConfigDaoImpl
extends MybatisDaoSupport<CoreAdminChooseConfigBean, Long>
implements CoreAdminChooseConfigDao {
    @Autowired
    private CoreAdminChooseConfigMapper chooseConfigMapper;

    public CoreAdminChooseConfigMapper getMapper() {
        return this.chooseConfigMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ADMIN_CHOOSE_CONFIG"}, key="'ALL'")
    public void cacheEvict(CoreAdminChooseConfigBean oldItem, CoreAdminChooseConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ADMIN_CHOOSE_CONFIG", "ALL");
            }
        });
    }
}
