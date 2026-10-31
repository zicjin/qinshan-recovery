package com.sunwayworld.emc.module.bp.samplebottle.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.bp.samplebottle.controller.EmcSampleBottleController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcSampleBottleControllerImpl implements EmcSampleBottleController {

    @Log(value = "采样瓶表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/sample-bottles/emc-sample-bottle-edit-list";
    }

}
