package com.sunwayworld.emc.module.bp.result.curve.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.result.curve.controller.EmcEquiptCurveConfigController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptCurveConfigControllerImpl implements EmcEquiptCurveConfigController {

    @Log(value = "仪器曲线配置编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/result/equipt-curve-configs/emc-equipt-curve-config-edit-list";
    }

}
