/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.menu.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.menu.bean.CoreAdminMenuBean;
import com.sunwayworld.module.admin.menu.dao.CoreAdminMenuDao;
import com.sunwayworld.module.admin.menu.mapper.CoreAdminMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminMenuDaoImpl
extends MybatisDaoSupport<CoreAdminMenuBean, String>
implements CoreAdminMenuDao {
    @Autowired
    private CoreAdminMenuMapper menuMapper;

    public CoreAdminMenuMapper getMapper() {
        return this.menuMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ADMIN_MENU"}, key="'ALL'")
    public void cacheEvict(CoreAdminMenuBean oldItem, CoreAdminMenuBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ADMIN_MENU", "ALL");
            }
        });
    }
}
