/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.cloud.module.train.question.persistent.dao.CoreQuestionOptDao;
import com.sunwayworld.cloud.module.train.question.persistent.mapper.CoreQuestionOptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreQuestionOptDaompl
extends MybatisDaoSupport<CoreQuestionOptBean, Long>
implements CoreQuestionOptDao {
    @Autowired
    private CoreQuestionOptMapper coreQuestionOptMapper;

    public CoreQuestionOptMapper getMapper() {
        return this.coreQuestionOptMapper;
    }
}
