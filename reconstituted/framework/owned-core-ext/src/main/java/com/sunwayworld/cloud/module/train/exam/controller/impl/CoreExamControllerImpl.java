/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.controller.impl;

import com.sunwayworld.cloud.module.train.exam.controller.CoreExamController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u8bd5\u5377\u7ba1\u7406")
@Controller
@GikamBean
public class CoreExamControllerImpl
implements CoreExamController {
    @Override
    @Log(value="\u8bd5\u5377\u7f16\u5236\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/train/exams/core-exam-edit-list";
    }

    @Override
    @Log(value="\u8bd5\u5377\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/train/exams/core-exam-detail";
    }

    @Override
    @Log(value="\u8bd5\u5377\u9009\u62e9\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/train/exams/core-exam-choose-list";
    }

    @Override
    @Log(value="\u8bd5\u5377\u9898\u76ee\u7f16\u8f91\u9875\u9762", type=LogType.CONTROLLER)
    public String questionEditPage() {
        return "module/train/exams/core-exam-question-edit";
    }
}
