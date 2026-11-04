/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.pagetmpl.bean.LcdpPageTmplBean;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.dao.LcdpPageTmplDao;
import com.sunwayworld.cloud.module.lcdp.pagetmpl.persistent.mapper.LcdpPageTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpPageTmplDaoImpl
extends MybatisDaoSupport<LcdpPageTmplBean, Long>
implements LcdpPageTmplDao {
    @Autowired
    private LcdpPageTmplMapper lcdpPageTmplMapper;

    public LcdpPageTmplMapper getMapper() {
        return this.lcdpPageTmplMapper;
    }
}
