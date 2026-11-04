/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.config.controller.CoreAdminChooseConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAdminChooseConfigControllerImpl
implements CoreAdminChooseConfigController {
    @Override
    public String editListPage() {
        return "module/admin/choose-configs/core-choose-config-edit-list";
    }
}
