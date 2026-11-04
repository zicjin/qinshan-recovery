/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.examtmpl.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.examtmpl.bean.CoreExamTmplBean;
import com.sunwayworld.cloud.module.train.examtmpl.persistent.dao.CoreExamTmplDao;
import com.sunwayworld.cloud.module.train.examtmpl.persistent.mapper.CoreExamTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreExamTmplDaoImpl
extends MybatisDaoSupport<CoreExamTmplBean, Long>
implements CoreExamTmplDao {
    @Autowired
    private CoreExamTmplMapper coreExamTmplMapper;

    public CoreExamTmplMapper getMapper() {
        return this.coreExamTmplMapper;
    }
}
