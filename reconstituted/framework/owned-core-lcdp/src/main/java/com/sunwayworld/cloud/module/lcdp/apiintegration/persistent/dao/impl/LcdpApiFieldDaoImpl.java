/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiFieldBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiFieldDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.mapper.LcdpApiFieldMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpApiFieldDaoImpl
extends MybatisDaoSupport<LcdpApiFieldBean, Long>
implements LcdpApiFieldDao {
    @Autowired
    private LcdpApiFieldMapper lcdpApiFieldMapper;

    public LcdpApiFieldMapper getMapper() {
        return this.lcdpApiFieldMapper;
    }

    @Override
    @CacheEvict(value={"T_LCDP_API_FIELD"}, keyGenerator="lcdpApiFieldKeyGenerator")
    public void cacheEvict(LcdpApiFieldBean oldItem, LcdpApiFieldBean newItem) {
    }

    @Bean(value={"lcdpApiFieldKeyGenerator"})
    public KeyGenerator lcdpApiFieldKeyGenerator() {
        return (t, m, p) -> BeanUtils.getPropertyValue((LcdpApiFieldBean)p[0], (LcdpApiFieldBean)p[1], "apiId");
    }
}
