/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.controller.CoreMenuRoleController;
import org.springframework.stereotype.Controller;

@LogModule(value="\u83dc\u5355\u89d2\u8272\u77e9\u9635")
@Controller
@GikamBean
public class CoreMenuRoleControllerImpl
implements CoreMenuRoleController {
    @Override
    @Log(value="\u83dc\u5355\u89d2\u8272\u77e9\u9635\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String matrixPage() {
        return "module/sys/menu-roles/core-menu-role-matrix";
    }
}
