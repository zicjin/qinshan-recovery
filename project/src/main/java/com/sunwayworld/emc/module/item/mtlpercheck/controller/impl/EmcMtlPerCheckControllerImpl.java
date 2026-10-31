package com.sunwayworld.emc.module.item.mtlpercheck.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.mtlpercheck.controller.EmcMtlPerCheckController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcMtlPerCheckControllerImpl implements EmcMtlPerCheckController {

    @Log(value = "标准物质校准计划编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/mtl-per-checks/emc-mtl-per-check-edit-list";
    }

    @Log(value = "标准物质校准计划编制列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/mtl-per-checks/emc-mtl-per-check-audit-list";
    }
    
}
