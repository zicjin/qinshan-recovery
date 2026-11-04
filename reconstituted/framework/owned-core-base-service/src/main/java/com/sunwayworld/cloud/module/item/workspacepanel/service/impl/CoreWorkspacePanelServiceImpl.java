/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacepanel.service.impl;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacepanel.persistent.dao.CoreWorkspacePanelDao;
import com.sunwayworld.cloud.module.item.workspacepanel.service.CoreWorkspacePanelService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreWorkspacePanelServiceImpl
implements CoreWorkspacePanelService {
    @Autowired
    private CoreWorkspacePanelDao panelDao;

    @Override
    public CoreWorkspacePanelDao getDao() {
        return this.panelDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreWorkspacePanelBean workspacePanel = wrapper.parseUnique(CoreWorkspacePanelBean.class);
        workspacePanel.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        workspacePanel.setAuthorityControl("0");
        this.getDao().insert(workspacePanel);
        return workspacePanel.getId();
    }
}
