/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.role.controller.CoreRoleController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreRoleControllerImpl
implements CoreRoleController {
    @Override
    @Log(value="\u89d2\u8272\u7f16\u8f91\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/roles/core-role-edit-list";
    }

    @Override
    @Log(value="\u89d2\u8272\u9009\u62e9\u9875\uff08\u53ef\u5d4c\u5165\uff09", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/roles/core-role-choose-list";
    }

    @Override
    @Log(value="\u89d2\u8272\u7528\u6237\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String assignedUserListPage() {
        return "module/sys/roles/role-user-edit-list";
    }

    @Override
    @Log(value="\u529f\u80fd\u89d2\u8272\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    public String menuEditListPage() {
        return "module/sys/roles/menu/menu-role-edit-list";
    }

    @Override
    @Log(value="\u529f\u80fd\u89d2\u8272\u5206\u914d\u83dc\u5355\u9875\u9762", type=LogType.CONTROLLER)
    public String assignMenuTree() {
        return "module/sys/roles/menu/menu-role-select-menu-tree";
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u89d2\u8272\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    public String workflowEditListPage() {
        return "module/sys/roles/workflow/workflow-role-edit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u89d2\u8272\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    public String dataEditListPage() {
        return "module/sys/roles/data/data-role-edit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u89d2\u8272\u5206\u914d\u5355\u4f4d\u9875\u9762", type=LogType.CONTROLLER)
    public String assignedOrgListPage() {
        return "module/sys/roles/data/org-role-edit-list";
    }

    @Override
    @Log(value="\u6570\u636e\u89d2\u8272\u5206\u914d\u5de5\u4f5c\u533a\u9762\u677f\u9875\u9762", type=LogType.CONTROLLER)
    public String assignedWorkspacePanelListPage() {
        return "module/sys/roles/data/workspace-panel-role-edit-list";
    }

    @Override
    @Log(value="\u89d2\u8272\u7528\u6237\u77e9\u9635\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String permissionMatrixListPage() {
        return "module/sys/role-users/core-role-user-permission-matrix-list";
    }
}
