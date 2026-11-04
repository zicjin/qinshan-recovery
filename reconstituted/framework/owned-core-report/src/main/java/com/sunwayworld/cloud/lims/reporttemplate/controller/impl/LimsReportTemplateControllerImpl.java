/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.controller.impl;

import com.sunwayworld.cloud.lims.reporttemplate.controller.LimsReportTemplateController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class LimsReportTemplateControllerImpl
implements LimsReportTemplateController {
    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "lims/module/report-templates/lims-report-template-edit-list";
    }

    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u6570\u636e\u6e90\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    @RequestMapping(value={"/sources/detail"})
    public String sourceDetailPage() {
        return "lims/module/report-templates/lims-report-template-source-detail";
    }

    @Override
    @Log(value="\u62a5\u544a\u6a21\u677f\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "lims/module/report-templates/lims-report-template-choose-list";
    }
}
