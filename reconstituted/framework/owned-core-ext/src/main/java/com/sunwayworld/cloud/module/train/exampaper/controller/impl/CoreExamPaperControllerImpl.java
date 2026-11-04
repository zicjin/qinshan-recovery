/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.controller.impl;

import com.sunwayworld.cloud.module.train.exampaper.controller.CoreExamPaperController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u8003\u5377")
@Controller
@GikamBean
public class CoreExamPaperControllerImpl
implements CoreExamPaperController {
    @Override
    @Log(value="\u8003\u5377\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/train/exam-papers/core-exam-paper-edit-list";
    }

    @Override
    @Log(value="\u8003\u5377\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/train/exam-papers/core-exam-paper-detail";
    }

    @Override
    @Log(value="\u8003\u8bd5\u9605\u5377\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String reviewListPage() {
        return "module/train/exam-papers/core-exam-paper-review-list";
    }

    @Override
    @Log(value="\u8003\u8bd5\u9605\u5377\u9875", type=LogType.CONTROLLER)
    public String reviewDetailPage() {
        return "module/train/exam-papers/core-exam-paper-review-detail";
    }
}
