package com.sunwayworld.emc.module.mdm.bizmdm.comparison.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.controller.EmcComparisonController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
@Controller
@GikamBean
public class EmcComparisonControllerImpl implements EmcComparisonController {

    @Override
    @Log(value = "比对信息编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/bizmdm/comparisons/emc-comparison-edit-list";
    }

    @Override
    @Log(value = "比对结果列表页面", type = LogType.CONTROLLER)
    public String comparisonResultPage() {
        return "emc/module/mdm/bizmdm/comparisons/emc-comparison-result-edit-list";
    }
}
