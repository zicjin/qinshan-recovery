/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.admin.config.bean.CoreAdminSelectConfigBean;
import com.sunwayworld.module.admin.config.service.CoreAdminSelectConfigService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/select-configs"})
public interface CoreAdminSelectConfigResource
extends GenericResource<CoreAdminSelectConfigService, CoreAdminSelectConfigBean, Long>,
GenericSelectableResource<CoreAdminSelectConfigService, CoreAdminSelectConfigBean, Long> {
}
