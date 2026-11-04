/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.controller.impl;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.controller.CoreCustomSrtSurveyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868")
@Controller
@GikamBean
public class CoreCustomSrtSurveyControllerImpl
implements CoreCustomSrtSurveyController {
    @Override
    @Log(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/custom-srt-surveys/core-custom-srt-survey-edit-list";
    }

    @Override
    @Log(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/qms/custom-srt-surveys/core-custom-srt-survey-detail";
    }

    @Override
    @Log(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/custom-srt-surveys/core-custom-srt-survey-audit-list";
    }

    @Override
    @Log(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/custom-srt-surveys/core-custom-srt-survey-search-list";
    }

    @Override
    @Log(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/custom-srt-surveys/core-custom-srt-survey-choose-list";
    }
}
