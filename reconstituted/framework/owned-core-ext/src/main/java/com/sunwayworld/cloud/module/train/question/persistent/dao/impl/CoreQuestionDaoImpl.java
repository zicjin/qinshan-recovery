/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.persistent.dao.CoreQuestionDao;
import com.sunwayworld.cloud.module.train.question.persistent.mapper.CoreQuestionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreQuestionDaoImpl
extends MybatisDaoSupport<CoreQuestionBean, Long>
implements CoreQuestionDao {
    @Autowired
    private CoreQuestionMapper coreQuestionMapper;

    public CoreQuestionMapper getMapper() {
        return this.coreQuestionMapper;
    }
}
