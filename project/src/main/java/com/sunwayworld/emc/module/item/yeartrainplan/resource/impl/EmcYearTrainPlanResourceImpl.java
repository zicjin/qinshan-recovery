package com.sunwayworld.emc.module.item.yeartrainplan.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.yeartrainplan.resource.EmcYearTrainPlanResource;
import com.sunwayworld.emc.module.item.yeartrainplan.service.EmcYearTrainPlanService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("年度计划培训")
@RestController
@GikamBean
public class EmcYearTrainPlanResourceImpl implements EmcYearTrainPlanResource {

    @Autowired
    private EmcYearTrainPlanService emcYearTrainPlanService;

    @Override
    public EmcYearTrainPlanService getService() {
        return emcYearTrainPlanService;
    }

    @Log(value = "新增年度计划培训", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
