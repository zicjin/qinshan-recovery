/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.service.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.CoreExternalAuditNcrDao;
import com.sunwayworld.cloud.module.qms.externalaudit.service.CoreExternalAuditNcrService;
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
public class CoreExternalAuditNcrServiceImpl
implements CoreExternalAuditNcrService {
    @Autowired
    private CoreExternalAuditNcrDao coreExternalAuditNcrDao;

    @Override
    public CoreExternalAuditNcrDao getDao() {
        return this.coreExternalAuditNcrDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreExternalAuditNcrBean coreExternalAuditNcr = jsonWrapper.parseUnique(CoreExternalAuditNcrBean.class);
        coreExternalAuditNcr.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreExternalAuditNcr);
        return coreExternalAuditNcr.getId();
    }
}
