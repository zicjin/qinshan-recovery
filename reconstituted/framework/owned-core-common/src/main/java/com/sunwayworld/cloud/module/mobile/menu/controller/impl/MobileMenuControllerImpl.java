/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.controller.impl;

import com.sunwayworld.cloud.module.mobile.menu.controller.MobileMenuController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class MobileMenuControllerImpl
implements MobileMenuController {
    @Override
    @Log(value="\u79fb\u52a8\u83dc\u5355\u914d\u7f6e->\u4e3b\u5217\u8868", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/mobile/menus/mobile-menu-edit-list";
    }

    @Override
    @Log(value="\u79fb\u52a8\u83dc\u5355\u914d\u7f6e->\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/mobile/menus/mobile-page-edit-list";
    }

    @Override
    @Log(value="\u79fb\u52a8\u83dc\u5355\u914d\u7f6e->\u5f39\u51fa\u7f16\u8f91\u6846", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/mobile/menus/mobile-menu-choose-list";
    }
}
