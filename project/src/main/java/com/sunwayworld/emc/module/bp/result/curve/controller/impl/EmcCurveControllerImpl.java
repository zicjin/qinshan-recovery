package com.sunwayworld.emc.module.bp.result.curve.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.result.curve.controller.EmcCurveController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */

@Controller
@GikamBean
public class EmcCurveControllerImpl implements EmcCurveController {

    @Log(value = "曲线表选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/result/curves/emc-curve-choose-list";
    }

    @Log(value = "选择分析项和QC类型页", type = LogType.CONTROLLER)
    @Override
    public String selectItemQcPage() {
        return "emc/module/bp/result/curves/emc-select-item-qc";
    }

    @Log(value = "结果录入页", type = LogType.CONTROLLER)
    @Override
    public String resultInputPage() {
        return "emc/module/bp/result/curves/emc-data-result-input";
    }

    @Log(value = "浏览校准曲线页", type = LogType.CONTROLLER)
    @Override
    public String viewCurvePage() {
        return "emc/module/bp/result/curves/emc-view-curve-history";
    }

}
