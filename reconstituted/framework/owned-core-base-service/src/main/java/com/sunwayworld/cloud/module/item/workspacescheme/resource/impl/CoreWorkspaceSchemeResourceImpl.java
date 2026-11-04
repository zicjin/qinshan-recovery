/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.resource.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.resource.CoreWorkspaceSchemeResource;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceSchemeService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9996\u9875\u65b9\u6848\u914d\u7f6e")
@RestController
@GikamBean
public class CoreWorkspaceSchemeResourceImpl
implements CoreWorkspaceSchemeResource,
AbstractGenericResource<CoreWorkspaceSchemeService, CoreWorkspaceSchemeBean, Long>,
AbstractGenericChoosableResource<CoreWorkspaceSchemeService, CoreWorkspaceSchemeBean, Long> {
    @Autowired
    private CoreWorkspaceSchemeService schemeService;

    @Override
    public CoreWorkspaceSchemeService getService() {
        return this.schemeService;
    }

    @Override
    @Log(value="\u65b0\u589e\u9996\u9875\u65b9\u6848\u914d\u7f6e", type=LogType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u65b9\u6848\u9762\u677f\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    public CoreWorkspaceSchemePanelConfigRepresentation selectSchemePanelConfigById(@PathVariable Long id) {
        return this.getService().selectSchemePanelConfigById(id);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u65b9\u6848\u9762\u677f\u914d\u7f6e\u4fe1\u606f", type=LogType.INSERT)
    public void savePanelConfig(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().savePanelConfig(id, wrapper);
    }
}
