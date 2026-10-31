package com.sunwayworld.emc.module.bp.reagent.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.reagent.controller.EmcReagentController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcReagentControllerImpl implements EmcReagentController {

    @Log(value = "一般试剂配制编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/reagents/emc-reagent-edit-list";
    }

    @Override
    public String stockChooseListPage() {
        return "emc/module/bp/reagents/emc-mtl-stock-choose-list";
    }
}
