/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.controller.impl;

import com.sunwayworld.cloud.module.train.question.controller.CoreQuestionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u9898\u76ee")
@Controller
@GikamBean
public class CoreQuestionControllerImpl
implements CoreQuestionController {
    @Override
    @Log(value="\u9898\u76ee\u7f16\u5236\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/train/questions/core-question-edit-list";
    }

    @Override
    @Log(value="\u9898\u76ee\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/train/questions/core-question-detail";
    }

    @Override
    @Log(value="\u9898\u5e93\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String questionBankDetailPage() {
        return "module/train/questions/core-question-bank-detail";
    }
}
