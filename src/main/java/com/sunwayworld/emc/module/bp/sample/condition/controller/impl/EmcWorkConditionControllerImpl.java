package com.sunwayworld.emc.module.bp.sample.condition.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.sample.condition.controller.EmcWorkConditionController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcWorkConditionControllerImpl implements EmcWorkConditionController {

    @Log(value = "现场工况编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/sample/conditions/emc-work-condition-edit-list";
    }

}
