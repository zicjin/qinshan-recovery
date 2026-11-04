/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.controller.impl;

import com.sunwayworld.cloud.module.qms.riskfactor.controller.CoreRiskFactorController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u98ce\u9669\u56e0\u7d20")
@Controller
@GikamBean
public class CoreRiskFactorControllerImpl
implements CoreRiskFactorController {
    @Override
    @Log(value="\u98ce\u9669\u56e0\u7d20\u7f16\u5236\u5217\u8868", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/qms/risk-factors/core-risk-factor-edit-list";
    }

    @Override
    @Log(value="\u98ce\u9669\u56e0\u7d20\u9009\u62e9\u5217\u8868", type=LogType.CONTROLLER)
    public String choosePage() {
        return "module/qms/risk-factors/core-risk-factor-choose-list";
    }
}
