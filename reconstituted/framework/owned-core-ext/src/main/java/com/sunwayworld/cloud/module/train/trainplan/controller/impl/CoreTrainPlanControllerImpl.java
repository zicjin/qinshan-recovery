/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.controller.impl;

import com.sunwayworld.cloud.module.train.trainplan.controller.CoreTrainPlanController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u57f9\u8bad\u8ba1\u5212")
@Controller
@GikamBean
public class CoreTrainPlanControllerImpl
implements CoreTrainPlanController {
    @Override
    @Log(value="\u57f9\u8bad\u8ba1\u5212\u7ba1\u7406\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/train/train-plans/core-train-plan-edit-list";
    }

    @Override
    @Log(value="\u57f9\u8bad\u8ba1\u5212\u5ba1\u6838\u9875\u9762", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "module/train/train-plans/core-train-plan-audit-list";
    }

    @Override
    @Log(value="\u57f9\u8bad\u8ba1\u5212\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/train/train-plans/core-train-plan-detail";
    }

    @Override
    @Log(value="\u57f9\u8bad\u8ba1\u5212\u9009\u62e9\u9875\u9762", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/train/train-plans/core-train-plan-choose-list";
    }

    @Override
    @Log(value="\u57f9\u8bad\u8ba1\u5212\u53c2\u4e0e\u4eba\u5458\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String userSearchPage() {
        return "module/train/train-plans/core-train-plan-user-search";
    }
}
