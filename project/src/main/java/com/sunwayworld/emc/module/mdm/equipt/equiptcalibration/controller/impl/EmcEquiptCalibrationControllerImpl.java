package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.controller.EmcEquiptCalibrationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@Controller
@GikamBean
public class EmcEquiptCalibrationControllerImpl implements EmcEquiptCalibrationController {

    @Override
    @Log(value = "仪器校准编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/equipt/equipt-calibrations/emc-equipt-calibration-edit-list";
    }

    @Override
    @Log(value = "仪器校准详情列表页面", type = LogType.CONTROLLER)
    public String detailListPage() {
        return "emc/module/mdm/equipt/equipt-calibrations/emc-equipt-calibration-detail-list";
    }
}
