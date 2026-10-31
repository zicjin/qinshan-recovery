package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.controller.EmcEquiptAcqScriptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptAcqScriptControllerImpl implements EmcEquiptAcqScriptController {

    @Log(value = "仪器采集脚本配置编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-acq-scripts/emc-equipt-acq-script-edit-list";
    }

    @Log(value = "仪器采集脚本选择查询页面跳转", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/equipt/equipt-acq-scripts/emc-equipt-acq-script-choose-list";
    }

    @Log(value = "展示测试文件采集结果的页面", type = LogType.CONTROLLER)
    @Override
    public String analyResultPage() {
        return "emc/module/mdm/equipt/equipt-acq-scripts/emc-equipt-acq-script-analy-result-list";
    }

}
