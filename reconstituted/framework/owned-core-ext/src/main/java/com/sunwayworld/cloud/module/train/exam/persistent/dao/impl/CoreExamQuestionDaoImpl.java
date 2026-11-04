/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamQuestionBean;
import com.sunwayworld.cloud.module.train.exam.persistent.dao.CoreExamQuestionDao;
import com.sunwayworld.cloud.module.train.exam.persistent.mapper.CoreExamQuestionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreExamQuestionDaoImpl
extends MybatisDaoSupport<CoreExamQuestionBean, Long>
implements CoreExamQuestionDao {
    @Autowired
    private CoreExamQuestionMapper coreExamQuestionMapper;

    public CoreExamQuestionMapper getMapper() {
        return this.coreExamQuestionMapper;
    }
}
