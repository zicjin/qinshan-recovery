package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.controller.EmcEquiptPerCheckController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptPerCheckControllerImpl implements EmcEquiptPerCheckController {

    @Log(value = "期间核查表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-per-checks/emc-equipt-per-check-edit-list";
    }

    @Log(value = "期间核查表审核列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/sup-audit-list")
    public String auditListPage() {
        return "emc/module/mdm/equipt/equipt-per-checks/emc-equipt-per-check-sup-audit-list";
    }

    @Log(value = "期间核查表查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/equipt/equipt-per-checks/emc-equipt-per-check-search-list";
    }

    @Log(value = "期间核查表负责人审核列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/prin-audit-list")
    public String prinAuditListPage() {
        return "emc/module/mdm/equipt/equipt-per-checks/emc-equipt-per-check-prin-audit-list";
    }
}
