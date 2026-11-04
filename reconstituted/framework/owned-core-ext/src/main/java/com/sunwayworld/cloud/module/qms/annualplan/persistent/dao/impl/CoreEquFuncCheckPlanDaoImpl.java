/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreEquFuncCheckPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreEquFuncCheckPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquFuncCheckPlanDaoImpl
extends MybatisDaoSupport<CoreEquFuncCheckPlanBean, Long>
implements CoreEquFuncCheckPlanDao {
    @Autowired
    private CoreEquFuncCheckPlanMapper coreEquFuncCheckPlanMapper;

    public CoreEquFuncCheckPlanMapper getMapper() {
        return this.coreEquFuncCheckPlanMapper;
    }
}
