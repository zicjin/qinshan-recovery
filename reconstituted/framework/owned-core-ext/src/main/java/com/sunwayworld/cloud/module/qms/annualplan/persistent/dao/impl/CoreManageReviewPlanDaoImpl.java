/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreManageReviewPlanBean;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.dao.CoreManageReviewPlanDao;
import com.sunwayworld.cloud.module.qms.annualplan.persistent.mapper.CoreManageReviewPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreManageReviewPlanDaoImpl
extends MybatisDaoSupport<CoreManageReviewPlanBean, Long>
implements CoreManageReviewPlanDao {
    @Autowired
    private CoreManageReviewPlanMapper coreManageReviewPlanMapper;

    public CoreManageReviewPlanMapper getMapper() {
        return this.coreManageReviewPlanMapper;
    }
}
