/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.bp.bean.LcdpBizProcessCommentBean;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessCommentDao;
import com.sunwayworld.cloud.module.lcdp.bp.persistent.mapper.LcdpBizProcessCommentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessCommentDaoImpl
extends MybatisDaoSupport<LcdpBizProcessCommentBean, Long>
implements LcdpBizProcessCommentDao {
    @Autowired
    private LcdpBizProcessCommentMapper lcdpBizProcessCommentMapper;

    public LcdpBizProcessCommentMapper getMapper() {
        return this.lcdpBizProcessCommentMapper;
    }
}
