/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptblock.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockBean;
import com.sunwayworld.cloud.module.lcdp.scriptblock.persistent.dao.LcdpScriptBlockDao;
import com.sunwayworld.cloud.module.lcdp.scriptblock.persistent.mapper.LcdpScriptBlockMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpScriptBlockDaoImpl
extends MybatisDaoSupport<LcdpScriptBlockBean, Long>
implements LcdpScriptBlockDao {
    @Autowired
    private LcdpScriptBlockMapper lcdpScriptBlockMapper;

    public LcdpScriptBlockMapper getMapper() {
        return this.lcdpScriptBlockMapper;
    }
}
