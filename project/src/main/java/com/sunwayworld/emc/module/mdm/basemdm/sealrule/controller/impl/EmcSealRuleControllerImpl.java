package com.sunwayworld.emc.module.mdm.basemdm.sealrule.controller.impl;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.controller.EmcSealRuleController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcSealRuleControllerImpl implements EmcSealRuleController {

    @Log(value = "盖章打印规则编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/seal-rules/emc-seal-rule-edit-list";
    }

    @Log(value = "盖章打印规则选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/seal-rules/emc-seal-rule-choose-list";
    }

    @Log(value = "盖章打印规则详情选择列表页", type = LogType.CONTROLLER)
    @Override
    public String detailsChoosePage() {
        return "emc/module/mdm/basemdm/seal-rules/emc-seal-rule-details-choose-list";
    }
}
