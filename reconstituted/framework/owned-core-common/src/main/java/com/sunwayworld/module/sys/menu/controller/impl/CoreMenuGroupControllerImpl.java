/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.controller.CoreMenuGroupController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreMenuGroupControllerImpl
implements CoreMenuGroupController {
    @Override
    public String editListPage() {
        return "module/sys/menu-groups/core-menu-group-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/sys/menu-groups/core-menu-group-detail";
    }

    @Override
    public String choosePage() {
        return "module/sys/menu-groups/core-menu-group-choose-list";
    }
}
