/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBpInstMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstDaoImpl
extends MybatisDaoSupport<LcdpBpInstBean, Long>
implements LcdpBpInstDao {
    @Autowired
    private LcdpBpInstMapper lcdpBpInstMapper;

    public LcdpBpInstMapper getMapper() {
        return this.lcdpBpInstMapper;
    }
}
