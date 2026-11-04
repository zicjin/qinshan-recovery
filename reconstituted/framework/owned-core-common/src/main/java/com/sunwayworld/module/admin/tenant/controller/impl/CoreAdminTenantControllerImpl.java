/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.tenant.controller.CoreAdminTenantController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

@Profile(value={"tenant"})
@Controller
@GikamBean
public class CoreAdminTenantControllerImpl
implements CoreAdminTenantController {
    @Override
    public String editListPage() {
        return "module/admin/tenants/core-tenant-edit-list";
    }
}
