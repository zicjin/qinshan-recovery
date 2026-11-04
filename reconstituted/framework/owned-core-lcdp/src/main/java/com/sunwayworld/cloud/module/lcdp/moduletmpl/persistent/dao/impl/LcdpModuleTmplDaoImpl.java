/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.mapper.LcdpModuleTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpModuleTmplDaoImpl
extends MybatisDaoSupport<LcdpModuleTmplBean, Long>
implements LcdpModuleTmplDao {
    @Autowired
    private LcdpModuleTmplMapper lcdpModuleTmplMapper;

    public LcdpModuleTmplMapper getMapper() {
        return this.lcdpModuleTmplMapper;
    }
}
