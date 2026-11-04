/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpCustomTmplResourceBean;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.dao.LcdpCustomTmplResourceDao;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.persistent.mapper.LcdpCustomTmplResourceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpCustomTmplResourceDaoImpl
extends MybatisDaoSupport<LcdpCustomTmplResourceBean, Long>
implements LcdpCustomTmplResourceDao {
    @Autowired
    private LcdpCustomTmplResourceMapper lcdpCustomTmplResourceMapper;

    public LcdpCustomTmplResourceMapper getMapper() {
        return this.lcdpCustomTmplResourceMapper;
    }
}
