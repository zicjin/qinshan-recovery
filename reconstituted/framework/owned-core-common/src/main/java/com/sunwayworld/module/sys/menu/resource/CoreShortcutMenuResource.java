/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreShortcutMenuService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/shortcut-menus"})
public interface CoreShortcutMenuResource
extends GenericResource<CoreShortcutMenuService, CoreShortcutMenuBean, Long> {
    public List<CoreShortcutMenuBean> selectCurrentUserAccessableList();
}
