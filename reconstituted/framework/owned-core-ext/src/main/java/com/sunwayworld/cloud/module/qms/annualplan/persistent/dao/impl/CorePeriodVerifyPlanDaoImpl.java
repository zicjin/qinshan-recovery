/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CorePeriodVerifyPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CorePeriodVerifyPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CorePeriodVerifyPlanDaoImpl
extends MybatisDaoSupport<CorePeriodVerifyPlanBean, Long>
implements CorePeriodVerifyPlanDao {
    @Autowired
    private CorePeriodVerifyPlanMapper corePeriodVerifyPlanMapper;

    public CorePeriodVerifyPlanMapper getMapper() {
        return this.corePeriodVerifyPlanMapper;
    }
}
