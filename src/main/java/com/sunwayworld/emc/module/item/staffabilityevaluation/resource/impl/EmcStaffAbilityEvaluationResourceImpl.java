package com.sunwayworld.emc.module.item.staffabilityevaluation.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.staffabilityevaluation.resource.EmcStaffAbilityEvaluationResource;
import com.sunwayworld.emc.module.item.staffabilityevaluation.service.EmcStaffAbilityEvaluationService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("人员能力评估")
@RestController
@GikamBean
public class EmcStaffAbilityEvaluationResourceImpl implements EmcStaffAbilityEvaluationResource {

    @Autowired
    private EmcStaffAbilityEvaluationService emcStaffAbilityEvaluationService;

    @Override
    public EmcStaffAbilityEvaluationService getService() {
        return emcStaffAbilityEvaluationService;
    }

    @Log(value = "新增人员能力评估", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
