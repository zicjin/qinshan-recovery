package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.controller.EmcEquiptCaliResultController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptCaliResultControllerImpl implements EmcEquiptCaliResultController {

    @Log(value = "校准结果确认记录编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-cali-results/emc-equipt-cali-result-edit-list";
    }


    @Log(value = "校准结果确认记录查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/equipt/equipt-cali-results/emc-equipt-cali-result-search-list";
    }

    @Log(value = "校准结果确认记录审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/mdm/equipt/equipt-cali-results/emc-equipt-cali-result-audit-list";
    }
}
