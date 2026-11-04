/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.helpcenter.controller.CoreHelpCenterController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreHelpCenterControllerImpl
implements CoreHelpCenterController {
    @Override
    @Log(value="\u5e2e\u52a9\u4e2d\u5fc3\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/admin/help-centers/core-help-center-edit-list";
    }
}
