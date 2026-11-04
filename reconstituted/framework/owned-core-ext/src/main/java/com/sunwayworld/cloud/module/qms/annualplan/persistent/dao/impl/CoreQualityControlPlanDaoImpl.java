/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreQualityControlPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreQualityControlPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreQualityControlPlanDaoImpl
extends MybatisDaoSupport<CoreQualityControlPlanBean, Long>
implements CoreQualityControlPlanDao {
    @Autowired
    private CoreQualityControlPlanMapper coreQualityControlPlanMapper;

    public CoreQualityControlPlanMapper getMapper() {
        return this.coreQualityControlPlanMapper;
    }
}
