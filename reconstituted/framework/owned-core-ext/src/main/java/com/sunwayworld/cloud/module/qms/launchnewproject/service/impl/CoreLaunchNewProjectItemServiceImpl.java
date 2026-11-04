/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.service.impl;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectItemBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.persistent.dao.CoreLaunchNewProjectItemDao;
import com.sunwayworld.cloud.module.qms.launchnewproject.service.CoreLaunchNewProjectItemService;
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
public class CoreLaunchNewProjectItemServiceImpl
implements CoreLaunchNewProjectItemService {
    @Autowired
    private CoreLaunchNewProjectItemDao coreLaunchNewProjectItemDao;

    @Override
    public CoreLaunchNewProjectItemDao getDao() {
        return this.coreLaunchNewProjectItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreLaunchNewProjectItemBean coreLaunchNewProjectItem = jsonWrapper.parseUnique(CoreLaunchNewProjectItemBean.class);
        coreLaunchNewProjectItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreLaunchNewProjectItem);
        return coreLaunchNewProjectItem.getId();
    }
}
