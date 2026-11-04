/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.controller.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.controller.CoreCustomSurveyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u8c03\u67e5\u4fe1\u606f")
@Controller
@GikamBean
public class CoreCustomSurveyControllerImpl
implements CoreCustomSurveyController {
    @Override
    @Log(value="\u8c03\u67e5\u4fe1\u606f\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/custom-surveys/core-custom-survey-edit-list";
    }

    @Override
    @Log(value="\u8c03\u67e5\u4fe1\u606f\u5ba1\u6838\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/qms/custom-surveys/core-custom-survey-audit-list";
    }

    @Override
    @Log(value="\u8c03\u67e5\u4fe1\u606f\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/qms/custom-surveys/core-custom-survey-search-list";
    }
}
