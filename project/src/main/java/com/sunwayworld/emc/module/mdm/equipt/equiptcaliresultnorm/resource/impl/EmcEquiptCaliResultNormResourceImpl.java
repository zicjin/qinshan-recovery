package com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.resource.EmcEquiptCaliResultNormResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.service.EmcEquiptCaliResultNormService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("测试方法中间表")
@RestController
@GikamBean
public class EmcEquiptCaliResultNormResourceImpl implements EmcEquiptCaliResultNormResource {

    @Autowired
    private EmcEquiptCaliResultNormService emcEquiptCaliResultNormService;

    @Override
    public EmcEquiptCaliResultNormService getService() {
        return emcEquiptCaliResultNormService;
    }

    @Log(value = "新增测试方法中间表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
