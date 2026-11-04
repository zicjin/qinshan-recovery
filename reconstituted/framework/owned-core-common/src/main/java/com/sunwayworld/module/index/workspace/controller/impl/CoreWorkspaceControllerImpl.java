/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.index.workspace.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.index.workspace.controller.CoreWorkspaceController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreWorkspaceControllerImpl
implements CoreWorkspaceController {
    @Override
    @Log(value="\u767b\u5f55\u9996\u9875", type=LogType.CONTROLLER)
    public String indexPage() {
        return "module/index/workspace/index";
    }

    @Override
    @Log(value="\u9996\u9875\u5f85\u529e\u9762\u677f", type=LogType.CONTROLLER)
    public String todoPanelPage() {
        return "module/item/workspace-panels/core-todo-panel";
    }

    @Override
    @Log(value="\u9996\u9875\u901a\u77e5\u9762\u677f", type=LogType.CONTROLLER)
    public String noticePanelPage() {
        return "module/item/workspace-panels/core-notice-panel";
    }

    @Override
    @Log(value="\u5185\u7f51\u9996\u9875\u9759\u6001\u8d44\u6e90\u53ca\u5168\u5c40\u53c2\u6570\u914d\u7f6e", type=LogType.CONTROLLER)
    public String resourcePage() {
        return "module/gikam/workspace";
    }
}
