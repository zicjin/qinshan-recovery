package com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.controller.EmcEquiptAcqParamController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptAcqParamControllerImpl implements EmcEquiptAcqParamController {

    @Log(value = "仪器采集参数配置编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-acq-params/emc-equipt-acq-param-edit-list";
    }

    @Log(value = "仪器采集参数选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/equipt/equipt-acq-params/emc-equipt-acq-param-choose-list";
    }

}
