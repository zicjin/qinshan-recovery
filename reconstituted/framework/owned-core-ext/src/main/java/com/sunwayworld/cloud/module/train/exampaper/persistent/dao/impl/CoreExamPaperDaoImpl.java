/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.persistent.dao.impl;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperBean;
import com.sunwayworld.cloud.module.train.exampaper.persistent.dao.CoreExamPaperDao;
import com.sunwayworld.cloud.module.train.exampaper.persistent.mapper.CoreExamPaperMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreExamPaperDaoImpl
extends MybatisDaoSupport<CoreExamPaperBean, Long>
implements CoreExamPaperDao {
    @Autowired
    private CoreExamPaperMapper coreExamPaperMapper;

    public CoreExamPaperMapper getMapper() {
        return this.coreExamPaperMapper;
    }
}
