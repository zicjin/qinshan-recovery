/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBizProcessNodeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeDaoImpl
extends MybatisDaoSupport<LcdpBizProcessNodeBean, Long>
implements LcdpBizProcessNodeDao {
    @Autowired
    private LcdpBizProcessNodeMapper lcdpBizProcessNodeMapper;

    public LcdpBizProcessNodeMapper getMapper() {
        return this.lcdpBizProcessNodeMapper;
    }
}
