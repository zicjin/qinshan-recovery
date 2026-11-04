/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.dao.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterBean;
import com.sunwayworld.module.admin.helpcenter.dao.CoreHelpCenterDao;
import com.sunwayworld.module.admin.helpcenter.mapper.CoreHelpCenterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreHelpCenterDaoImpl
extends MybatisDaoSupport<CoreHelpCenterBean, String>
implements CoreHelpCenterDao {
    @Autowired
    private CoreHelpCenterMapper coreHelpCenterMapper;

    public CoreHelpCenterMapper getMapper() {
        return this.coreHelpCenterMapper;
    }

    @Override
    @CacheEvict(value={"T_CORE_HELP_CENTER"}, key="'ALL'")
    public void cacheEvict(CoreHelpCenterBean oldItem, CoreHelpCenterBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            if (0 != i) {
                RedisHelper.evict("T_CORE_HELP_CENTER", "ALL");
            }
        });
    }
}
