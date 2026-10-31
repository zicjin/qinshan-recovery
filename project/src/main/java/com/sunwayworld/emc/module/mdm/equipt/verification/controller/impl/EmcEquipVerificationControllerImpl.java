package com.sunwayworld.emc.module.mdm.equipt.verification.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.verification.controller.EmcEquipVerificationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquipVerificationControllerImpl implements EmcEquipVerificationController {

    @Log(value = "检定结果确认记录表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equip-verifications/emc-equip-verification-edit-list";
    }

    @Log(value = "检定结果确认记录表审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/mdm/equipt/equip-verifications/emc-equip-verification-audit-list";
    }

    @Log(value = "检定结果确认记录表查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/equipt/equip-verifications/emc-equip-verification-search-list";
    }

}
