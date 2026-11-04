/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.service.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiTestReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiTestReasonDao;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiTestReasonService;
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
public class CoreNfssiTestReasonServiceImpl
implements CoreNfssiTestReasonService {
    @Autowired
    private CoreNfssiTestReasonDao coreNfssiTestReasonDao;

    @Override
    public CoreNfssiTestReasonDao getDao() {
        return this.coreNfssiTestReasonDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNfssiTestReasonBean coreNfssiTestReason = jsonWrapper.parseUnique(CoreNfssiTestReasonBean.class);
        coreNfssiTestReason.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreNfssiTestReason);
        return coreNfssiTestReason.getId();
    }
}
