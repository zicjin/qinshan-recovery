package com.sunwayworld.emc.module.bp.sample.sample.controller.impl;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.sample.sample.controller.PfSamplingPlanController;
import com.sunwayworld.emc.module.bp.sample.samplingplan.controller.impl.EmcSamplingPlanControllerImpl;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021年8月27日
 */
@Controller
@GikamBean
public class PfSamplingPlanControllerImpl extends EmcSamplingPlanControllerImpl implements PfSamplingPlanController {


    @Override
    @Log(value = "车辆信息页", type = LogType.CONTROLLER)
    @RequestMapping("/car-info")
    public String carInfoPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-car-info-choose-list";
    }

    @Override
    @Log(value = "新增VOC页", type = LogType.CONTROLLER)
    @RequestMapping("/add-voc")
    public String addVocPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-add-voc";
    }
    
    @Override
    @Log(value = "VOC详情页", type = LogType.CONTROLLER)
    @RequestMapping("/voc-page")
    public String vocPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-voc-page";
    }
    
    @Override
    @Log(value = "VOC批录入页", type = LogType.CONTROLLER)
    @RequestMapping("/batch-update-voc")
    public String batchUpdateVocPage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-batch-update-voc-page";
    }

    @Override
    @Log(value = "采样瓶详情页", type = LogType.CONTROLLER)
    @RequestMapping("/sample-bottle-page")
    public String sampleBottlePage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-sample-bottle-page";
    }

    @Override
    @Log(value = "新增采样瓶页", type = LogType.CONTROLLER)
    @RequestMapping("/add-sample-bottle")
    public String addSampleBottlePage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-add-sample-bottle";
    }

    @Override
    @Log(value = "采样瓶瓶号", type = LogType.CONTROLLER)
    @RequestMapping("/sample-bottle")
    public String sampleBottleChoosePage() {
        return "emc/module/bp/sample/sampling-plans/emc-sampling-plan-sample-bottle-choose-list";
    }

}
