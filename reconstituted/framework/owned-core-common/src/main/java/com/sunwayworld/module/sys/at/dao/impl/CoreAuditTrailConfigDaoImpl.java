/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailConfigBean;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailConfigDao;
import com.sunwayworld.module.sys.at.mapper.CoreAuditTrailConfigMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditTrailConfigDaoImpl
extends MybatisDaoSupport<CoreAuditTrailConfigBean, String>
implements CoreAuditTrailConfigDao {
    @Autowired
    private CoreAuditTrailConfigMapper configMapper;

    public CoreAuditTrailConfigMapper getMapper() {
        return this.configMapper;
    }

    @Override
    public List<CoreAuditTrailConfigBean> selectAuditTrailableByCondition(MapperParameter parameter) {
        List<Map<String, Object>> mapList = this.getMapper().selectAuditTrailableByCondition(parameter);
        mapList.forEach(CollectionUtils::lowerCaseKey);
        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreAuditTrailConfigBean.class)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value={"T_CORE_AT_CONFIG"}, key="#id", unless="#result == null")
    public CoreAuditTrailConfigBean selectByIdIfPresent(String id) {
        return (CoreAuditTrailConfigBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_AT_CONFIG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreAuditTrailConfigBean oldItem, CoreAuditTrailConfigBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_AT_CONFIG", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            }
        });
    }
}
