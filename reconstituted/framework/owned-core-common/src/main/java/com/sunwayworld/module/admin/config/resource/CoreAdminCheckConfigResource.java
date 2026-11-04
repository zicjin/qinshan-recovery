/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.admin.config.bean.CoreAdminCheckConfigBean;
import com.sunwayworld.module.admin.config.service.CoreAdminCheckConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/check-configs"})
public interface CoreAdminCheckConfigResource
extends GenericResource<CoreAdminCheckConfigService, CoreAdminCheckConfigBean, String>,
GenericSelectableResource<CoreAdminCheckConfigService, CoreAdminCheckConfigBean, String> {
}
