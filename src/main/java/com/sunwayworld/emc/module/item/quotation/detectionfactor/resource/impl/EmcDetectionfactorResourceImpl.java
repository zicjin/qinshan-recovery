package com.sunwayworld.emc.module.item.quotation.detectionfactor.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.quotation.detectionfactor.resource.EmcDetectionfactorResource;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.service.EmcDetectionfactorService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("检测因子价格管理表")
@RestController
@GikamBean
public class EmcDetectionfactorResourceImpl implements EmcDetectionfactorResource {

    @Autowired
    private EmcDetectionfactorService emcDetectionfactorService;

    @Override
    public EmcDetectionfactorService getService() {
        return emcDetectionfactorService;
    }

    @Log(value = "新增检测因子价格管理表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @RequestMapping(value = "/action/insert-by-item-group", method = RequestMethod.POST)
    public Long insertByItemGroup(RestJsonWrapperBean wrapper) {
        return getService().insertByItemGroup(wrapper);
    }

}
