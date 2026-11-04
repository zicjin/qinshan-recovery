/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.resource;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserPanelLayoutRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemeBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/workspace-user-schemes"})
public interface CoreWorkspaceUserSchemeResource
extends GenericCloudResource<CoreWorkspaceUserSchemeBean, Long> {
    @RequestMapping(method={RequestMethod.GET})
    public CoreWorkspaceSchemePanelConfigRepresentation selectUserPanelConfig();

    @RequestMapping(value={"/layouts"}, method={RequestMethod.GET})
    public CoreWorkspaceUserPanelLayoutRepresentation selectUserPanelLayout();

    @RequestMapping(method={RequestMethod.PUT})
    public void save(RestJsonWrapperBean var1);

    @RequestMapping(value={"/schemes/{schemeId}/panel-configs"}, method={RequestMethod.GET})
    public CoreWorkspaceSchemePanelConfigRepresentation selectPanelConfigBySchemeId(@PathVariable Long var1, RestJsonWrapperBean var2);
}
