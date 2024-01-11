package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.controller.EmcEquiptAcqResultController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptAcqResultControllerImpl implements EmcEquiptAcqResultController {

    @Log(value = "设备选择查询页面跳转", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/equipt/equipt-acq-results/emc-equipt-acq-result-choose-list";
    }

}
