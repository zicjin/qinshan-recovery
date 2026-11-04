/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuExtBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuExtDao;
import com.sunwayworld.module.sys.menu.mapper.CoreMenuExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuExtDaoImpl
extends MybatisDaoSupport<CoreMenuExtBean, String>
implements CoreMenuExtDao {
    @Autowired
    private CoreMenuExtMapper extMapper;

    public CoreMenuExtMapper getMapper() {
        return this.extMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_MENU_EXT"}, key="'ALL'")
    public void cacheEvict(CoreMenuExtBean oldItem, CoreMenuExtBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_MENU_EXT", "ALL");
            }
        });
    }
}
