/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.controller.impl;

import com.sunwayworld.cloud.module.qms.qualitycontrol.controller.CoreQualityControlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u8d28\u91cf\u63a7\u5236")
@Controller
@GikamBean
public class CoreQualityControlControllerImpl
implements CoreQualityControlController {
    @Override
    @Log(value="\u8d28\u91cf\u63a7\u5236\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/quality-controls/core-quality-control-edit-list";
    }

    @Override
    @Log(value="\u8d28\u91cf\u63a7\u5236\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/quality-controls/core-quality-control-detail";
    }

    @Override
    @Log(value="\u8d28\u91cf\u63a7\u5236\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/quality-controls/core-quality-control-audit-list";
    }

    @Override
    @Log(value="\u8d28\u91cf\u63a7\u5236\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/quality-controls/core-quality-control-search-list";
    }

    @Override
    @Log(value="\u8d28\u91cf\u63a7\u5236\u8ba1\u5212\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String planChooseListPage() {
        return "module/qms/quality-control-plans/core-quality-control-plan-choose-list";
    }
}
