package com.sunwayworld.emc.module.item.equiptassayplan.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.equiptassayplan.resource.EmcEquiptAssayPlanResource;
import com.sunwayworld.emc.module.item.equiptassayplan.service.EmcEquiptAssayPlanService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("仪器检定校准计划")
@RestController
@GikamBean
public class EmcEquiptAssayPlanResourceImpl implements EmcEquiptAssayPlanResource {

    @Autowired
    private EmcEquiptAssayPlanService emcEquiptAssayPlanService;

    @Override
    public EmcEquiptAssayPlanService getService() {
        return emcEquiptAssayPlanService;
    }

    @Log(value = "新增仪器检定校准计划", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
