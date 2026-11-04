/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.service.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceSchemePanelConfigDao;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceSchemePanelConfigService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreWorkspaceSchemePanelConfigServiceImpl
implements CoreWorkspaceSchemePanelConfigService {
    @Autowired
    private CoreWorkspaceSchemePanelConfigDao schemePanelConfigDao;

    @Override
    public CoreWorkspaceSchemePanelConfigDao getDao() {
        return this.schemePanelConfigDao;
    }
}
