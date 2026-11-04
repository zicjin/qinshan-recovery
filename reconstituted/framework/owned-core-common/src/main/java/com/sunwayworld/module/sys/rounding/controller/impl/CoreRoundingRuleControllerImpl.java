/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.rounding.controller.CoreRoundingRuleController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreRoundingRuleControllerImpl
implements CoreRoundingRuleController {
    @Override
    @Log(value="\u4fee\u7ea6\u89c4\u5219\u5217\u8868\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/rounding-rules/core-rounding-rule-edit-list";
    }

    @Override
    @Log(value="\u4fee\u7ea6\u89c4\u5219\u8be6\u7ec6\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/rounding-rules/core-rounding-rule-detail";
    }

    @Override
    @Log(value="\u4fee\u7ea6\u89c4\u5219\u9009\u62e9\u9875\u9762", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/sys/rounding-rules/core-rounding-rule-choose-list";
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u4fee\u7ea6\u89c4\u5219\u9875\u9762", type=LogType.CONTROLLER)
    public String calculateDetailPage() {
        return "module/sys/rounding-rules/core-rounding-rule-calculate-detail";
    }
}
