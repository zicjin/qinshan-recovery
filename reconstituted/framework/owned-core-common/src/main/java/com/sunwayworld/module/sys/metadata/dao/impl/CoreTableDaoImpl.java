/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.metadata.bean.CoreTableBean;
import com.sunwayworld.module.sys.metadata.dao.CoreTableDao;
import com.sunwayworld.module.sys.metadata.mapper.CoreTableMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreTableDaoImpl
extends MybatisDaoSupport<CoreTableBean, String>
implements CoreTableDao {
    @Autowired
    private CoreTableMapper coreTableMapper;

    @Override
    public GenericMapper<String> getMapper() {
        return this.coreTableMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_TABLE"}, key="#id", unless="#result == null")
    public CoreTableBean selectByIdIfPresent(String id) {
        return (CoreTableBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_TABLE"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreTableBean oldItem, CoreTableBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_TABLE", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
            }
        });
    }

    @Override
    public List<Map<String, Object>> selectFieldListByCondition(MapperParameter mapperParameter) {
        return this.coreTableMapper.selectFieldListByCondition(mapperParameter);
    }
}
