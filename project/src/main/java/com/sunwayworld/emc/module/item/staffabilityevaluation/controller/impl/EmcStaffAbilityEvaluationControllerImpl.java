package com.sunwayworld.emc.module.item.staffabilityevaluation.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.staffabilityevaluation.controller.EmcStaffAbilityEvaluationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcStaffAbilityEvaluationControllerImpl implements EmcStaffAbilityEvaluationController {

    @Log(value = "人员能力评估编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/staff-ability-evaluations/emc-staff-ability-evaluation-edit-list";
    }

}
