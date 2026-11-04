/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.constant.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.dao.CoreConstantDao;
import com.sunwayworld.module.sys.constant.mapper.CoreConstantMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreConstantDaoImpl
extends MybatisDaoSupport<CoreConstantBean, String>
implements CoreConstantDao {
    @Autowired
    private CoreConstantMapper constantMapper;

    public CoreConstantMapper getMapper() {
        return this.constantMapper;
    }

    @Override
    public List<CoreConstantBean> selectAll() {
        return CoreConstantDao.super.selectAll();
    }

    @Override
    @Cacheable(value={"PERM_$T_CORE_CONSTANT"}, key="#id", unless="#result == null")
    public CoreConstantBean selectByIdIfPresent(String id) {
        return (CoreConstantBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"PERM_$T_CORE_CONSTANT"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreConstantBean oldItem, CoreConstantBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("PERM_$T_CORE_CONSTANT", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }
}
