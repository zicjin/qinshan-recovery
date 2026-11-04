/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.controller.impl;

import com.sunwayworld.cloud.module.qms.launchnewproject.controller.CoreLaunchNewProjectController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u65b0\u9879\u76ee\u5f00\u5c55")
@Controller
@GikamBean
public class CoreLaunchNewProjectControllerImpl
implements CoreLaunchNewProjectController {
    @Override
    @Log(value="\u65b0\u9879\u76ee\u5f00\u5c55\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/launch-new-projects/core-launch-new-project-edit-list";
    }

    @Override
    @Log(value="\u65b0\u9879\u76ee\u5f00\u5c55\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/launch-new-projects/core-launch-new-project-detail";
    }

    @Override
    @Log(value="\u65b0\u9879\u76ee\u5f00\u5c55\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/launch-new-projects/core-launch-new-project-audit-list";
    }

    @Override
    @Log(value="\u65b0\u9879\u76ee\u5f00\u5c55\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/launch-new-projects/core-launch-new-project-search-list";
    }

    @Override
    @Log(value="\u65b0\u9879\u76ee\u5f00\u5c55\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/launch-new-projects/core-launch-new-project-choose-list";
    }
}
