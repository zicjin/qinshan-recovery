/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.service;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserPanelLayoutRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemeBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreWorkspaceUserSchemeService
extends GenericService<CoreWorkspaceUserSchemeBean, Long> {
    public CoreWorkspaceSchemePanelConfigRepresentation selectUserPanelConfig();

    public CoreWorkspaceUserPanelLayoutRepresentation selectUserPanelLayout();

    public void save(RestJsonWrapperBean var1);

    public CoreWorkspaceSchemePanelConfigRepresentation selectPanelConfigBySchemeId(Long var1, RestJsonWrapperBean var2);
}
