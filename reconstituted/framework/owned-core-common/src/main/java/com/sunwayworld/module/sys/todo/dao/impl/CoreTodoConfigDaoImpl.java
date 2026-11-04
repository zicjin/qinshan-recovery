/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.dao.CoreTodoConfigDao;
import com.sunwayworld.module.sys.todo.mapper.CoreTodoConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTodoConfigDaoImpl
extends MybatisDaoSupport<CoreTodoConfigBean, Long>
implements CoreTodoConfigDao {
    @Autowired
    private CoreTodoConfigMapper configMapper;

    public CoreTodoConfigMapper getMapper() {
        return this.configMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_TODO_CONFIG"}, key="'ACTIVATED'")
    public void cacheEvict(CoreTodoConfigBean oldItem, CoreTodoConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_TODO_CONFIG", "ACTIVATED");
            }
        });
    }
}
