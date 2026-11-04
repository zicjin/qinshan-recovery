/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreAnnualPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreAnnualPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreAnnualPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAnnualPlanDaoImpl
extends MybatisDaoSupport<CoreAnnualPlanBean, Long>
implements CoreAnnualPlanDao {
    @Autowired
    private CoreAnnualPlanMapper coreAnnualPlanMapper;

    public CoreAnnualPlanMapper getMapper() {
        return this.coreAnnualPlanMapper;
    }
}
