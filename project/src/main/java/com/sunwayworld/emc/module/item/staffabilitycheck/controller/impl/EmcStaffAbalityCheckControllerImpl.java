package com.sunwayworld.emc.module.item.staffabilitycheck.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.staffabilitycheck.controller.EmcStaffAbalityCheckController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcStaffAbalityCheckControllerImpl implements EmcStaffAbalityCheckController {

    @Log(value = "能力验证表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/staff-abality-checks/emc-staff-abality-check-edit-list";
    }

    @Log(value = "能力验证表查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/item/staff-abality-checks/emc-staff-abality-check-search-list";
    }

}
