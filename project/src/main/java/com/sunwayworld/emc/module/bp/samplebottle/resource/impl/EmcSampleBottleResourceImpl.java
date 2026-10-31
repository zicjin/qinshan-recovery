package com.sunwayworld.emc.module.bp.samplebottle.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.samplebottle.resource.EmcSampleBottleResource;
import com.sunwayworld.emc.module.bp.samplebottle.service.EmcSampleBottleService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("采样瓶表")
@RestController
@GikamBean
public class EmcSampleBottleResourceImpl implements EmcSampleBottleResource {

    @Autowired
    private EmcSampleBottleService emcSampleBottleService;

    @Override
    public EmcSampleBottleService getService() {
        return emcSampleBottleService;
    }

    @Log(value = "新增采样瓶表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "根据瓶数量增加采样瓶空瓶", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/add-sample-bottle", method = RequestMethod.POST)
    public String addSampleBottle(RestJsonWrapperBean wrapper) {
        return emcSampleBottleService.addSampleBottle(wrapper);
    }

}
