/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.admin.config.bean.CoreAdminChooseConfigBean;
import com.sunwayworld.module.admin.config.service.CoreAdminChooseConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/choose-configs"})
public interface CoreAdminChooseConfigResource
extends GenericResource<CoreAdminChooseConfigService, CoreAdminChooseConfigBean, Long>,
GenericSelectableResource<CoreAdminChooseConfigService, CoreAdminChooseConfigBean, Long> {
}
