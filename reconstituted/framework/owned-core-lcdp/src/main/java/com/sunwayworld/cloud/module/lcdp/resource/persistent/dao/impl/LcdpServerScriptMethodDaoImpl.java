/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpServerScriptMethodBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpServerScriptMethodDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpServerScriptMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpServerScriptMethodDaoImpl
extends MybatisDaoSupport<LcdpServerScriptMethodBean, Long>
implements LcdpServerScriptMethodDao {
    @Autowired
    private LcdpServerScriptMethodMapper lcdpServerScriptMethodMapper;

    public LcdpServerScriptMethodMapper getMapper() {
        return this.lcdpServerScriptMethodMapper;
    }
}
