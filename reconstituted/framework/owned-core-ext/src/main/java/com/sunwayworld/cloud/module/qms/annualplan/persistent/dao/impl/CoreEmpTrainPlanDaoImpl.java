/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEmpTrainPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreEmpTrainPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreEmpTrainPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEmpTrainPlanDaoImpl
extends MybatisDaoSupport<CoreEmpTrainPlanBean, Long>
implements CoreEmpTrainPlanDao {
    @Autowired
    private CoreEmpTrainPlanMapper coreEmpTrainPlanMapper;

    public CoreEmpTrainPlanMapper getMapper() {
        return this.coreEmpTrainPlanMapper;
    }
}
