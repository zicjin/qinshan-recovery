/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpPageI18nCodeBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpPageI18nCodeDao;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.mapper.LcdpPageI18nCodeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpPageI18nCodeDaoImpl
extends MybatisDaoSupport<LcdpPageI18nCodeBean, Long>
implements LcdpPageI18nCodeDao {
    @Autowired
    private LcdpPageI18nCodeMapper lcdpPageI18nCodeMapper;

    public LcdpPageI18nCodeMapper getMapper() {
        return this.lcdpPageI18nCodeMapper;
    }
}
