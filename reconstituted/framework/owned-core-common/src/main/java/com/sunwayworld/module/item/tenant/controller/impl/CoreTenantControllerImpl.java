/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.tenant.controller.CoreTenantController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreTenantControllerImpl
implements CoreTenantController {
    @Override
    @Log(value="\u79df\u6237\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/tenants/core-tenant-edit-list";
    }
}
