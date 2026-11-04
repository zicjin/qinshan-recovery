/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.resource;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/workspace-schemes"})
public interface CoreWorkspaceSchemeResource
extends GenericCloudResource<CoreWorkspaceSchemeBean, Long>,
GenericChoosableCloudResource<CoreWorkspaceSchemeBean, Long> {
    @RequestMapping(value={"/{id}/panel-configs"}, method={RequestMethod.GET})
    public CoreWorkspaceSchemePanelConfigRepresentation selectSchemePanelConfigById(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/panel-configs"}, method={RequestMethod.PUT})
    public void savePanelConfig(@PathVariable Long var1, RestJsonWrapperBean var2);
}
