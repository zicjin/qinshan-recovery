package com.sunwayworld.emc.module.bp.bp.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.bp.controller.EmcOrderTaskResultController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcOrderTaskResultControllerImpl implements EmcOrderTaskResultController{
    @Log(value = "数据汇总页", type = LogType.CONTROLLER)
    @Override
    public String dataSummaryPage() {
        return "emc/module/bp/result/taskresults/emc-result-data-summary-list";
    }
}
