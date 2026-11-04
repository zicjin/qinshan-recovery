/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.sys.menu.bean.CoreMenuExtBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuExtService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/menu-exts"})
public interface CoreMenuExtResource
extends GenericResource<CoreMenuExtService, CoreMenuExtBean, String>,
GenericChoosableResource<CoreMenuExtService, CoreMenuExtBean, String>,
GenericSelectableResource<CoreMenuExtService, CoreMenuExtBean, String> {
}
