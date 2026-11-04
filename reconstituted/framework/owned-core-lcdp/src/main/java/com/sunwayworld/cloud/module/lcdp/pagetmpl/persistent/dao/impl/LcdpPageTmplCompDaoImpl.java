/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplCompBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.LcdpPageTmplCompDao;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.mapper.LcdpPageTmplCompMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpPageTmplCompDaoImpl
extends MybatisDaoSupport<LcdpPageTmplCompBean, String>
implements LcdpPageTmplCompDao {
    @Autowired
    private LcdpPageTmplCompMapper lcdpPageTmplCompMapper;

    public LcdpPageTmplCompMapper getMapper() {
        return this.lcdpPageTmplCompMapper;
    }
}
