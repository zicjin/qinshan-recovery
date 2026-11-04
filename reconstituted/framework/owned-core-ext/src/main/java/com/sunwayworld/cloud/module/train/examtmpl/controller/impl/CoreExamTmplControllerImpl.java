/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.examtmpl.controller.impl;

import com.sunwayworld.cloud.module.train.examtmpl.controller.CoreExamTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u8bd5\u5377\u6a21\u677f")
@Controller
@GikamBean
public class CoreExamTmplControllerImpl
implements CoreExamTmplController {
    @Override
    @Log(value="\u8bd5\u5377\u6a21\u677f\u7f16\u5236\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/train/exam-tmpls/core-exam-tmpl-edit-list";
    }

    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/train/exam-tmpls/core-exam-tmpl-detail";
    }

    @Override
    @Log(value="\u8bd5\u5377\u6a21\u677f\u9009\u62e9\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/train/exam-tmpls/core-exam-tmpl-choose-list";
    }
}
