package com.sunwayworld.emc.module.bp.result.run.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.result.run.controller.EmcRunRemindController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcRunRemindControllerImpl implements EmcRunRemindController {

    @Log(value = "批次提醒编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/result/run-reminds/emc-run-remind-edit-list";
    }

}
