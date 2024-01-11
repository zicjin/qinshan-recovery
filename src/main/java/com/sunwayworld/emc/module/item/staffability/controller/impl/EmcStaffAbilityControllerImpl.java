package com.sunwayworld.emc.module.item.staffability.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.staffability.controller.EmcStaffAbilityController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcStaffAbilityControllerImpl implements EmcStaffAbilityController {

    @Log(value = "人员能力表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/staff-abilitys/emc-staff-ability-edit-list";
    }

}
