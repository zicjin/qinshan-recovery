package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.controller.EmcMonitorPointCategoryController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

@Controller
@GikamBean
public class EmcMonitorPointCategoryControllerImpl implements EmcMonitorPointCategoryController {

    @Log(value = "监测点类型编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/monitor-point-categories/emc-monitor-point-category-edit-list";
    }

    @Log(value = "监测点类型弹出选择页面", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/monitor-point-categories/emc-monitor-point-category-choose-list";
    }

}
