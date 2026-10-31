package com.sunwayworld.emc.module.mdm.basemdm.envrecord.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.controller.EmcEnvRecordController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEnvRecordControllerImpl implements EmcEnvRecordController {

    @Override
    @Log(value = "环境记录编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/env-records/emc-env-record-edit-list";
    }

    @Override
    @Log(value = "环境记录详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/basemdm/env-records/emc-env-record-detail";
    }

    @Override
    @Log(value = "环境记录选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/basemdm/env-records/emc-env-record-choose-list";
    }
}
