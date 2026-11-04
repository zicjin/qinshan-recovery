/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.service.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageVersionDao;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageVersionService;
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
public class CoreFileManageVersionServiceImpl
implements CoreFileManageVersionService {
    @Autowired
    private CoreFileManageVersionDao coreFileManageVersionDao;

    @Override
    public CoreFileManageVersionDao getDao() {
        return this.coreFileManageVersionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileManageVersionBean coreFileManageVersion = jsonWrapper.parseUnique(CoreFileManageVersionBean.class);
        coreFileManageVersion.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreFileManageVersion);
        return coreFileManageVersion.getId();
    }
}
