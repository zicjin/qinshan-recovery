/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnDsDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnDsMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnDsDaoImpl
extends MybatisDaoSupport<CoreElnDsBean, Long>
implements CoreElnDsDao {
    @Autowired
    private CoreElnDsMapper coreElnDsMapper;

    public CoreElnDsMapper getMapper() {
        return this.coreElnDsMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_ELN_DS_FIELD_BY_ELN_DS_ID"}, keyGenerator="defaultDaoCacheEvictKeyGenerator")
    public void cacheEvict(CoreElnDsBean oldItem, CoreElnDsBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ELN_DS_FIELD_BY_ELN_DS_ID", (String)BeanUtils.getPropertyValue(oldItem, newItem, "id"));
            }
        });
    }
}
