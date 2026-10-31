package com.sunwayworld.emc.module.mdm.basemdm.monitorability.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.controller.EmcMonitorAbilityController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Controller
@GikamBean
public class EmcMonitorAbilityControllerImpl implements EmcMonitorAbilityController{

    @Log(value = "监测能力编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/monitor-abilities/emc-monitor-ability-edit-list";
    }

    @Log(value = "监测能力详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/monitor-abilities/emc-monitor-ability-detail";
    }
    
    @Log(value = "监测能力选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/monitor-abilities/emc-monitor-ability-choose-list";
    }
    
    @Log(value = "监测能力方法版本详情页", type = LogType.CONTROLLER)
    @Override
    public String methodDetailPage() {
        return "emc/module/mdm/basemdm/monitor-abilities/emc-monitor-ability-method-detail";
    }

}
