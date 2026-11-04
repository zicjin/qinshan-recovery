/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.dao.CoreOrgDao;
import com.sunwayworld.module.mdm.org.mapper.CoreOrgMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreOrgDaoImpl
extends MybatisDaoSupport<CoreOrgBean, String>
implements CoreOrgDao {
    @Autowired
    private CoreOrgMapper orgMapper;

    public CoreOrgMapper getMapper() {
        return this.orgMapper;
    }

    @Override
    @Cacheable(value={"T_CORE_ORG"}, key="#id", unless="#result == null")
    public CoreOrgBean selectByIdIfPresent(String id) {
        return (CoreOrgBean)super.selectByIdIfPresent(id);
    }

    @Override
    public List<Map<String, Object>> selectRoleOrgList(Map<String, Object> parameter) {
        return this.getMapper().selectRoleOrgList(parameter);
    }

    @Override
    public List<CoreOrgBean> selectTreeNodeList(MapperParameter parameter) {
        return this.getMapper().selectTreeNodeList(parameter).stream().map(e -> PersistableHelper.mapToPersistable(e, CoreOrgBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<String> selectDescendantIdList(List<String> idList) {
        return this.getMapper().selectDescendantIdList(idList);
    }

    @Override
    @Caching(evict={@CacheEvict(value={"T_CORE_ORG"}, key="'ALL'"), @CacheEvict(value={"T_CORE_ORG"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")})
    public void cacheEvict(CoreOrgBean oldItem, CoreOrgBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ORG", "ALL");
                RedisHelper.evict("T_CORE_ORG", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            }
        });
    }
}
