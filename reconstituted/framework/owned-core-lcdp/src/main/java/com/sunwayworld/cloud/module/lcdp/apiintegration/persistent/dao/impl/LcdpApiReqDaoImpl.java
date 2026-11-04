/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiReqBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiReqDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.mapper.LcdpApiReqMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpApiReqDaoImpl
extends MybatisDaoSupport<LcdpApiReqBean, Long>
implements LcdpApiReqDao {
    @Autowired
    private LcdpApiReqMapper lcdpApiReqMapper;

    public LcdpApiReqMapper getMapper() {
        return this.lcdpApiReqMapper;
    }
}
