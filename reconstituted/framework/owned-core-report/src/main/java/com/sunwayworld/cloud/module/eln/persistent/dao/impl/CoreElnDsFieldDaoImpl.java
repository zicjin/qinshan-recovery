/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.persistent.dao.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnDsFieldDao;
import com.sunwayworld.cloud.module.eln.persistent.mapper.CoreElnDsFieldMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreElnDsFieldDaoImpl
extends MybatisDaoSupport<CoreElnDsFieldBean, Long>
implements CoreElnDsFieldDao {
    @Autowired
    private CoreElnDsFieldMapper coreElnDsFieldMapper;

    public CoreElnDsFieldMapper getMapper() {
        return this.coreElnDsFieldMapper;
    }

    @Override
    public void cacheEvict(CoreElnDsFieldBean oldItem, CoreElnDsFieldBean newItem) {
        if (oldItem != null) {
            if (oldItem.getElnDsId() != null) {
                RedisHelper.evict("T_CORE_ELN_DS_FIELD_BY_ELN_DS_ID", oldItem.getElnDsId().toString());
            }
        } else if (newItem != null && newItem.getElnDsId() != null) {
            RedisHelper.evict("T_CORE_ELN_DS_FIELD_BY_ELN_DS_ID", newItem.getElnDsId().toString());
        }
    }
}
