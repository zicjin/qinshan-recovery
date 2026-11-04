/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacepanel.resource.impl;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacepanel.resource.CoreWorkspacePanelResource;
import com.sunwayworld.cloud.module.item.workspacepanel.service.CoreWorkspacePanelService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.suspendable.resource.AbstractGenericSuspendableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5de5\u4f5c\u533a\u9762\u677f")
@RestController
@GikamBean
public class CoreWorkspacePanelResourceImpl
implements CoreWorkspacePanelResource,
AbstractGenericResource<CoreWorkspacePanelService, CoreWorkspacePanelBean, String>,
AbstractGenericSuspendableResource<CoreWorkspacePanelService, CoreWorkspacePanelBean, String> {
    @Autowired
    private CoreWorkspacePanelService coreWorkspacePanelService;

    @Override
    public CoreWorkspacePanelService getService() {
        return this.coreWorkspacePanelService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5de5\u4f5c\u533a\u9762\u677f", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }
}
