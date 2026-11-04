/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplPageCompBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpModuleTmplPageCompDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.mapper.LcdpModuleTmplPageCompMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpModuleTmplPageCompDaoImpl
extends MybatisDaoSupport<LcdpModuleTmplPageCompBean, String>
implements LcdpModuleTmplPageCompDao {
    @Autowired
    private LcdpModuleTmplPageCompMapper lcdpModuleTmplPageCompMapper;

    public LcdpModuleTmplPageCompMapper getMapper() {
        return this.lcdpModuleTmplPageCompMapper;
    }
}
