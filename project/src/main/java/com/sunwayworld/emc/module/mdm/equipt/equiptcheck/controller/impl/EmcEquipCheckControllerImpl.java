package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.controller.EmcEquipCheckController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquipCheckControllerImpl implements EmcEquipCheckController {

    @Log(value = "仪器验收申请编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equip-checks/emc-equip-check-edit-list";
    }

    @Log(value = "仪器验收申请审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/mdm/equipt/equip-checks/emc-equip-check-audit-list";
    }

    @Log(value = "仪器验收申请查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/equipt/equip-checks/emc-equip-check-search-list";
    }

    @Log(value = "仪器外观描述页", type = LogType.CONTROLLER)
    @Override
    public String appearanceEditListPage() {
        return "emc/module/mdm/equipt/equip-checks/emc-equip-appearance-edit-list";
    }

    @Log(value = "仪器外观验收页", type = LogType.CONTROLLER)
    @Override
    public String appearanceChooseListPage() {
        return "emc/module/mdm/equipt/equip-checks/emc-equip-appearance-choose-list";
    }
}
