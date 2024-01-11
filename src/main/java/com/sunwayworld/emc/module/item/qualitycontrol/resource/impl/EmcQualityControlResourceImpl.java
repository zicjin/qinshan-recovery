package com.sunwayworld.emc.module.item.qualitycontrol.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.qualitycontrol.resource.EmcQualityControlResource;
import com.sunwayworld.emc.module.item.qualitycontrol.service.EmcQualityControlService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-12-04
 */
@LogModule("质量控制计划")
@RestController
@GikamBean
public class EmcQualityControlResourceImpl implements EmcQualityControlResource {
    @Autowired
    private EmcQualityControlService qualityControlService;
    @Override
    public EmcQualityControlService getService() {
        return qualityControlService;
    }
    
    @Log(value = "新增质量控制计划", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
    
}
