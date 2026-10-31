// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.sys.role.controller.impl;

import com.sunwayworld.module.sys.role.controller.impl.CoreRoleUserControllerImpl;
import org.springframework.stereotype.Controller;

@Controller
public class EmcRoleUserControllerImpl
extends CoreRoleUserControllerImpl {
    @Override
    public String matrixPage() {
        return "emc/module/sys/role-users/emc-role-user-matrix";
    }
}
