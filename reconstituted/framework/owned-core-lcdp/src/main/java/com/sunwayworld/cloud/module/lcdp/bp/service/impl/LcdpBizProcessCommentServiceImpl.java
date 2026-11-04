/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.service.impl;

import com.sunwayworld.cloud.module.lcdp.bp.persistent.dao.LcdpBizProcessCommentDao;
import com.sunwayworld.cloud.module.lcdp.bp.service.LcdpBizProcessCommentService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBizProcessCommentServiceImpl
implements LcdpBizProcessCommentService {
    @Autowired
    private LcdpBizProcessCommentDao lcdpBizProcessCommentDao;

    @Override
    public LcdpBizProcessCommentDao getDao() {
        return this.lcdpBizProcessCommentDao;
    }
}
