/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuExtBean;
import java.util.List;

public interface CoreMenuExtService
extends GenericService<CoreMenuExtBean, String>,
GenericChoosableService<CoreMenuExtBean, String>,
GenericSelectableService<CoreMenuExtBean, String> {
    public List<CoreMenuExtBean> selectCurrentUserMenuExtList();
}
