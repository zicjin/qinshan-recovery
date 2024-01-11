package com.sunwayworld.emc.module.item.equiptassayplan.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.equiptassayplan.controller.EmcEquiptAssayPlanController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcEquiptAssayPlanControllerImpl implements EmcEquiptAssayPlanController {

    @Log(value = "仪器检定校准计划编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/equipt-assay-plans/emc-equipt-assay-plan-edit-list";
    }
    
    @Log(value = "仪器检定校准计划审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/equipt-assay-plans/emc-equipt-assay-plan-audit-list";
    }


}
