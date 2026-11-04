/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstNodeBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstNodeDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBpInstNodeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstNodeDaoImpl
extends MybatisDaoSupport<LcdpBpInstNodeBean, Long>
implements LcdpBpInstNodeDao {
    @Autowired
    private LcdpBpInstNodeMapper lcdpBpInstNodeMapper;

    public LcdpBpInstNodeMapper getMapper() {
        return this.lcdpBpInstNodeMapper;
    }
}
