/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.service.impl;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageAuthBean;
import com.sunwayworld.cloud.module.qms.filemanage.persistent.dao.CoreFileManageAuthDao;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageAuthService;
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
public class CoreFileManageAuthServiceImpl
implements CoreFileManageAuthService {
    @Autowired
    private CoreFileManageAuthDao coreFileManageAuthDao;

    @Override
    public CoreFileManageAuthDao getDao() {
        return this.coreFileManageAuthDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileManageAuthBean coreFileManageAuth = jsonWrapper.parseUnique(CoreFileManageAuthBean.class);
        coreFileManageAuth.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreFileManageAuth);
        return coreFileManageAuth.getId();
    }
}
