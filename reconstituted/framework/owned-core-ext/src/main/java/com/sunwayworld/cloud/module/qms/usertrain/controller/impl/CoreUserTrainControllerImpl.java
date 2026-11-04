/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.usertrain.controller.impl;

import com.sunwayworld.cloud.module.qms.usertrain.controller.CoreUserTrainController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u4eba\u5458\u57f9\u8bad")
@Controller
@GikamBean
public class CoreUserTrainControllerImpl
implements CoreUserTrainController {
    @Override
    @Log(value="\u4eba\u5458\u57f9\u8bad\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/user-trains/core-user-train-edit-list";
    }

    @Override
    @Log(value="\u4eba\u5458\u57f9\u8bad\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/user-trains/core-user-train-detail";
    }

    @Override
    @Log(value="\u4eba\u5458\u57f9\u8bad\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/user-trains/core-user-train-audit-list";
    }

    @Override
    @Log(value="\u4eba\u5458\u57f9\u8bad\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/user-trains/core-user-train-search-list";
    }
}
