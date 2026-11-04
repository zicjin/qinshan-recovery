/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.service.impl;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.cloud.module.qms.managereview.persistent.dao.CoreManageReviewImproveDao;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewImproveService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreManageReviewImproveServiceImpl
implements CoreManageReviewImproveService {
    @Autowired
    private CoreManageReviewImproveDao coreManageReviewImproveDao;

    @Override
    public CoreManageReviewImproveDao getDao() {
        return this.coreManageReviewImproveDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreManageReviewImproveBean coreManageReviewImprove = jsonWrapper.parseUnique(CoreManageReviewImproveBean.class);
        coreManageReviewImprove.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreManageReviewImprove);
        return coreManageReviewImprove.getId();
    }
}
