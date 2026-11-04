/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.persistent.dao.CoreExamDao;
import com.sunwayworld.cloud.module.train.exam.persistent.mapper.CoreExamMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreExamDaoImpl
extends MybatisDaoSupport<CoreExamBean, Long>
implements CoreExamDao {
    @Autowired
    private CoreExamMapper coreExamMapper;

    public CoreExamMapper getMapper() {
        return this.coreExamMapper;
    }
}
