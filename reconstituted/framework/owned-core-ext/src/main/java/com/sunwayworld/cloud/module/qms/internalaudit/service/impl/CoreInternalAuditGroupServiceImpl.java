/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.service.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.CoreInternalAuditGroupDao;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditGroupService;
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
public class CoreInternalAuditGroupServiceImpl
implements CoreInternalAuditGroupService {
    @Autowired
    private CoreInternalAuditGroupDao coreInternalAuditGroupDao;

    @Override
    public CoreInternalAuditGroupDao getDao() {
        return this.coreInternalAuditGroupDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreInternalAuditGroupBean coreInternalAuditGroup = jsonWrapper.parseUnique(CoreInternalAuditGroupBean.class);
        coreInternalAuditGroup.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreInternalAuditGroup);
        return coreInternalAuditGroup.getId();
    }
}
