/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.workspacescheme.impl;

import com.sunwayworld.cloud.module.item.controller.workspacescheme.CoreWorkspacePanelController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreWorkspacePanelControllerImpl
implements CoreWorkspacePanelController {
    @Override
    @Log(value="\u9996\u9875\u9762\u677f\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/workspace-panels/core-workspace-panel-edit-list";
    }
}
