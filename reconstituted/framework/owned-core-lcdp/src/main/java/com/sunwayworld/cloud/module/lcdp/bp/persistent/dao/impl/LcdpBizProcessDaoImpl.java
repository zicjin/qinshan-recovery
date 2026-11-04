/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBizProcessMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessDaoImpl
extends MybatisDaoSupport<LcdpBizProcessBean, Long>
implements LcdpBizProcessDao {
    @Autowired
    private LcdpBizProcessMapper lcdpBizProcessMapper;

    public LcdpBizProcessMapper getMapper() {
        return this.lcdpBizProcessMapper;
    }
}
