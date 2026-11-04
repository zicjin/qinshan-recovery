/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.index.workspace.controller;

import com.sunwayworld.framework.controller.GenericIndexPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/module/index/workspaces"})
public interface CoreWorkspaceController
extends GenericIndexPageController {
    @RequestMapping(value={"/todo-panel"})
    public String todoPanelPage();

    @RequestMapping(value={"/notice-panel"})
    public String noticePanelPage();

    @RequestMapping(value={"/resource-page"})
    public String resourcePage();
}
