/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBpInstNodeUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBpInstNodeUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBpInstNodeUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBpInstNodeUserDaoImpl
extends MybatisDaoSupport<LcdpBpInstNodeUserBean, Long>
implements LcdpBpInstNodeUserDao {
    @Autowired
    private LcdpBpInstNodeUserMapper lcdpBpInstNodeUserMapper;

    public LcdpBpInstNodeUserMapper getMapper() {
        return this.lcdpBpInstNodeUserMapper;
    }
}
