package com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.controller.EmcSamplenumberController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcSamplenumberControllerImpl implements EmcSamplenumberController {

    @Log(value = "采样单编码信息编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/samplenumbers/emc-samplenumber-edit-list";
    }

}
