/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreInternalAuditPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreInternalAuditPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreInternalAuditPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreInternalAuditPlanDaoImpl
extends MybatisDaoSupport<CoreInternalAuditPlanBean, Long>
implements CoreInternalAuditPlanDao {
    @Autowired
    private CoreInternalAuditPlanMapper coreInternalAuditPlanMapper;

    public CoreInternalAuditPlanMapper getMapper() {
        return this.coreInternalAuditPlanMapper;
    }
}
