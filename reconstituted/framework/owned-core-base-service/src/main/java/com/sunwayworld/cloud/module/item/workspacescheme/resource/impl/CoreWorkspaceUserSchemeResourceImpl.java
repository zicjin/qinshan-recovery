/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.resource.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserPanelLayoutRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.resource.CoreWorkspaceUserSchemeResource;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceUserSchemeService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7528\u6237\u9996\u9875\u65b9\u6848")
@RestController
@GikamBean
public class CoreWorkspaceUserSchemeResourceImpl
implements CoreWorkspaceUserSchemeResource,
AbstractGenericResource<CoreWorkspaceUserSchemeService, CoreWorkspaceUserSchemeBean, Long> {
    @Autowired
    private CoreWorkspaceUserSchemeService userSchemeService;

    @Override
    public CoreWorkspaceUserSchemeService getService() {
        return this.userSchemeService;
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u767b\u5f55\u7528\u6237\u65b9\u6848\u9762\u677f\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    public CoreWorkspaceSchemePanelConfigRepresentation selectUserPanelConfig() {
        return this.getService().selectUserPanelConfig();
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u767b\u5f55\u7528\u6237\u65b9\u6848\u9762\u677f\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    public CoreWorkspaceSchemePanelConfigRepresentation selectPanelConfigBySchemeId(@PathVariable Long schemeId, RestJsonWrapperBean wrapper) {
        return this.getService().selectPanelConfigBySchemeId(schemeId, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u7528\u6237\u767b\u5f55\u9762\u677f\u4fe1\u606f", type=LogType.SELECT)
    public CoreWorkspaceUserPanelLayoutRepresentation selectUserPanelLayout() {
        return this.getService().selectUserPanelLayout();
    }

    @Override
    @Log(value="\u4fdd\u5b58\u5f53\u524d\u7528\u6237\u767b\u5f55\u9762\u677f\u914d\u7f6e\u4fe1\u606f", type=LogType.UPDATE)
    public void save(RestJsonWrapperBean wrapper) {
        this.getService().save(wrapper);
    }
}
