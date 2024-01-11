package com.sunwayworld.emc.module.mdm.basemdm.revisionrule.controller.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.controller.EmcRevisionRuleController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcRevisionRuleControllerImpl implements EmcRevisionRuleController {

    @Log(value = "修约规则选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/revision-rules/emc-revision-rule-choose-list";
    }

}
