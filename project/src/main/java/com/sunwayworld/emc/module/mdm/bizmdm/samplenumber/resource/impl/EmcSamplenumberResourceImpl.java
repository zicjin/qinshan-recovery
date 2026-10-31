package com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.resource.EmcSamplenumberResource;
import com.sunwayworld.emc.module.mdm.bizmdm.samplenumber.service.EmcSamplenumberService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("采样单编码信息")
@RestController
@GikamBean
public class EmcSamplenumberResourceImpl implements EmcSamplenumberResource {

    @Autowired
    private EmcSamplenumberService emcSamplenumberService;

    @Override
    public EmcSamplenumberService getService() {
        return emcSamplenumberService;
    }

    @Log(value = "新增采样单编码信息", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
