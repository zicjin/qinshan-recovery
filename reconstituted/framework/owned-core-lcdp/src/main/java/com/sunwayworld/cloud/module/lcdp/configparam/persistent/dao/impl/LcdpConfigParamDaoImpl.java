/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.configparam.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.configparam.bean.LcdpConfigParamBean;
import com.sunwayworld.cloud.module.lcdp.configparam.persistent.dao.LcdpConfigParamDao;
import com.sunwayworld.cloud.module.lcdp.configparam.persistent.mapper.LcdpConfigParamMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpConfigParamDaoImpl
extends MybatisDaoSupport<LcdpConfigParamBean, Long>
implements LcdpConfigParamDao {
    @Autowired
    private LcdpConfigParamMapper lcdpConfigParamMapper;

    public LcdpConfigParamMapper getMapper() {
        return this.lcdpConfigParamMapper;
    }
}
