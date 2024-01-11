package com.sunwayworld.emc.module.bp.result.equiptcurve.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.controller.EmcEquiptCurveController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcEquiptCurveControllerImpl implements EmcEquiptCurveController {

    @Log(value = "仪器曲线表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/result/equipt-curves/emc-equipt-curve-edit-list";
    }

    @Log(value = "仪器曲线表选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/result/equipt-curves/emc-equipt-curve-choose-list";
    }

    @Log(value = "浏览仪器曲线页", type = LogType.CONTROLLER)
    @Override
    public String viewCurvePage() {
        return "emc/module/bp/result/equipt-curves/emc-equipt-view-curve-history";
    }

    @Log(value = "仪器曲线结果录入页", type = LogType.CONTROLLER)
    @Override
    public String resultInputPage() {
        return "emc/module/bp/result/equipt-curves/emc-equipt-data-result-input";
    }

}
