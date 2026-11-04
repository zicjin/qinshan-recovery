/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeUserBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeUserDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBizProcessNodeUserMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeUserDaoImpl
extends MybatisDaoSupport<LcdpBizProcessNodeUserBean, Long>
implements LcdpBizProcessNodeUserDao {
    @Autowired
    private LcdpBizProcessNodeUserMapper lcdpBizProcessNodeUserMapper;

    public LcdpBizProcessNodeUserMapper getMapper() {
        return this.lcdpBizProcessNodeUserMapper;
    }
}
