/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.menu.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;
import com.sunwayworld.module.admin.menu.bean.CoreAdminMenuBean;
import com.sunwayworld.module.admin.menu.service.CoreAdminMenuService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/admin/menus"})
public interface CoreAdminMenuResource
extends GenericResource<CoreAdminMenuService, CoreAdminMenuBean, String>,
GenericTreeResource<CoreAdminMenuService, CoreAdminMenuBean, String> {
}
