/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacepanel.resource;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/workspace-panels"})
public interface CoreWorkspacePanelResource
extends GenericCloudResource<CoreWorkspacePanelBean, String>,
GenericSuspendableCloudResource<CoreWorkspacePanelBean, String> {
}
