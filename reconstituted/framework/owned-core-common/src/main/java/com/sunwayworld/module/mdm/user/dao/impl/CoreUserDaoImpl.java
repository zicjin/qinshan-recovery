/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.tenant.service.CoreAdminTenantService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.dao.CoreUserDao;
import com.sunwayworld.module.mdm.user.mapper.CoreUserMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreUserDaoImpl
extends MybatisDaoSupport<CoreUserBean, String>
implements CoreUserDao {
    @Autowired
    private CoreUserMapper userMapper;
    @Lazy
    @Autowired
    private TenantManager tenantManager;
    @Lazy
    @Autowired
    private CoreAdminTenantService tenantService;

    public CoreUserMapper getMapper() {
        return this.userMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_USER"}, key="#id", unless="#result == null")
    public CoreUserBean selectByIdIfPresent(String id) {
        return (CoreUserBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_USER"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreUserBean oldItem, CoreUserBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_USER", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            } else if (this.tenantManager.isTenantActivated() && !this.tenantService.isSynchronizing(Optional.ofNullable(oldItem).orElse(newItem).getId())) {
                this.tenantService.syncUser(TenantContext.getTenant(), oldItem, newItem);
            }
        });
    }
}
