/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuLocationBean;

public interface CoreShortcutMenuLocationService
extends GenericService<CoreShortcutMenuLocationBean, String> {
    public void modifyLocation(Long var1);
}
