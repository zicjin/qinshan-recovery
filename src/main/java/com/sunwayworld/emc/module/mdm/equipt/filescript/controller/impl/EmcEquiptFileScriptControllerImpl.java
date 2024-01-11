package com.sunwayworld.emc.module.mdm.equipt.filescript.controller.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.filescript.controller.EmcEquiptFileScriptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptFileScriptControllerImpl implements EmcEquiptFileScriptController {
    @Override
    @Log(value = "设备脚本列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/equipt/file-scripts/emc-equipt-file-script-edit-list";
    }

    @Override
    @Log(value = "设备脚本详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/equipt/file-scripts/emc-equipt-file-script-detail";
    }

    @Log(value = "设备脚本选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/equipt/file-scripts/emc-equipt-file-script-choose-list";
    }
}