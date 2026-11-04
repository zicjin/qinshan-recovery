/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.mapper.LcdpApiMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpApiDaoImpl
extends MybatisDaoSupport<LcdpApiBean, Long>
implements LcdpApiDao {
    @Autowired
    private LcdpApiMapper lcdpApiMapper;

    public LcdpApiMapper getMapper() {
        return this.lcdpApiMapper;
    }

    @Override
    public void cacheEvict(LcdpApiBean oldItem, LcdpApiBean newItem) {
        TransactionUtils.runAfterCompletion(i -> {
            RedisHelper.evict("T_LCDP_API", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "apiCode"), String.class));
            RedisHelper.evict("T_LCDP_API_URL", ConvertUtils.convert(BeanUtils.getPropertyValue(oldItem, newItem, "apiUrl"), String.class));
        });
    }
}
