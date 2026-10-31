package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.controller.impl;



import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.controller.PfEquiptTempController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * 容器类型 Controller
 *
 */
@Controller
@GikamBean
public class PfEquiptTempControllerImpl implements PfEquiptTempController {

    @Override
    @Log(value = "仪器温度维护列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/equipt-temp/emc-equipt-temp-edit-list";
    }
    
}

