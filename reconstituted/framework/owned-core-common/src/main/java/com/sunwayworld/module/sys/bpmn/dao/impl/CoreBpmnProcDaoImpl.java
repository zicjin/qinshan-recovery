/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnProcDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnProcMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnProcDaoImpl
extends MybatisDaoSupport<CoreBpmnProcBean, Long>
implements CoreBpmnProcDao {
    @Autowired
    private CoreBpmnProcMapper procMapper;

    public CoreBpmnProcMapper getMapper() {
        return this.procMapper;
    }

    @Override
    public List<CoreBpmnProcBean> selectRuntimeBpmnProcList(String tableName, String usedOrgId) {
        List<Map<String, Object>> mapList = this.getMapper().selectRuntimeBpmnProcList(tableName, usedOrgId);
        if (mapList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnProcBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<CoreBpmnProcBean> selectBpmnProcListByServiceId(String serviceId) {
        List<Map<String, Object>> mapList = this.getMapper().selectBpmnProcListByServiceId(serviceId);
        if (mapList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreBpmnProcBean.class)).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value={"T_CORE_BPMN_PROC"}, key="#id", unless="#result == null")
    public CoreBpmnProcBean selectByIdIfPresent(Long id) {
        return (CoreBpmnProcBean)super.selectByIdIfPresent(id);
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_BPMN_PROC"}, keyGenerator="defaultDaoCacheEvictKeyGenerator"), @CacheEvict(value={"T_CORE_BPMN_PROC_LATEST.BY_PROCCODE"}, keyGenerator="latestByProcCodeKeyGenerator")})
    public void cacheEvict(CoreBpmnProcBean oldItem, CoreBpmnProcBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_BPMN_PROC", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class));
                RedisHelper.evict("T_CORE_BPMN_PROC_LATEST.BY_PROCCODE", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "procCode"), String.class));
            }
        });
    }

    @Bean(value={"latestByProcCodeKeyGenerator"})
    public KeyGenerator latestByProcCodeKeyGenerator() {
        return (t, m, p) -> BeanUtils.getPropertyValue((CoreBpmnProcBean)p[0], (CoreBpmnProcBean)p[1], "procCode");
    }
}
