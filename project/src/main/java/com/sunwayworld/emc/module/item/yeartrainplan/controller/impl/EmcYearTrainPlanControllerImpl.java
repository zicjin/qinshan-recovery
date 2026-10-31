package com.sunwayworld.emc.module.item.yeartrainplan.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.yeartrainplan.controller.EmcYearTrainPlanController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcYearTrainPlanControllerImpl implements EmcYearTrainPlanController {

    @Log(value = "年度计划培训编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/year-train-plans/emc-year-train-plan-edit-list";
    }
    
    @Log(value = "年度计划培训审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/year-train-plans/emc-year-train-plan-audit-list";
    }

}
