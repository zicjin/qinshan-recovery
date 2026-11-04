/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.controller.CoreAdminCheckConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAdminCheckConfigControllerImpl
implements CoreAdminCheckConfigController {
    @Override
    public String editListPage() {
        return "module/admin/check-configs/core-check-config-edit-list";
    }
}
