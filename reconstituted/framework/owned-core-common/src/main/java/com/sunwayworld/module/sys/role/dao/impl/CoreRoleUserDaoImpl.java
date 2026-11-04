/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.dao.CoreRoleUserDao;
import com.sunwayworld.module.sys.role.mapper.CoreRoleUserMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreRoleUserDaoImpl
extends MybatisDaoSupport<CoreRoleUserBean, Long>
implements CoreRoleUserDao {
    @Autowired
    private CoreRoleUserMapper roleUserMapper;

    public CoreRoleUserMapper getMapper() {
        return this.roleUserMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ROLE_USER.BY_USERID"}, keyGenerator="roleUserByUserIdKeyGenerator")
    public void cacheEvict(CoreRoleUserBean oldItem, CoreRoleUserBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ROLE_USER.BY_USERID", this.getRedisKey(oldItem, newItem));
                RedisHelper.evict("T_CORE_ROLE_USER", "ALL");
            }
        });
    }

    @Bean(value={"roleUserByUserIdKeyGenerator"})
    public KeyGenerator roleUserByUserIdKeyGenerator() {
        return (t, m, p) -> this.getRedisKey((CoreRoleUserBean)p[1], (CoreRoleUserBean)p[0]);
    }

    private String getRedisKey(CoreRoleUserBean oldItem, CoreRoleUserBean newItem) {
        return ConvertUtils.convert(Optional.ofNullable(newItem).orElse(oldItem).getUserId(), String.class);
    }
}
