/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/roles/page"})
public interface CoreRoleController
extends GenericEditListPageController,
GenericChoosePageController {
    @RequestMapping(value={"/menu-role-edit-list"})
    public String menuEditListPage();

    @RequestMapping(value={"/assigned-menu-tree"})
    public String assignMenuTree();

    @RequestMapping(value={"/workflow-role-edit-list"})
    public String workflowEditListPage();

    @RequestMapping(value={"/assigned-user-list"})
    public String assignedUserListPage();

    @RequestMapping(value={"/data-role-edit-list"})
    public String dataEditListPage();

    @RequestMapping(value={"/assigned-org-list"})
    public String assignedOrgListPage();

    @RequestMapping(value={"/assigned-wkpanel-list"})
    public String assignedWorkspacePanelListPage();

    @RequestMapping(value={"/permission-matrix-list"})
    public String permissionMatrixListPage();
}
