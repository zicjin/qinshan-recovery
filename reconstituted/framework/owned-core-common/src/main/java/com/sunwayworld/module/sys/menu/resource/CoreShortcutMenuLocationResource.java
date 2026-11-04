/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuLocationBean;
import com.sunwayworld.module.sys.menu.service.CoreShortcutMenuLocationService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/sys/shortcut-menu-locations"})
public interface CoreShortcutMenuLocationResource
extends GenericResource<CoreShortcutMenuLocationService, CoreShortcutMenuLocationBean, String> {
    public void modifyLocation(Long var1);
}
