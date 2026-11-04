/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqscript.controller.impl;

import com.sunwayworld.cloud.module.equipt.acqscript.controller.CoreEquiptAcqScriptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreEquiptAcqScriptControllerImpl
implements CoreEquiptAcqScriptController {
    @Override
    @Log(value="\u4eea\u5668\u91c7\u96c6\u811a\u672c\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/equipt/acq-scripts/core-equipt-acq-script-edit-list";
    }

    @Override
    @Log(value="\u4eea\u5668\u91c7\u96c6\u811a\u672c\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/equipt/acq-scripts/core-equipt-acq-script-detail";
    }

    @Override
    @Log(value="\u4eea\u5668\u91c7\u96c6\u811a\u672c\u9009\u62e9\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/equipt/acq-scripts/core-equipt-acq-script-choose-list";
    }
}
