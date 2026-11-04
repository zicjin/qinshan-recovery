/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerBean;
import com.sunwayworld.cloud.module.train.exampaper.persistent.dao.CoreExamPaperAnswerDao;
import com.sunwayworld.cloud.module.train.exampaper.persistent.mapper.CoreExamPaperAnswerMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreExamPaperAnswerDaoImpl
extends MybatisDaoSupport<CoreExamPaperAnswerBean, Long>
implements CoreExamPaperAnswerDao {
    @Autowired
    private CoreExamPaperAnswerMapper coreExamPaperAnswerMapper;

    public CoreExamPaperAnswerMapper getMapper() {
        return this.coreExamPaperAnswerMapper;
    }
}
