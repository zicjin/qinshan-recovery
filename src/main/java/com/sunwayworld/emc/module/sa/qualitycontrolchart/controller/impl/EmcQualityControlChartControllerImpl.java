package com.sunwayworld.emc.module.sa.qualitycontrolchart.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.sa.qualitycontrolchart.controller.EmcQualityControlChartController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcQualityControlChartControllerImpl implements EmcQualityControlChartController {

    @Override
    @Log(value = "质控图查询页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/sa/quality-control-charts/emc-quality-control-chart-search-list";
    }

}
