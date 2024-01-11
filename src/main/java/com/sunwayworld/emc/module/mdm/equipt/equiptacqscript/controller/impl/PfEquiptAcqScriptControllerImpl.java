package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.controller.PfEquiptAcqScriptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年09月08日
 */
@GikamBean
@Controller
public class PfEquiptAcqScriptControllerImpl extends EmcEquiptAcqScriptControllerImpl implements PfEquiptAcqScriptController {

    @Override
    @Log(value = "采集结果参数选择页面", type = LogType.CONTROLLER)
    public String easResultChoosePage() {
        return "emc/module/mdm/equipt/equipt-acq-scripts/emc-equipt-eas-result-choose-list";
    }
}
