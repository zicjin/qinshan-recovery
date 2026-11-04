/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptsource.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.scriptsource.bean.LcdpScriptSourceBean;
import com.sunwayworld.cloud.module.lcdp.scriptsource.persistent.dao.LcdpScriptSourceDao;
import com.sunwayworld.cloud.module.lcdp.scriptsource.persistent.mapper.LcdpScriptSourceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpScriptSourceDaoImpl
extends MybatisDaoSupport<LcdpScriptSourceBean, Long>
implements LcdpScriptSourceDao {
    @Autowired
    private LcdpScriptSourceMapper lcdpScriptSourceMapper;

    public LcdpScriptSourceMapper getMapper() {
        return this.lcdpScriptSourceMapper;
    }
}
