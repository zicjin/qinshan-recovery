/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.controller.CoreRoleUserController;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreRoleUserControllerImpl
implements CoreRoleUserController {
    @Override
    @Log(value="\u89d2\u8272\u7528\u6237\u77e9\u9635\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String matrixPage() {
        return "module/sys/role-users/core-role-user-matrix";
    }
}
