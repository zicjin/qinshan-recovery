/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.service.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.CoreInternalAuditNcrDao;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditNcrService;
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
public class CoreInternalAuditNcrServiceImpl
implements CoreInternalAuditNcrService {
    @Autowired
    private CoreInternalAuditNcrDao coreInternalAuditNcrDao;

    @Override
    public CoreInternalAuditNcrDao getDao() {
        return this.coreInternalAuditNcrDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreInternalAuditNcrBean coreInternalAuditNcr = jsonWrapper.parseUnique(CoreInternalAuditNcrBean.class);
        coreInternalAuditNcr.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreInternalAuditNcr);
        return coreInternalAuditNcr.getId();
    }
}
