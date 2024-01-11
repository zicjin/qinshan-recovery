package com.sunwayworld.emc.module.mdm.equipt.equipt.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.controller.EmcEquiptMaintainRepairController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcEquiptMaintainRepairControllerImpl implements EmcEquiptMaintainRepairController {

    @Log(value = "仪器维护维修编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-maintain-repairs/emc-equipt-maintain-repair-edit-list";
    }

}
