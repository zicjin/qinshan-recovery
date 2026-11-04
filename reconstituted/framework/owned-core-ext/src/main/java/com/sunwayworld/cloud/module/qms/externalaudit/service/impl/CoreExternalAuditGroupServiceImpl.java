/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.service.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.CoreExternalAuditGroupDao;
import com.sunwayworld.cloud.module.qms.externalaudit.service.CoreExternalAuditGroupService;
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
public class CoreExternalAuditGroupServiceImpl
implements CoreExternalAuditGroupService {
    @Autowired
    private CoreExternalAuditGroupDao coreExternalAuditGroupDao;

    @Override
    public CoreExternalAuditGroupDao getDao() {
        return this.coreExternalAuditGroupDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreExternalAuditGroupBean coreExternalAuditGroup = jsonWrapper.parseUnique(CoreExternalAuditGroupBean.class);
        coreExternalAuditGroup.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreExternalAuditGroup);
        return coreExternalAuditGroup.getId();
    }
}
