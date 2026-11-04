/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.service.impl;

import com.sunwayworld.cloud.module.nfssi.order.bean.CoreNfssiVerifyReasonBean;
import com.sunwayworld.cloud.module.nfssi.order.persistent.dao.CoreNfssiVerifyReasonDao;
import com.sunwayworld.cloud.module.nfssi.order.service.CoreNfssiVerifyReasonService;
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
public class CoreNfssiVerifyReasonServiceImpl
implements CoreNfssiVerifyReasonService {
    @Autowired
    private CoreNfssiVerifyReasonDao coreNfssiVerifyReasonDao;

    @Override
    public CoreNfssiVerifyReasonDao getDao() {
        return this.coreNfssiVerifyReasonDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNfssiVerifyReasonBean coreNfssiVerifyReason = jsonWrapper.parseUnique(CoreNfssiVerifyReasonBean.class);
        coreNfssiVerifyReason.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreNfssiVerifyReason);
        return coreNfssiVerifyReason.getId();
    }
}
