/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.service;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.List;

public interface CoreWorkspaceSchemeService
extends GenericService<CoreWorkspaceSchemeBean, Long>,
GenericChoosableService<CoreWorkspaceSchemeBean, Long> {
    public CoreWorkspaceSchemePanelConfigRepresentation selectSchemePanelConfigById(Long var1);

    public List<CoreWorkspaceSchemeBean> selectAuthorizedList();

    public void savePanelConfig(Long var1, RestJsonWrapperBean var2);
}
