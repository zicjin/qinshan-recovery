/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.controller.CoreMenuController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreMenuControllerImpl
implements CoreMenuController {
    @Override
    public String editListPage() {
        return "module/sys/menus/core-menu-edit-list";
    }

    @Override
    public String choosePage() {
        return "module/sys/menus/core-menu-choose-list";
    }

    @Log(value="\u83dc\u5355\u9009\u62e9", type=LogType.CONTROLLER)
    @RequestMapping(value={"/edit-choose-list"})
    public String editChoosePage() {
        return "module/sys/menus/core-menu-edit-choose-list";
    }
}
