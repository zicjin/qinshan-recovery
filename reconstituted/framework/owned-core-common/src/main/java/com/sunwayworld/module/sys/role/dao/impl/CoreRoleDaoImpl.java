/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.dao.CoreRoleDao;
import com.sunwayworld.module.sys.role.mapper.CoreRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRoleDaoImpl
extends MybatisDaoSupport<CoreRoleBean, Long>
implements CoreRoleDao {
    @Autowired
    private CoreRoleMapper mapper;

    public CoreRoleMapper getMapper() {
        return this.mapper;
    }

    @Override
    @Cacheable(value={"T_CORE_ROLE"}, key="'' + #id", unless="#result == null")
    public CoreRoleBean selectByIdIfPresent(Long id) {
        return (CoreRoleBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_ROLE"}, allEntries=true)
    public void cacheEvict(CoreRoleBean oldItem, CoreRoleBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ROLE", "ALL");
                RedisHelper.evict("T_CORE_ROLE", (String)BeanUtils.getPropertyValue(oldItem, newItem, "ID"));
            }
        });
    }
}
