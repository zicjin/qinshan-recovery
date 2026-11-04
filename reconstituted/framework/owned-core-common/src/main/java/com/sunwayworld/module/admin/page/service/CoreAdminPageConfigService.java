/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.service;

import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigLineBean;
import java.util.List;
import org.springframework.lang.Nullable;

public interface CoreAdminPageConfigService
extends GenericService<CoreAdminPageConfigBean, Long>,
GenericActivatableService<CoreAdminPageConfigBean, Long> {
    public List<CoreAdminPageConfigLineBean> selectActivatedConfigLineList(@Nullable String var1, String var2);
}
