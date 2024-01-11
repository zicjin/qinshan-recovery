package com.sunwayworld.emc.module.mdm.bizmdm.capacity.controller.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.controller.EmcTestCapacityController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcTestCapacityControllerImpl implements EmcTestCapacityController {

    @Log(value = "监测能力库编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/test-capacitys/emc-test-capacity-edit-list";
    }

}
