/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.controller.impl;

import com.sunwayworld.cloud.module.fr.report.controller.CoreReportConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreReportConfigControllerImpl
implements CoreReportConfigController {
    @Override
    @Log(value="\u62a5\u8868\u914d\u7f6e\u8be6\u60c5\u9875")
    public String detailPage() {
        return "module/sys/reports/core-report-config-detail";
    }

    @Override
    @Log(value="\u62a5\u8868\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875")
    public String editListPage() {
        return "module/sys/reports/core-report-config-edit-list";
    }

    @Override
    @Log(value="\u62a5\u8868\u914d\u7f6e\u660e\u7ec6\u9009\u62e9\u9875")
    public String lineSelectPage() {
        return "module/sys/reports/core-report-config-line-select";
    }

    @Override
    @Log(value="\u62a5\u8868\u660e\u7ec6\u914d\u7f6echoose\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String lineChooseListPage() {
        return "module/sys/reports/core-report-config-line-choose-list";
    }

    @Override
    @Log(value="\u62a5\u8868\u660e\u7ec6\u67e5\u8be2\u9875", type=LogType.CONTROLLER)
    public String lineSearchListPage() {
        return "module/sys/reports/core-report-config-line-search-list";
    }

    @Override
    @Log(value="\u660e\u7ec6\u5b50\u62a5\u8868\u914d\u7f6e\u9875\u9762", type=LogType.CONTROLLER)
    public String subReportEditListPage() {
        return "module/sys/reports/core-report-config-line-sub-report-edit-list";
    }

    @Override
    @Log(value="\u62a5\u8868\u914d\u7f6e\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/reports/core-report-config-choose-list";
    }
}
