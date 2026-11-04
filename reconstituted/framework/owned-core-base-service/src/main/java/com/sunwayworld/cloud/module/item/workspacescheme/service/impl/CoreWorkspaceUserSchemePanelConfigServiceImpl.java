/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.service.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemePanelConfigBean;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceUserSchemePanelConfigDao;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceUserSchemePanelConfigService;
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
public class CoreWorkspaceUserSchemePanelConfigServiceImpl
implements CoreWorkspaceUserSchemePanelConfigService {
    @Autowired
    private CoreWorkspaceUserSchemePanelConfigDao userSchemePanelConfigDao;

    @Override
    public CoreWorkspaceUserSchemePanelConfigDao getDao() {
        return this.userSchemePanelConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreWorkspaceUserSchemePanelConfigBean coreWorkspaceUserSchemePanelConf = jsonWrapper.parseUnique(CoreWorkspaceUserSchemePanelConfigBean.class);
        coreWorkspaceUserSchemePanelConf.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreWorkspaceUserSchemePanelConf);
        return coreWorkspaceUserSchemePanelConf.getId();
    }
}
