package com.sunwayworld.emc.module.bp.sample.samplingplan.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.sample.samplingplan.controller.EmcSamplingPlanController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magt@sunwayworld.com 2019年10月12日
 */
@Controller
@GikamBean
public class EmcSamplingPlanControllerImpl implements EmcSamplingPlanController {

    @Log(value = "采样安排列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-edit-list";
    }

    @Override
    @Log(value = "采样信息页", type = LogType.CONTROLLER)
    @RequestMapping("/sample-info")
    public String sampleInfoPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-sample-info";
    }

    @Override
    @Log(value = "监测点属性信息页", type = LogType.CONTROLLER)
    @RequestMapping("/monitor-property")
    public String monitorPropertyPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-monitor-property";
    }

    @Override
    @Log(value = "采样安排查询页", type = LogType.CONTROLLER)
    @RequestMapping("/query-list")
    public String samplePlanQueryPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-query-list";
    }

    @Override
    @Log(value = "容器修改列表页", type = LogType.CONTROLLER)
    @RequestMapping("/container-list")
    public String updateContainerPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-container-list";
    }
}
