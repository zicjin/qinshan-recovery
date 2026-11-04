/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.train.controller.impl;

import com.sunwayworld.cloud.module.train.train.controller.CoreTrainController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u57f9\u8bad\u5b9e\u65bd")
@Controller
@GikamBean
public class CoreTrainControllerImpl
implements CoreTrainController {
    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u7ba1\u7406\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/train/trains/core-train-edit-list";
    }

    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/train/trains/core-train-search-list";
    }

    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/train/trains/core-train-detail";
    }

    @Override
    @Log(value="\u57f9\u8bad\u5b9e\u65bd\u53c2\u4e0e\u4eba\u5458\u67e5\u8be2\u9875\u9762", type=LogType.CONTROLLER)
    public String userSearchPage() {
        return "module/train/trains/core-train-user-search";
    }
}
