/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpModulePageCompDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpModulePageCompMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpModulePageCompDaoImpl
extends MybatisDaoSupport<LcdpModulePageCompBean, String>
implements LcdpModulePageCompDao {
    @Autowired
    private LcdpModulePageCompMapper lcdpModulePageCompMapper;

    public LcdpModulePageCompMapper getMapper() {
        return this.lcdpModulePageCompMapper;
    }
}
