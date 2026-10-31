package com.sunwayworld.emc.module.sys.rounding.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.module.sys.rounding.controller.impl.CoreRoundingRuleControllerImpl;

/**
 * @author zhengxy@sunwayworld.com 2020年04月03日
 */
@Controller
public class EmcRoundingRuleControllerImpl extends CoreRoundingRuleControllerImpl {

    @Override
    public String choosePage() {
        return "emc/module/sys/rounding-rules/emc-rounding-rule-choose-list";
    }
}
