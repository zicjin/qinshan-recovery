/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeDsFieldDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeDsFieldMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreOfficeDsFieldDaoImpl
extends MybatisDaoSupport<CoreOfficeDsFieldBean, Long>
implements CoreOfficeDsFieldDao {
    @Autowired
    private CoreOfficeDsFieldMapper coreOfficeDsFieldMapper;

    public CoreOfficeDsFieldMapper getMapper() {
        return this.coreOfficeDsFieldMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_OFFICE_DS.ACTIVATABLE-TREE"}, key="'ACTIVATABLE'")
    public void cacheEvict(CoreOfficeDsFieldBean oldItem, CoreOfficeDsFieldBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_OFFICE_DS.ACTIVATABLE-TREE", "'ACTIVATABLE'");
            }
        });
    }
}
