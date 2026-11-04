/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeDsDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeDsMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreOfficeDsDaoImpl
extends MybatisDaoSupport<CoreOfficeDsBean, Long>
implements CoreOfficeDsDao {
    @Autowired
    private CoreOfficeDsMapper coreOfficeDsMapper;

    public CoreOfficeDsMapper getMapper() {
        return this.coreOfficeDsMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_OFFICE_DS.ACTIVATABLE-TREE"}, key="'ACTIVATABLE'")
    public void cacheEvict(CoreOfficeDsBean oldItem, CoreOfficeDsBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_ELN_DS_FIELD_BY_ELN_DS_ID", "'ACTIVATABLE'");
            }
        });
    }
}
