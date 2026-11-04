/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreDailySupervisionPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreDailySupervisionPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreDailySupervisionPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreDailySupervisionPlanDaoImpl
extends MybatisDaoSupport<CoreDailySupervisionPlanBean, Long>
implements CoreDailySupervisionPlanDao {
    @Autowired
    private CoreDailySupervisionPlanMapper coreDailySupervisionPlanMapper;

    public CoreDailySupervisionPlanMapper getMapper() {
        return this.coreDailySupervisionPlanMapper;
    }
}
