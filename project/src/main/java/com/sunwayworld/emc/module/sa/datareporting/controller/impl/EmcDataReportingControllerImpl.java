package com.sunwayworld.emc.module.sa.datareporting.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.datareporting.controller.EmcDataReportingController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcDataReportingControllerImpl implements EmcDataReportingController {

    @Override
    @Log(value = "数据上报页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/sa/data-reportings/emc-data-reporting-edit-list";
    }
}
