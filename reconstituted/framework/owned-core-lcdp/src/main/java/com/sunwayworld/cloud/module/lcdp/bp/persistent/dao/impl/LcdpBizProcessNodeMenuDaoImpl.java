/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessNodeMenuBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessNodeMenuDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBizProcessNodeMenuMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessNodeMenuDaoImpl
extends MybatisDaoSupport<LcdpBizProcessNodeMenuBean, Long>
implements LcdpBizProcessNodeMenuDao {
    @Autowired
    private LcdpBizProcessNodeMenuMapper lcdpBizProcessNodeMenuMapper;

    public LcdpBizProcessNodeMenuMapper getMapper() {
        return this.lcdpBizProcessNodeMenuMapper;
    }
}
