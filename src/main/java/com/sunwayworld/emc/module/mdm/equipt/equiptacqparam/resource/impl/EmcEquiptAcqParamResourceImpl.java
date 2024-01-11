package com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.resource.EmcEquiptAcqParamResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.EmcEquiptAcqParamService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "设备台账-仪器采集参数配置")
@RestController
@GikamBean
public class EmcEquiptAcqParamResourceImpl implements EmcEquiptAcqParamResource {

    @Autowired
    private EmcEquiptAcqParamService equiptAcqParamService;

    @Override
    public EmcEquiptAcqParamService getService() {
        return equiptAcqParamService;
    }

}
