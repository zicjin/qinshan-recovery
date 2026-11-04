/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnDiagramDao;
import com.sunwayworld.module.sys.bpmn.mapper.CoreBpmnDiagramMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreBpmnDiagramDaoImpl
extends MybatisDaoSupport<CoreBpmnDiagramBean, Long>
implements CoreBpmnDiagramDao {
    @Autowired
    private CoreBpmnDiagramMapper diagramMapper;

    public CoreBpmnDiagramMapper getMapper() {
        return this.diagramMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_BPMN_DIAGRAM"}, key="'' + #id", unless="#result == null")
    public CoreBpmnDiagramBean selectByIdIfPresent(Long id) {
        return (CoreBpmnDiagramBean)super.selectByIdIfPresent(id);
    }

    @Override
    @CacheEvict(value={"T_CORE_BPMN_DIAGRAM", "T_CORE_BPMN_DIAGRAM.DOCUMENT", "T_CORE_BPMN_DIAGRAM.TASKCANDIDATOR", "T_CORE_BPMN_DIAGRAM.SEQUENCEFLOWCONDITION"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreBpmnDiagramBean oldItem, CoreBpmnDiagramBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                String redisKey = ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "ID"), String.class);
                RedisHelper.evict("T_CORE_BPMN_DIAGRAM", redisKey);
                RedisHelper.evict("T_CORE_BPMN_DIAGRAM.DOCUMENT", redisKey);
                RedisHelper.evict("T_CORE_BPMN_DIAGRAM.TASKCANDIDATOR", redisKey);
            }
        });
    }
}
