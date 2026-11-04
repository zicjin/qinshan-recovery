/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.controller.CoreAdminPageConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAdminPageConfigControllerImpl
implements CoreAdminPageConfigController {
    @Override
    public String editListPage() {
        return "module/admin/page-configs/core-page-config-edit-list";
    }

    @Override
    public String designPage() {
        return "module/gikam/design";
    }
}
