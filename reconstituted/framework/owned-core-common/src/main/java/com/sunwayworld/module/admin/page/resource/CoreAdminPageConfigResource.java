/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.resource;

import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigBean;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/page-configs"})
public interface CoreAdminPageConfigResource
extends GenericResource<CoreAdminPageConfigService, CoreAdminPageConfigBean, Long>,
GenericActivatableResource<CoreAdminPageConfigService, CoreAdminPageConfigBean, Long> {
}
