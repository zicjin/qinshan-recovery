/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.controller.CoreAdminSelectConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAdminSelectConfigControllerImpl
implements CoreAdminSelectConfigController {
    @Override
    public String editListPage() {
        return "module/admin/select-configs/core-select-config-edit-list";
    }
}
