/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.dao.CoreRolePermissionDao;
import com.sunwayworld.module.sys.role.mapper.CoreRolePermissionMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRolePermissionDaoImpl
extends MybatisDaoSupport<CoreRolePermissionBean, Long>
implements CoreRolePermissionDao {
    @Autowired
    private CoreRolePermissionMapper permissionMapper;

    public CoreRolePermissionMapper getMapper() {
        return this.permissionMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ROLE_PERMISSION.BY_ROLEID"}, keyGenerator="permissionByRoleIdKeyGenerator")
    public void cacheEvict(CoreRolePermissionBean oldItem, CoreRolePermissionBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ROLE_PERMISSION.BY_ROLEID", this.getRedisKey(oldItem, newItem));
            }
        });
    }

    @Bean(value={"permissionByRoleIdKeyGenerator"})
    public KeyGenerator permissionByRoleIdKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CoreRolePermissionBean)p[1], (CoreRolePermissionBean)p[0]);
    }

    private String getRedisKey(CoreRolePermissionBean oldItem, CoreRolePermissionBean newItem) {
        return ConvertUtils.convert(Optional.ofNullable(newItem).orElse(oldItem).getRoleId(), String.class);
    }
}
