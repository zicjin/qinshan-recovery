/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplConfigBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplConfigDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.mapper.LcdpModuleTmplConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpModuleTmplConfigDaoImpl
extends MybatisDaoSupport<LcdpModuleTmplConfigBean, Long>
implements LcdpModuleTmplConfigDao {
    @Autowired
    private LcdpModuleTmplConfigMapper lcdpModuleTmplConfigMapper;

    public LcdpModuleTmplConfigMapper getMapper() {
        return this.lcdpModuleTmplConfigMapper;
    }
}
