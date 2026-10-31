package com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.controller.EmcEquiptCaliResultNormController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptCaliResultNormControllerImpl implements EmcEquiptCaliResultNormController {

    @Log(value = "测试方法中间表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-cali-result-norms/emc-equipt-cali-result-norm-edit-list";
    }

}
