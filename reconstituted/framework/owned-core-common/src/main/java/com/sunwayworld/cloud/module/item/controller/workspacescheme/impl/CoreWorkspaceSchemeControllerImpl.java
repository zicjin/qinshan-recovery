/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.workspacescheme.impl;

import com.sunwayworld.cloud.module.item.controller.workspacescheme.CoreWorkspaceSchemeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreWorkspaceSchemeControllerImpl
implements CoreWorkspaceSchemeController {
    @Override
    @Log(value="\u9996\u9875\u65b9\u6848\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/workspace-schemes/core-workspace-scheme-edit-list";
    }

    @Override
    @Log(value="\u9996\u9875\u65b9\u6848\u914d\u7f6e\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/item/workspace-schemes/core-workspace-scheme-choose-list";
    }
}
