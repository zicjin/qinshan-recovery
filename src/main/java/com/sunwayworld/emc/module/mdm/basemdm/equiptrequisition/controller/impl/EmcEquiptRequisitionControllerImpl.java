package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.controller.EmcEquiptRequisitionController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcEquiptRequisitionControllerImpl implements EmcEquiptRequisitionController {

    @Log(value = "仪器出入库编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/equipt-requisitions/emc-equipt-requisition-edit-list";
    }

}
