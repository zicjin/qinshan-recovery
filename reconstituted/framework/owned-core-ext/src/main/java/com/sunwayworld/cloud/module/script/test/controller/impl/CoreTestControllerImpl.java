/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.controller.impl;

import com.sunwayworld.cloud.module.script.test.controller.CoreTestController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u6d4b\u8bd5\u7ba1\u7406")
@Controller
@GikamBean
public class CoreTestControllerImpl
implements CoreTestController {
    @Override
    @Log(value="\u6d4b\u8bd5\u7ba1\u7406\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/script/tests/core-test-edit-list";
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u5206\u6790\u9879\u8ba1\u7b97\u516c\u5f0f\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String itemScriptDetailPage() {
        return "module/script/tests/core-test-item-script-detail";
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u5206\u6790\u9879\u8ba1\u7b97\u516c\u5f0f\u9009\u62e9\u5668\u9875", type=LogType.CONTROLLER)
    public String itemScriptSelectorPage() {
        return "module/script/tests/core-test-item-script-selector";
    }
}
