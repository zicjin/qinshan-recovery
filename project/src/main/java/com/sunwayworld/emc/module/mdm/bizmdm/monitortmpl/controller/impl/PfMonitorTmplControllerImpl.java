package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.controller.PfMonitorTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月31日
 */
@Controller
@GikamBean
public class PfMonitorTmplControllerImpl extends EmcMonitorTmplControllerImpl implements PfMonitorTmplController {

    @Override
    @Log(value = "采样费用页", type = LogType.CONTROLLER)
    @RequestMapping("/sampling-cost")
    public String monitorSchemeSamplingCostPage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-scheme-sampling-cost-list";
    }
}
