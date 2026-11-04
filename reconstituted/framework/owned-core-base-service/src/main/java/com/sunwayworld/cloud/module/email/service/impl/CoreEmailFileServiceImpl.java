/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailFileBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailFileDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailFileService;
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
public class CoreEmailFileServiceImpl
implements CoreEmailFileService {
    @Autowired
    private CoreEmailFileDao coreEmailFileDao;

    @Override
    public CoreEmailFileDao getDao() {
        return this.coreEmailFileDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreEmailFileBean coreEmailFile = jsonWrapper.parseUnique(CoreEmailFileBean.class);
        coreEmailFile.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreEmailFile);
        return coreEmailFile.getId();
    }
}
