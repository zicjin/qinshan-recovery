/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.admin.config.bean.CoreAdminSelectConfigBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;

public interface CoreAdminSelectConfigService
extends GenericService<CoreAdminSelectConfigBean, Long>,
GenericSelectableService<CoreAdminSelectConfigBean, Long> {
    public void insertByCodeCategory(CoreCodeCategoryBean var1);

    public void supplementSelectConfig();
}
