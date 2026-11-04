/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuBean;
import java.util.List;

public interface CoreShortcutMenuService
extends GenericService<CoreShortcutMenuBean, Long> {
    public List<CoreShortcutMenuBean> selectCurrentUserAccessableList();

    public List<CoreShortcutMenuBean> selectListByUserId(String var1);

    public void moveMenu(Long var1, int var2, int var3);
}
